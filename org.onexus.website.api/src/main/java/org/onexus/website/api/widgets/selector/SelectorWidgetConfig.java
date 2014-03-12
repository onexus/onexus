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
package org.onexus.website.api.widgets.selector;

import org.onexus.resource.api.ORI;
import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.widgets.WidgetConfig;

@ResourceAlias("widget-selector")
public class SelectorWidgetConfig extends WidgetConfig {

    private SelectorWidgetStatus defaultStatus;

    private ORI collection;
    private String field;
    private String where;
    private ORI mapCollection;
    private Boolean selection;

    public SelectorWidgetConfig() {
        super();
    }

    public SelectorWidgetConfig(String id) {
        super(id);
    }

    public ORI getCollection() {
        return collection;
    }

    public void setCollection(ORI collection) {
        this.collection = collection;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public ORI getMapCollection() {
        return mapCollection;
    }

    public void setMapCollection(ORI mapCollection) {
        this.mapCollection = mapCollection;
    }

    public Boolean getSelection() {
        return selection;
    }

    public void setSelection(Boolean selection) {
        this.selection = selection;
    }

    @Override
    public SelectorWidgetStatus createEmptyStatus() {
        return new SelectorWidgetStatus(getId());
    }

    public SelectorWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(SelectorWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
