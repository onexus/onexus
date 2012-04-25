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

import org.onexus.core.query.FixedEntity;
import org.onexus.core.query.Query;
import org.onexus.core.utils.ResourceTools;

public class SingleEntityQuery extends Query {

    public SingleEntityQuery(String collectionURI, String entityId) {
        super(collectionURI);
        setMainNamespace(ResourceTools.getParentURI(collectionURI));
        getFixedEntities().add(new FixedEntity(collectionURI, entityId));
    }

}
