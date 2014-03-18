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

import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.utils.LinkUtils;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedLink {

    private String indexName;
    private List<FieldPair> fields;

    public EmbeddedLink(String indexName, List<String> fields) {

        this.indexName = indexName;
        this.fields = new ArrayList<FieldPair>(fields.size());

        for (String field : fields) {
            this.fields.add(new FieldPair(LinkUtils.getFromFieldName(field), LinkUtils.getToFieldName(field)));
        }

    }

    public String getIndexName() {
        return indexName;
    }

    public String buildKey(IEntity entity) {

        //TODO the order of the fields can be different
        StringBuilder key = new StringBuilder();
        boolean first = true;
        for (FieldPair field : fields) {
            if (!first) {
                key.append(ElasticSearchUtils.PRIMARY_KEY_SEPARATOR);
            } else {
                first = false;
            }
            key.append(entity.get(field.from));
        }
        return key.toString();
    }

    private static class FieldPair {
        private String from;
        private String to;

        public FieldPair(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

}
