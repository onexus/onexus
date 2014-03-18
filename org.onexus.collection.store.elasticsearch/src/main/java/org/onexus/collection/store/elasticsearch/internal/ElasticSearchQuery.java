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

import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import static org.onexus.collection.store.elasticsearch.internal.ElasticSearchUtils.convertOriToIndexName;
import static org.onexus.collection.store.elasticsearch.internal.filters.FilterAdapterFactory.filterAdapter;

public class ElasticSearchQuery {

    private IResourceManager resourceManager;
    private Client client;
    private Query query;

    private SearchRequestBuilder searchRequest;
    private String fromIndexName;
    private ORI fromORI;

    public ElasticSearchQuery(IResourceManager resourceManager, Client client, Query query) {
        super();

        this.resourceManager = resourceManager;
        this.query = query;
        this.client = client;

        build();
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

        // Flush index
        client.admin().indices().flush(new FlushRequest(fromIndexName)).actionGet();

    }

    private void where() {

        Filter filter = query.getWhere();
        if (filter != null) {
            searchRequest.setFilter(filterAdapter(filter).build(resourceManager, query, filter));
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

    private String convertAliasToIndexName(String alias) {
        ORI ori = query.getDefine().get(alias);
        ori = ori.toAbsolute( query.getOn() );
        return convertOriToIndexName(ori);
    }

    public ORI getFrom() {

        if (fromORI == null) {
            fromORI = query.getDefine().get( query.getFrom()).toAbsolute( query.getOn() );
        }

        return fromORI;
    }

    public Query getOnexusQuery() {
        return query;
    }

    public SearchRequestBuilder getSearchRequest() {
        return searchRequest;
    }
}
