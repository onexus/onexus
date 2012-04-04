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
package org.onexus.core.query;

import java.io.Serializable;
import java.util.*;

public class Query implements Serializable {

    // Namespace
    private String mainNamespace;

    // Collections
    private String mainCollection;
    private List<String> collections = new ArrayList<String>();

    // Filters
    private Set<FixedEntity> fixedEntities = new HashSet<FixedEntity>();
    private Map<String, Set<Filter>> filters = new HashMap<String, Set<Filter>>();

    // Order
    private Order order;

    // Limit
    private long firstResult = 0;
    private long maxResults = Long.MAX_VALUE;

    public Query() {
        super();
    }

    public Query(String mainCollection) {
        super();
        this.mainCollection = mainCollection;
        this.collections.add(mainCollection);
    }

    public Set<FixedEntity> getFixedEntities() {
        return fixedEntities;
    }

    public Set<String> getFilterKeys() {
        return filters.keySet();
    }

    public Set<Filter> getFilters(String filterKey) {
        return filters.get(filterKey);
    }

    public List<String> getCollections() {
        return collections;
    }

    public String getMainCollection() {
        return mainCollection;
    }

    public void setMainCollection(String mainCollection) {
        this.mainCollection = mainCollection;
    }

    public String getMainNamespace() {
        return mainNamespace;
    }

    public void setMainNamespace(String mainNamespace) {
        this.mainNamespace = mainNamespace;
    }

    public long getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(long firstResult) {
        this.firstResult = firstResult;
    }

    public long getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(long maxResults) {
        this.maxResults = maxResults;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void putFilter(String id, Filter rule) {
        if (!filters.containsKey(id)) {
            filters.put(id, new HashSet<Filter>());
        }
        filters.get(id).add(rule);
    }

}
