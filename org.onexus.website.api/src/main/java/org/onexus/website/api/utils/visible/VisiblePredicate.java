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

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.IEntitySelection;
import org.onexus.website.api.utils.parser.BooleanExpressionEvaluator;

import java.util.Collection;

public class VisiblePredicate implements Predicate {

    private ORI parentURI;
    private Collection<IEntitySelection> filters;

    public VisiblePredicate(ORI parentURI, Collection<IEntitySelection> filters) {
        super();

        this.parentURI = parentURI;
        this.filters = filters;
    }

    @Override
    public boolean evaluate(Object object) {

        if (object == null) {
            return true;
        }

        String visibleQuery;
        if (object instanceof IVisible) {
            visibleQuery = ((IVisible) object).getVisible();
        } else {
            visibleQuery = object.toString();
        }

        if (StringUtils.isEmpty(visibleQuery)) {
            return true;
        }

        // Remove the help message if it's present
        int msg = visibleQuery.indexOf("::");
        if (msg != -1) {
            visibleQuery = visibleQuery.substring(0, msg).trim();
        } else {
            visibleQuery = visibleQuery.trim();
        }

        if (visibleQuery.equalsIgnoreCase("true")) {
            return true;
        }

        if (visibleQuery.equalsIgnoreCase("NOT true")) {
            return false;
        }

        if (visibleQuery.equalsIgnoreCase("false")) {
            return false;
        }

        if (visibleQuery.equalsIgnoreCase("NOT false")) {
            return true;
        }

        // Use only single character operators.
        String normalizedQuery = visibleQuery
                .replace("NOT", "!")
                .replace("AND", ",")
                .replace("OR", "|")
                .replaceAll("\\s", "");


        BooleanExpressionEvaluator evaluator = new BooleanExpressionEvaluator(normalizedQuery) {
            @Override
            protected boolean evaluateToken(String token) {

                VisibleRule rule = new VisibleRule(parentURI, token);

                boolean matchAnyFilter = false;
                for (IEntitySelection filter : filters) {
                    if (filter.match(rule)) {
                        matchAnyFilter = true;
                        break;
                    }
                }

                return matchAnyFilter;
            }
        };

        return evaluator.evaluate();
    }

    public static String getMessage(IVisible visible) {

        String visibleQuery = visible.getVisible();

        int msg = visibleQuery.indexOf("::");
        if (msg != -1) {
            return visibleQuery.substring(msg + 2).trim();
        }

        return "";
    }
}
