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


import java.io.Serializable;

/**
 * Filter serves as the highest level abstract base class for all OQL
 * where filters.
 */
public abstract class Filter implements Serializable {

    /**
     * Keep this constructor for JAXB compatibility.
     */
    public Filter() {
        super();
    }

    /**
     * Converts an object to a valid OQL string representation.
     *
     * @param value The input object value
     * @return A valid OQL string representation
     */
    protected static String convertToOQL(final Object value) {
        return Query.escapeString(value.toString());
    }

    /**
     * Returns a <code>String</code> with as many tab separators as the last 'oql' line.
     *
     * @param oql A <code>StringBuilder</code> with the previous OQL code.
     * @return A <code>String</code> with the same number of tab characters as the last line.
     */
    protected static String endingTabs(final StringBuilder oql) {
        StringBuilder prevTabs = new StringBuilder();
        int l = oql.length();
        for (int i = l - 1; oql.charAt(i) == '\t' && i > 0; i--) {
            prevTabs.append('\t');
        }

        return prevTabs.toString();
    }

    /**
     * @return A OQL valid string representation for this filter.
     */
    public final String toString() {
        return toString(new StringBuilder(), true).toString();
    }

    /**
     * Returns the input 'oql' StringBuilder after append this filter OQL string.
     *
     * @param oql         A StringBuilder to append the OQL
     * @param prettyPrint If true then add tabs and new line characters to format the OQL query.
     * @return The OQL query
     */
    public abstract StringBuilder toString(final StringBuilder oql, final boolean prettyPrint);

}
