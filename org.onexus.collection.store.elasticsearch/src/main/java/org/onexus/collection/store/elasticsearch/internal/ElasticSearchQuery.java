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

import com.google.common.base.Joiner;
import com.google.common.cache.LoadingCache;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.sort.SortOrder;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Link;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.refreshIndex;
import static org.onexus.collection.store.elasticsearch.internal.filters.FilterAdapterFactory.filterAdapter;

public class ElasticSearchQuery {

    private static final Joiner PATH_JOINER = Joiner.on(".");

    private LoadingCache<ORI, String> indexNameCache;
    private IResourceManager resourceManager;
    private Client client;
    private Query query;

    private SearchRequestBuilder searchRequest;
    private String fromIndexName;
    private ORI fromORI;
    private Map<ORI, List<String>> collectionPathList;
    private Map<ORI, String> collectionPath;

    public ElasticSearchQuery(IResourceManager resourceManager, LoadingCache<ORI, String> indexNameCache, Client client, Query query) {
        super();

        this.indexNameCache = indexNameCache;
        this.resourceManager = resourceManager;
        this.query = query;
        this.client = client;

        // Create all collections paths. Using the links.
        this.collectionPathList = new HashMap<ORI, List<String>>();
        this.collectionPath = new HashMap<ORI, String>();
        buildPath(getFrom(), new ArrayList<String>(0));

        // Build the query
        build();

    }

    private void buildPath(ORI collectionOri, List<String> prefix) {

        if (collectionPathList.containsKey(collectionOri)) {

            List<String> previousPrefix = collectionPathList.get(collectionOri);

            // If there are to routes to the same collection
            // we prefer the shorter one.
            if (previousPrefix.size() < prefix.size()) {
                return;
            }

        }

        this.collectionPathList.put(collectionOri, prefix);
        this.collectionPath.put(collectionOri, PATH_JOINER.join(prefix));

        Collection collection = resourceManager.load(Collection.class, collectionOri);
        if (collection.getLinks() != null) {
            for (Link link : collection.getLinks()) {
                ORI linkOri = link.getCollection().toAbsolute(collectionOri);
                List<String> linkPrefix = new ArrayList<String>(prefix.size() + 1);
                linkPrefix.addAll(prefix);
                linkPrefix.add(indexNameCache.getUnchecked(linkOri));
                buildPath(linkOri, linkPrefix);
            }
        }

    }

    public boolean isLinked(ORI collection) {
        return collectionPathList.containsKey(collection);
    }

    public List<String> getPath(ORI collection) {
        return collectionPathList.get(collection);
    }

    private void build() {
        from();
        where();
        order();
        limit();
    }

    private void from() {

        // Form collection
        fromIndexName = convertAliasToIndexName(query.getFrom());

        // Create request
        searchRequest = client.prepareSearch( fromIndexName );
        searchRequest.setTypes("entity");
        searchRequest.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.setExplain(false);

    }

    private void where() {

        Filter filter = query.getWhere();
        if (filter != null) {
            searchRequest.setPostFilter(filterAdapter(filter).build(this, filter));
        }

    }

    private void order() {
        for (OrderBy order : query.getOrders()) {
            String indexName = convertAliasToIndexName(order.getCollection());
            searchRequest.addSort(convertToField(indexName, order.getField()), (order.isAscendent() ? SortOrder.ASC : SortOrder.DESC));
        }
    }

    private void limit() {

        if (query.getOffset() != null) {
            searchRequest.setFrom(query.getOffset().intValue());
        }

        if (query.getCount() != null) {
            searchRequest.setSize(query.getCount().intValue());
        }

    }

    private String convertToField(String indexName, String fieldId) {
        if (fromIndexName.equals(indexName)) {
            return fieldId;
        }
        return indexName + "." + fieldId;
    }

    public String convertAliasToIndexName(String alias) {
        return indexNameCache.getUnchecked(convertAliasToAbsoluteORI(alias));
    }

    public ORI convertAliasToAbsoluteORI(String alias) {
        ORI ori = query.getDefine().get(alias);

        if (query.getOn() != null) {
            ori = ori.toAbsolute(query.getOn());
        }

        return ori;
    }

    public ORI getFrom() {

        if (fromORI == null) {
            fromORI = query.getDefine().get( query.getFrom()).toAbsolute( query.getOn() );
        }

        return fromORI;
    }

    public String fieldPath(String collectionAlias, String fieldId) {

        if (collectionAlias.equals(query.getFrom())) {
            return fieldId;
        }

        return indexPath(collectionAlias) + "." + fieldId;
    }

    public String indexPath(String collectionAlias) {
        ORI ori = query.getDefine().get(collectionAlias).toAbsolute(query.getOn());
        return collectionPath.get(ori);
    }

    public Query getOnexusQuery() {
        return query;
    }

    public SearchRequestBuilder getSearchRequest() {

        refreshIndex(client, fromIndexName);

        return searchRequest;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public boolean isFromCollection(String collectionAlias) {
        return query.getFrom().equals(collectionAlias);
    }
}
