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
package org.onexus.collection.api.query;

/**
 * A logical filter that negates the given filter.
 */
public class Not extends Filter {

    private Filter negatedFilter;

    @SuppressWarnings("UnusedDeclaration")
    public Not() {
        // Keep this constructor for JAXB compatibility
    }

    /**
     * Create a NOT filter.
     *
     * @param negatedFilter The filter to negate.
     */
    public Not(Filter negatedFilter) {
        super();
        this.negatedFilter = negatedFilter;
    }

    /**
     * Get the filter that this filter negates.
     *
     * @return The negated filter.
     */
    public Filter getNegatedFilter() {
        return negatedFilter;
    }

    @Override
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {

        boolean binaryFilter = prettyPrint && (negatedFilter instanceof BinaryFilter);
        String prevTabs = binaryFilter ? endingTabs(oql) : "";

        oql.append("NOT");
        oql.append(binaryFilter ? "\n" + prevTabs : " ");

        if (negatedFilter != null) {
            negatedFilter.toString(oql, prettyPrint);
        }

        return oql;
    }
}
