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
package org.onexus.ui.website.widgets.tableviewer.headers;

import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.formaters.StringFormater;

public class FieldHeader extends ElementHeader {

    private final static int DEFAULT_MAX_LENGTH = 25;
    public final static String SORT_PROPERTY_SEPARATOR = "::::";

    private Collection collection;
    private Field field;

    public FieldHeader(Collection collection, Field field, IHeader parentHeader) {
        super(field, parentHeader, new StringFormater(getMaxLength(field),
                false));
        this.field = field;
        this.collection = collection;
    }

    public FieldHeader(Collection collection, Field field) {
        this(collection, field, new CollectionHeader(collection));
    }

    @Override
    public String getSortProperty() {
        StringBuilder sortProperty = new StringBuilder();

        sortProperty.append(collection.getURI()).append(SORT_PROPERTY_SEPARATOR);
        sortProperty.append(field.getId());

        return sortProperty.toString();
    }

    @Override
    public String getLabel() {
        String label = field.getLabel();
        return (label == null ? field.getId() : label);
    }

    @Override
    public String getTitle() {
        String title = field.getTitle();
        return (title == null ? field.getId() : title);
    }

    @Override
    public boolean isSortable() {
        String annotation = field.getProperty("SORTABLE");
        return (annotation == null ? true : annotation.equalsIgnoreCase("TRUE"));
    }

    public static int getMaxLength(Field attribute) {
        int maxLength;
        String value = attribute.getProperty("MAX_LENGTH");

        try {
            maxLength = Integer.valueOf(value);
        } catch (Exception e) {
            maxLength = DEFAULT_MAX_LENGTH;
        }

        return maxLength;

    }

    public Collection getCollection() {
        return collection;
    }

}
