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
package org.onexus.website.widget.tableviewer.headers;

import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.website.api.IFilter;
import org.onexus.website.widget.tableviewer.formaters.StringFormater;

public class FieldHeader extends ElementHeader implements IFilter {

    private static final int DEFAULT_MAX_LENGTH = 25;
    public static final String SORT_PROPERTY_SEPARATOR = "::::";

    private String defaultLabel;
    private String defaultTitle;
    private Collection collection;
    private Field field;
    private String filter;
    private boolean sortable;

    public FieldHeader(String defaultLabel, String defaultTitle, Collection collection, Field field, IHeader parentHeader, String filter, boolean sortable) {
        super(field, parentHeader, new StringFormater(getMaxLength(field, DEFAULT_MAX_LENGTH), false));
        this.defaultLabel = defaultLabel;
        this.defaultTitle = defaultTitle;
        this.field = field;
        this.collection = collection;
        this.sortable = sortable;
        this.filter = filter;
    }

    @Override
    public String getSortProperty() {
        StringBuilder sortProperty = new StringBuilder();

        sortProperty.append(collection.getORI()).append(SORT_PROPERTY_SEPARATOR);
        sortProperty.append(field.getId());

        return sortProperty.toString();
    }

    @Override
    public String getLabel() {
        if (defaultLabel != null) {
            return defaultLabel;
        }
        String label = field.getLabel();
        return label == null ? field.getId() : label;
    }

    @Override
    public String getTitle() {

        if (defaultTitle != null) {
            return defaultTitle;
        }

        String title = field.getTitle();
        return title == null ? field.getId() : title;
    }

    @Override
    public boolean isSortable() {
        return sortable;
    }

    public boolean isFilterable() {
        return !Strings.isEmpty(filter);
    }

    public static int getMaxLength(Field attribute, int defaultLength) {

        String value = attribute.getProperty("MAX_LENGTH");

        if (value == null) {
            return defaultLength;
        }

        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultLength;
        }

    }

    public Collection getCollection() {
        return collection;
    }

    public Field getField() {
        return field;
    }

    public String getFilter() {
        return filter;
    }
}
