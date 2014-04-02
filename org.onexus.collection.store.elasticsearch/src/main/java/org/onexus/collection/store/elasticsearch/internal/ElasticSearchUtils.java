/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.collection.store.elasticsearch.internal;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.types.Text;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchUtils {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchUtils.class);
    public static final String PRIMARY_KEY_SEPARATOR = "\t";
    public static final String ENTITY_TYPE = "entity";

    public static final Map<Class<?>, String> FIELD_DATA_TYPE = new HashMap<Class<?>, String>();
    static {
        FIELD_DATA_TYPE.put(String.class, "string");
        FIELD_DATA_TYPE.put(Text.class, "string");
        FIELD_DATA_TYPE.put(Boolean.class, "boolean");
        FIELD_DATA_TYPE.put(Date.class, "date");
        FIELD_DATA_TYPE.put(Integer.class, "integer");
        FIELD_DATA_TYPE.put(Long.class, "long");
        FIELD_DATA_TYPE.put(Double.class, "double");
    }

    public static boolean exists( Client client, String indexName) {
        return client
                .admin()
                .indices()
                .exists(new IndicesExistsRequest(indexName))
                .actionGet()
                .isExists();
    }

    public static boolean drop( Client client, String indexName) {

        // Delete index
        return client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet().isAcknowledged();

    }

    public static IndexRequestBuilder insertEntity(Client client, String indexName, IEntity entity, List<EmbeddedLink> links) throws IOException {

        // Builder
        XContentBuilder builder = XContentFactory.smileBuilder();

        // Load entity into the builder
        builder.startObject();

        // Insert entity values
        for (Field field : entity.getCollection().getFields()) {
            String fieldId = field.getId();
            builder.field(fieldId, entity.get(fieldId));
        }

        // Insert linked entities
        for (EmbeddedLink link : links) {
            GetResponse response = client.prepareGet(link.getIndexName(), ENTITY_TYPE, link.buildKey(entity)).execute().actionGet();
            builder.field(link.getIndexName(), response.getSource());
        }

        builder.endObject();

        // Index the entity
        String key = buildKey(entity);

        IndexRequestBuilder index;
        if ( Strings.isNullOrEmpty(key) ) {
            index = client.prepareIndex(indexName, ENTITY_TYPE);
        } else {
            index = client.prepareIndex(indexName, ENTITY_TYPE, buildKey(entity));
        }

        return index.setSource(builder);

    }

    public static void createIndex(Client client, String indexName, Collection collection, List<EmbeddedLink> links) {

        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder().startObject()
                    .startObject(ENTITY_TYPE)
                        .startObject("_all").field("enabled", false).endObject()
                        .field("dynamic", false)
                        .startObject("properties");

            mapFields(mapping, collection.getFields());

            for (EmbeddedLink link : links) {
                mapLink(mapping, link);
            }

            mapping.endObject().endObject().endObject();
        }
        catch (IOException e) {
            log.error("Error creating manual index mapping", e);
        }

        CreateIndexRequest request = new CreateIndexRequest(indexName);
        if (mapping != null) {
            request.mapping(ENTITY_TYPE, mapping);
        }

        client.admin().indices().create(request).actionGet();

    }

    private static void mapLink(XContentBuilder mapping, EmbeddedLink link) throws IOException {
        mapping.startObject(link.getIndexName())
                .startObject("_all").field("enabled", false).endObject()
                .field("dynamic", false)
                .startObject("properties");

        mapFields(mapping, link.getToCollection().getFields());

        for (EmbeddedLink toLink : link.getToLinks()) {
            mapLink(mapping, toLink);
        }

        mapping.endObject();
        mapping.endObject();
    }

    private static void mapFields(XContentBuilder mapping, Iterable<Field> fields) throws IOException {
        for(Field field : fields) {

            String type = FIELD_DATA_TYPE.get(field.getType());
            String index = "string".equals(type) ? "analyzed" : "not_analyzed";

            mapping.startObject(field.getId())
                    .field("type", type)
                    .field("index", index)
                    .endObject();
        }
    }

    public static void refreshIndex(Client client, String indexName) {
        client.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
    }

    public static String buildKey(IEntity entity) {

        StringBuilder id = new StringBuilder();
        boolean first = true;
        for (Field field : entity.getCollection().getFields()) {
            if (Boolean.TRUE.equals(field.isPrimaryKey())) {
                if (!first) {
                    id.append(PRIMARY_KEY_SEPARATOR);
                } else {
                    first = false;
                }
                id.append(entity.get(field.getId()));
            }
        }
        return id.toString();
    }

    public static LoadingCache<ORI, String> newOriToIndexNameCache(final IResourceManager resourceManager, int maxSize) {
        return CacheBuilder.newBuilder().maximumSize(maxSize).build(new CacheLoader<ORI, String>() {
            @Override
            public String load(ORI key) throws Exception {
                return convertOriToIndexName(resourceManager, key);
            }
        });
    };

    public static String convertOriToIndexName(IResourceManager resourceManager, ORI ori) {

        Project project = resourceManager.getProject(ori.getProjectUrl());
        String projectName = removeNonValidChars(project.getName());
        String indexName = removeNonValidChars(ori.getPath());

        return projectName + "_" + indexName;
    }

    public static String removeNonValidChars(String id) {
        return id.toLowerCase().trim().replaceAll("[^a-z0-9]", "_");
    }

    public static Settings buildNodeSettings(String esHome) {

        // Create folder if it's the first run
        File esFolder = new File(esHome);
        if (!esFolder.exists()) {
            esFolder.mkdirs();
        }

        // Build settings
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "onexus" )
                .put("node.name", "node-" + NetworkUtils.getLocalAddress().getHostName())
                .put("path.home", esHome);

        return builder.build();
    }

    public static Node buildNode(String esHome) {
        return NodeBuilder.nodeBuilder().settings(buildNodeSettings(esHome)).node();
    }

}

