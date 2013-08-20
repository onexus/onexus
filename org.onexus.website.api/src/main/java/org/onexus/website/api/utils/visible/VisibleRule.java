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
package org.onexus.website.api.utils.visible;

import org.onexus.resource.api.ORI;

import java.io.Serializable;

public class VisibleRule implements Serializable {

    public enum SelectionType {SINGLE, LIST, ANY}

    private ORI parentURI;
    private ORI filteredCollection;
    private SelectionType type = SelectionType.ANY;
    private String field = null;
    private String value = null;

    VisibleRule(ORI parentURI, String rule) {
        super();

        this.parentURI = parentURI;

        rule = rule.trim();

        int ini = rule.indexOf("[");
        if (ini != -1) {
            filteredCollection = new ORI(rule.substring(0, ini));

            int end = rule.indexOf("]");

            String restriction = rule.substring(ini + 1, end).trim();

            if (restriction.equalsIgnoreCase("LIST")) {
                type = SelectionType.LIST;
            } else if (restriction.equalsIgnoreCase("SINGLE")) {
                type = SelectionType.SINGLE;
            } else {
                String values[] = restriction.split("=");

                if (values.length > 0) field = values[0].trim();
                if (values.length > 1) value = values[1].trim();
            }
        } else {
            filteredCollection = new ORI(rule);
        }

    }

    public ORI getParentURI() {
        return parentURI;
    }

    public ORI getFilteredCollection() {
        return filteredCollection;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public SelectionType getType() {
        return type;
    }
}
