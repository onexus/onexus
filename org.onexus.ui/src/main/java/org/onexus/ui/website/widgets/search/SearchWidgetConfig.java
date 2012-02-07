/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.widgets.search;

import java.util.ArrayList;
import java.util.List;

import org.onexus.ui.website.widgets.WidgetConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("widget-search")
public class SearchWidgetConfig extends WidgetConfig {
    
    private SearchWidgetStatus defaultStatus;

    private List<SearchField> fields;

    public SearchWidgetConfig() {
	super();
    }

    public SearchWidgetConfig(String id, String region) {
	super(id, region);
    }

    public List<SearchField> getFields() {
	if (fields == null) {
	    fields = new ArrayList<SearchField>();
	}
	return fields;
    }

    public void setFields(List<SearchField> fields) {
	this.fields = fields;
    }

    public void addField(SearchField field) {
	this.getFields().add(field);
    }

    @Override
    public SearchWidgetStatus createEmptyStatus() {
	return new SearchWidgetStatus(getId());
    }

    public SearchWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(SearchWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
