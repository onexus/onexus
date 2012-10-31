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
package org.onexus.ui.website.utils;

import org.onexus.collection.api.query.EqualId;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;

public class SingleEntityQuery extends Query {

    public final static String COLLECTION_ALIAS = "c";

    public SingleEntityQuery(ORI collectionURI, String entityId) {
        super();

        addDefine(COLLECTION_ALIAS, collectionURI);
        addSelect(COLLECTION_ALIAS, null);
        setFrom(COLLECTION_ALIAS);
        setWhere(new EqualId(COLLECTION_ALIAS, entityId));

    }

}
