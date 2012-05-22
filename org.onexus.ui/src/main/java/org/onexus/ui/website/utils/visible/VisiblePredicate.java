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

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.onexus.ui.website.pages.browser.IFilter;

import java.util.Collection;

public class VisiblePredicate implements Predicate {

    private String releaseURI;
    private Collection<IFilter> filters;

    public VisiblePredicate(String releaseURI, Collection<IFilter> filters) {
        super();

        this.releaseURI = releaseURI;
        this.filters = filters;
    }

    @Override
    public boolean evaluate(Object object) {

        IVisible visible = (IVisible) object;

        if (object == null) {
            return true;
        }

        String visibleQuery = visible.getVisible();

        if (StringUtils.isEmpty(visibleQuery)) {
            return true;
        }

        String visibleRules[] = visibleQuery.split(",");

        for (VisibleRule rule : VisibleRule.parseRules(releaseURI, visible.getVisible())) {
            for (IFilter filter : filters) {
                if (!filter.isVisible(rule)) {
                    return false;
                }
            }
        }

        return true;
    }
}