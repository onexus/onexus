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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An OQL Query.
 * <p/>
 * <p>
 * An OQL query has six main sections:
 * <ul>
 * <li>DEFINE ... ON ...: The define section maps collection alias to a collection ORI.
 * If the collection ORI are not absolut, then the ON clause is used as a base path.</li>
 * <li>SELECT: The select section defines with fields of each collection are used.</li>
 * <li>FROM: The from section defines a single collection that is the main collection of the query
 * (all the other collections used at the select section must have a link to the main collection).</li>
 * <li>WHERE: The where section defines one or multiple filters to restrict the selected entitie.</li>
 * <li>ORDER BY: The order section defines one or multiple collection sorting directions.</li>
 * <li>LIMIT: The limit section defines an 'offset' and a maximum 'count' entities to return.</li>
 * </ul>
 * </p>
 */
public class Query implements Serializable {

    private static final String END_LINE_AND_TAB = "\n\t";

    private Map<String, ORI> define = new LinkedHashMap<String, ORI>();

    private ORI on;

    private Map<String, List<String>> select = new LinkedHashMap<String, List<String>>();

    private String from;

    private Filter where;

    private List<OrderBy> orders = new ArrayList<OrderBy>();

    private Long offset;

    private Long count;

    /**
     * Create an empty query.
     */
    public Query() {
        super();
    }

    /**
     * Remove escaped characters.
     *
     * @param value A escaped string.
     * @return A decoded string.
     */
    public static String unescapeString(String value) {

        if (value == null) {
            return null;
        }

        if (value.length() < 3) {
            return "";
        }

        char quote = value.charAt(0);

        value = value.substring(1, value.length() - 1);
        if (quote == '"') {
            value = value.replace("\\\"", "\"");
        } else {
            value = value.replace("\\'", "'");
        }

        return value;

    }

    /**
     * Escape quotes characters.
     *
     * @param value A string to escape.
     * @return The escaped string.
     */
    public static String escapeString(String value) {
        if (value == null) {
            return null;
        }
        value = "'" + value.replace("'", "\\'") + "'";
        return value;
    }

    /**
     * Gets define section.
     *
     * @return A map that maps a collection alias to it's collection ORI.
     */
    public Map<String, ORI> getDefine() {
        return define;
    }

    /**
     * Adds one collection alias definition.
     *
     * @param collectionAlias The collection alias.
     * @param collectionORI   The collection ORI.
     */
    public void addDefine(String collectionAlias, ORI collectionORI) {
        define.put(collectionAlias, collectionORI.toRelative(on));
    }

    /**
     * Base ORI that the relative define ORIs are base on.
     *
     * @return The define base ORI.
     */
    public ORI getOn() {
        return this.on;
    }

    /**
     * Sets the ORI that the relative define ORIs are base on.
     *
     * @param baseORI The base ORI for the define definitions.
     */
    public void setOn(ORI baseORI) {
        this.on = baseORI;
    }

    /**
     * Gets the select section.
     *
     * @return A map that maps a collection alias to a list of used field ids.
     */
    public Map<String, List<String>> getSelect() {
        return select;
    }

    /**
     * Adds a list of selection fields for the given collection.
     *
     * @param collectionAlias The collection alias.
     * @param fieldIds        A list of used field ids.
     */
    public void addSelect(String collectionAlias, List<String> fieldIds) {
        if (select.containsKey(collectionAlias) && select.get(collectionAlias) != null) {
            select.get(collectionAlias).addAll(fieldIds);
        } else {
            select.put(collectionAlias, fieldIds == null ? null : new ArrayList<String>(fieldIds));
        }
    }

    /**
     * Gets the main collection used as FROM.
     *
     * @return The main collection alias.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the main collection used as FROM.
     *
     * @param collectionAlias The from collection alias.
     */
    public void setFrom(String collectionAlias) {
        this.from = collectionAlias;
    }

    /**
     * Gets the query filter.
     *
     * @return The query filter.
     */
    public Filter getWhere() {
        return where;
    }

    /**
     * Sets the query filter.
     *
     * @param where The query filter.
     */
    public void setWhere(Filter where) {
        this.where = where;
    }

    /**
     * Gets the order section.
     *
     * @return A list of <code>OrderBy</code> sorting directions.
     */
    public List<OrderBy> getOrders() {
        return orders;
    }

    /**
     * Adds an <code>OrderBy</code> sorting direction.
     *
     * @param order A <code>OrderBy</code> sorting direction.
     */
    public void addOrderBy(OrderBy order) {
        this.orders.add(order);
    }

    /**
     * Gets the initial entities offset.
     *
     * @return The number of entities to skip.
     */
    public Long getOffset() {
        return offset;
    }

    /**
     * Sets the initial entities offset.
     *
     * @param offset The number of entities to skip.
     */
    public void setOffset(int offset) {
        setOffset(Long.valueOf(offset));
    }

    /**
     * Sets the initial entities offset.
     *
     * @param offset The number of entities to skip.
     */
    public void setOffset(Long offset) {
        this.offset = offset;
    }

    /**
     * Gets the total entities to return.
     *
     * @return The number of entities to return.
     */
    public Long getCount() {
        return count;
    }

    /**
     * Sets the total entities to return.
     *
     * @param count The number of entities to return.
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * Sets the total entities to return.
     *
     * @param count The number of entities to return.
     */
    public void setCount(int count) {
        setCount(Long.valueOf(count));
    }

    @Override
    public String toString() {
        return toString(new StringBuilder(), true).toString();
    }

    /**
     * Returns the input 'oql' StringBuilder after append this filter OQL string.
     *
     * @param oql         A StringBuilder to append the OQL
     * @param prettyPrint If true then add tabs and new line characters to format the OQL query.
     * @return The OQL query
     */
    public StringBuilder toString(StringBuilder oql, boolean prettyPrint) {


        // Define section
        if (define != null && !define.isEmpty()) {
            oql.append("DEFINE");

            Iterator<Map.Entry<String, ORI>> itDefine = define.entrySet().iterator();
            while (itDefine.hasNext()) {
                oql.append(prettyPrint ? END_LINE_AND_TAB : " ");

                Map.Entry<String, ORI> entry = itDefine.next();
                oql.append(entry.getKey()).append("=").append(escapeString(entry.getValue().toString()));

                if (itDefine.hasNext()) {
                    oql.append(',');
                }
            }
        }

        // On section
        if (on != null) {
            oql.append(prettyPrint ? "\n" : " ");
            oql.append("ON");
            oql.append(prettyPrint ? END_LINE_AND_TAB : " ");
            oql.append(escapeString(on.toString()));
        }


        // Select section
        if (select != null && !select.isEmpty()) {
            oql.append(prettyPrint ? "\n" : " ");
            oql.append("SELECT");
            Iterator<Map.Entry<String, List<String>>> itSelect = select.entrySet().iterator();
            while (itSelect.hasNext()) {
                oql.append(prettyPrint ? END_LINE_AND_TAB : " ");
                Map.Entry<String, List<String>> entry = itSelect.next();
                oql.append(entry.getKey());
                if (entry.getValue() != null) {
                    Iterator<String> fields = entry.getValue().iterator();
                    if (fields.hasNext()) {
                        oql.append(" (");
                        while (fields.hasNext()) {
                            oql.append(fields.next());
                            if (fields.hasNext()) {
                                oql.append(", ");
                            }
                        }
                        oql.append(")");
                    }
                }

                if (itSelect.hasNext()) {
                    oql.append(",");
                }
            }
        }

        // From section
        if (from != null && !from.isEmpty()) {
            oql.append(prettyPrint ? "\n" : " ");
            oql.append("FROM");
            oql.append(prettyPrint ? END_LINE_AND_TAB : " ");
            oql.append(from);
        }

        // Where section
        if (where != null) {
            oql.append(prettyPrint ? "\n" : " ");
            oql.append("WHERE");
            oql.append(prettyPrint ? END_LINE_AND_TAB : " ");
            where.toString(oql, prettyPrint);
        }

        // OrderBy section
        if (orders != null && !orders.isEmpty()) {
            oql.append(prettyPrint ? "\n" : " ");
            oql.append("ORDER BY");

            Iterator<OrderBy> itOrders = orders.iterator();
            while (itOrders.hasNext()) {
                oql.append(prettyPrint ? END_LINE_AND_TAB : " ");
                OrderBy orderBy = itOrders.next();
                orderBy.toString(oql, prettyPrint);
                if (itOrders.hasNext()) {
                    oql.append(',');
                }
            }
        }

        // Limit section
        if (offset != null && count != null) {
            oql.append(prettyPrint ? "\n" : " ");
            oql.append("LIMIT");
            oql.append(prettyPrint ? END_LINE_AND_TAB : " ");
            oql.append("'").append(offset).append("', '").append(count).append("'");
        }

        return oql;
    }

}
