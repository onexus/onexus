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

public class Query implements Serializable {

    public static final String END_LINE_AND_TAB = "\n\t";

    private Map<String, ORI> define = new LinkedHashMap<String, ORI>();

    private ORI on;

    private Map<String, List<String>> select = new LinkedHashMap<String, List<String>>();

    private String from;

    private Filter where;

    private List<OrderBy> orders = new ArrayList<OrderBy>();

    private Long offset;

    private Long count;

    public Query() {
        super();
    }

    public Map<String, ORI> getDefine() {
        return define;
    }

    public void addDefine(String key, ORI resourceURI) {
        define.put(key, resourceURI.toRelative(on));
    }

    public Map<String, List<String>> getSelect() {
        return select;
    }

    public String getFrom() {
        return from;
    }

    public List<OrderBy> getOrders() {
        return orders;
    }

    public Long getOffset() {
        return offset;
    }

    public Long getCount() {
        return count;
    }

    public void addSelect(String key, List<String> fieldNames) {
        if (select.containsKey(key) && select.get(key) != null) {
            select.get(key).addAll(fieldNames);
        } else {
            select.put(key, (fieldNames == null ? null : new ArrayList<String>(fieldNames)));
        }
    }

    public ORI getOn() {
        return this.on;
    }

    public void setOn(ORI resourceURI) {
        this.on = resourceURI;
    }

    public void setFrom(String resourceURI) {
        this.from = resourceURI;
    }

    public Filter getWhere() {
        return where;
    }

    public void setWhere(Filter where) {
        this.where = where;
    }

    public void addOrderBy(OrderBy order) {
        this.orders.add(order);
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public void setOffset(int offset) {
        setOffset(Long.valueOf(offset));
    }


    public void setCount(Long count) {
        this.count = count;
    }

    public void setCount(int count) {
        setCount(Long.valueOf(count));
    }


    @Override
    public String toString() {
        return toString(new StringBuilder(), true).toString();
    }

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


    public static String escapeString(String value) {
        if (value == null) {
            return null;
        }
        value = "'" + value.replace("'", "\\'") + "'";
        return value;
    }

}
