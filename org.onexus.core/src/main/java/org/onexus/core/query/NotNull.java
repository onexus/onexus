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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NotNull extends Filter {

    private String[] cellCollections;

    public NotNull() {
        this(null, new String[0]);
    }

    public NotNull(String axisCollection, String... collectionIds) {
        super(axisCollection);
        this.cellCollections = collectionIds;
    }

    public String[] getCellCollections() {
        return cellCollections;
    }

    @Override
    public Set<String> getDependentCollections() {
        Set<String> dependencies = super.getDependentCollections();
        dependencies.addAll(Arrays.asList(cellCollections));
        return dependencies;
    }
}
