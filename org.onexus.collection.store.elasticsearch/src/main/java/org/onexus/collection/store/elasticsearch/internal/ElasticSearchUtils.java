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
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.ORI;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ElasticSearchUtils {

    public static final String PRIMARY_KEY_SEPARATOR = "\t";
    public static final String ENTITY_TYPE = "entity";

    public static boolean exists( Client client, String indexName) {
        return client
                .admin()
                .indices()
                .exists(new IndicesExistsRequest(indexName))
                .actionGet()
                .isExists();
    }

    public static boolean drop( Client client, String indexName) {
        return client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet().isAcknowledged();
    }

    public static IndexResponse insertEntity(Client client, String indexName, IEntity entity, List<EmbeddedLink> links) throws IOException {

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

        return index.setSource(builder)
                .execute()
                .actionGet();

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

    public static LoadingCache<ORI, String> newOriToIndexNameCache(int maxSize) {
        return CacheBuilder.newBuilder().maximumSize(maxSize).build(new CacheLoader<ORI, String>() {
            @Override
            public String load(ORI key) throws Exception {
                return convertOriToIndexName(key);
            }
        });
    };

    public static String convertOriToIndexName(ORI ori) {
        String hashCode = Integer.toHexString(ori.getProjectUrl().hashCode());
        String indexName = removeNonValidChars(ori.getPath());

        // Check that the index name is no longer than 64 characters
        int totalLength = indexName.length() + hashCode.length() + 1;
        if (totalLength > 64) {
            return indexName.substring(0, indexName.length()
                    - (totalLength - 64))
                    + "_" + hashCode;
        } else {
            return hashCode + "_" + indexName;
        }
    }

    public static String removeNonValidChars(String id) {
        return id.toLowerCase().trim().replaceAll("[^a-z0-9]", "_");
    }

    public static final String ONEXUS_FOLDER = System.getProperty("user.home") + File.separator + ".onexus";
    public static final String ES_FOLDER = ONEXUS_FOLDER + File.separator + "elasticsearch";

    public static Settings buildNodeSettings() {

        // Create folder if it's the first run
        File esFolder = new File(ES_FOLDER);
        if (!esFolder.exists()) {
            esFolder.mkdirs();
        }

        // Build settings
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "onexus" )
                .put("node.name", "node-" + NetworkUtils.getLocalAddress().getHostName())
                .put("path.home", ES_FOLDER);

        return builder.build();
    }

    public static Node buildNode() {
        return NodeBuilder.nodeBuilder().settings(buildNodeSettings()).node();
    }

}

