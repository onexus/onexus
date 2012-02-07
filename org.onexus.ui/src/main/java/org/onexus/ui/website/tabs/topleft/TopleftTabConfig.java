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
package org.onexus.ui.website.tabs.topleft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.onexus.ui.website.tabs.TabConfig;
import org.onexus.ui.website.widgets.WidgetConfig;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("tab-topleft")
public class TopleftTabConfig extends TabConfig {

    public final static String REGION_TOP = "top";
    public final static String REGION_TOP_RIGHT = "top-right";
    public final static String REGION_LEFT = "left";
    
    private TopleftTabStatus defaultStatus;

    public TopleftTabConfig() {
	super();
    }

    public TopleftTabConfig(String tabId, String title) {
	super(tabId, title);
    }
    
    public List<WidgetConfig> getLeftWidgets() {
	List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();
	CollectionUtils.select(getWidgets(),
		new FilterWidgetRegion(REGION_LEFT), widgets);
	return widgets;
    }

    public List<WidgetConfig> getTopWidgets() {
	List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();
	CollectionUtils.select(getWidgets(),
		new FilterWidgetRegion(REGION_TOP), widgets);
	return widgets;
    }

    public List<WidgetConfig> getTopRightWidgets() {
	List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();
	CollectionUtils.select(getWidgets(), new FilterWidgetRegion(
		REGION_TOP_RIGHT), widgets);
	return widgets;
    }

    private static class FilterWidgetRegion implements Predicate {

	private String validRegion;

	public FilterWidgetRegion(String validRegion) {
	    super();
	    this.validRegion = validRegion;
	}

	@Override
	public boolean evaluate(Object object) {
	    WidgetConfig widgetConfig = (WidgetConfig) object;
	    return validRegion.equalsIgnoreCase(widgetConfig.getRegion());
	}

    }

    @Override
    public TopleftTabStatus createEmptyStatus() {
	return new TopleftTabStatus(getId(), getFirstViewerId(getViewers()));
    }

    @Override
    public TopleftTabStatus getDefaultStatus() {
	return defaultStatus;
    }

    public void setDefaultStatus(TopleftTabStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
