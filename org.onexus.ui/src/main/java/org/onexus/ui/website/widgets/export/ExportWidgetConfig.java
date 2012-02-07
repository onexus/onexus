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
package org.onexus.ui.website.widgets.export;

import org.onexus.ui.website.widgets.WidgetConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("widget-export")
public class ExportWidgetConfig extends WidgetConfig {
    
    private ExportWidgetStatus defaultStatus;
    
    public ExportWidgetConfig() {
	super();
    }

    public ExportWidgetConfig(String id, String region, String mainCollection,
	    String... collections) {
	super(id, region);

	
    }

    @Override
    public ExportWidgetStatus createEmptyStatus() {
	return new ExportWidgetStatus(getId());
    }

    public ExportWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(ExportWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
