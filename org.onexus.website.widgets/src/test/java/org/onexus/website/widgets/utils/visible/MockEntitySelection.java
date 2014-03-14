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
package org.onexus.website.widgets.utils.visible;


import org.h2.util.StringUtils;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.FilterConfig;
import org.onexus.website.api.IEntitySelection;
import org.onexus.website.api.utils.visible.VisibleRule;

public class MockEntitySelection implements IEntitySelection {

    private ORI filteredCollection;
    private String fieldValue;

    public MockEntitySelection(ORI filteredCollection, String fieldValue) {
        this.fieldValue = fieldValue;
        this.filteredCollection = filteredCollection;
    }

    @Override
    public boolean match(VisibleRule rule) {

        String filterPath = filteredCollection.getPath();
        String rulePath = rule.getFilteredCollection().getPath();

        boolean validCollection = (filterPath == null || rulePath == null) ? false : filterPath.endsWith(rulePath);

        if (rule.getField() == null) {
            return validCollection;
        }

        return StringUtils.equals(fieldValue, rule.getValue());

    }


    @Override
    public ORI getSelectionCollection() {
        return filteredCollection;
    }

    @Override
    public FilterConfig getFilterConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEnable(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeletable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDeletable(boolean deletable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Filter buildFilter(Query query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTitle(Query query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toUrlParameter(boolean global, ORI parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadUrlPrameter(String parameter) {
        throw new UnsupportedOperationException();
    }

}
