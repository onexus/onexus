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
package org.onexus.ui.website.widgets.selector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.collection.api.IEntity;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("widget-selector")
public class SelectorWidgetConfig extends WidgetConfig {

    private SelectorWidgetStatus defaultStatus;

    private String collection;
    private String field;
    private String where;

    public SelectorWidgetConfig() {
        super();
    }

    public SelectorWidgetConfig(String id) {
        super(id);
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
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
