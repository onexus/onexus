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

import org.onexus.resource.api.ORI;

import java.util.List;
import java.util.Map;

/**
 * A IQueryParser can parse a string representation of an OQL
 * query and return it's java objects representation.
 */
public interface IQueryParser {

    /**
     * Parses a full OQL query.
     *
     * @param expression The <code>String</code> representation of the query.
     * @return A <code>Query</code> object for the given 'expression'. Null if it's not
     * possible to parse the query.
     *
     */
    Query parseQuery(String expression);

    /**
     * Parses a DEFINE section of a OQL query.
     *
     * @param expression The <code>String</code> representation of the define section.
     * @return A map that maps collection alias to it's collection ORI. Null if it's not
     * possible to parse the 'expression'.
     */
    Map<String, ORI> parseDefine(String expression);

    /**
     * Parses a SELECT section of a OQL query.
     *
     * @param expression The <code>String</code> representation of the SELECT section.
     * @return A map that maps collection alias to a set of collection field ids. Null if it's not
     * possible to parse the 'expression'.
     */
    Map<String, List<String>> parserSelect(String expression);

    /**
     * Parses a WHERE section of a OQL query.
     *
     * @param expression The <code>String</code> representation of the WHERE section.
     * @return A filter that repressent the given 'expression'. Null if it's not
     * possible to parse the 'expression'
     */
    Filter parseWhere(String expression);

    /**
     * Parses a ORDER BY section of a OQL query.
     *
     * @param expression The <code>String</code> representation of the ORDER BY section.
     * @return A list of <code>OrderBy</code> objects that represents the given 'expression'.
     * Null if it's not possible to parse the 'expression'
     */
    List<OrderBy> parseOrder(String expression);

}
