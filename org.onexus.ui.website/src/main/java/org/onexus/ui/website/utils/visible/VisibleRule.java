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
package org.onexus.ui.website.utils.visible;

import org.onexus.resource.api.ORI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VisibleRule implements Serializable {

    private ORI parentURI;
    private ORI filteredCollection;
    private String field;
    private String operator;
    private String value;
    private boolean negated;

    private VisibleRule(ORI parentURI, String rule) {
        super();

        this.parentURI = parentURI;

        rule = rule.trim();

        negated = rule.startsWith("!");

        rule = rule.replace("!", "");
        int ini = rule.indexOf("[");
        if (ini != -1) {
            filteredCollection = new ORI(rule.substring(0, ini));

            int end = rule.indexOf("]");
            String values[] = rule.substring(ini + 1, end).split(" ");

            if (values.length > 0) field = values[0].trim();
            if (values.length > 1) operator = values[1].trim();
            if (values.length > 2) value = values[2].trim();

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

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public boolean isNegated() {
        return negated;
    }

    public static List<VisibleRule> parseRules(ORI parentURI, String visible ) {

        List<VisibleRule> rules = new ArrayList<VisibleRule>();

        if (visible == null) {
            return rules;
        }

        for (String rule : visible.split(",")) {
            rules.add(new VisibleRule(parentURI, rule));
        }

        return rules;
    }

}
