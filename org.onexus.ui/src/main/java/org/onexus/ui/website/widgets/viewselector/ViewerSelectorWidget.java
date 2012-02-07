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
package org.onexus.ui.website.widgets.viewselector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.ui.website.WebsiteStatus;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.browser.BrowserPage;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.tabs.TabConfig;
import org.onexus.ui.website.tabs.TabStatus;
import org.onexus.ui.website.viewers.ViewerConfig;
import org.onexus.ui.website.widgets.Widget;

public class ViewerSelectorWidget extends Widget<ViewerSelectorWidgetConfig, ViewerSelectorWidgetStatus> {

    private IModel<ViewerOption> ctModel;

    public ViewerSelectorWidget(String componentId, ViewerSelectorWidgetConfig config,
	    IModel<ViewerSelectorWidgetStatus> status) {
	super(componentId, config, status);

	onEventFireUpdate(EventQueryUpdate.class);

	WebsiteStatus websiteStatus =  getWebsiteStatus();
	BrowserPageStatus browserStatus = (BrowserPageStatus) websiteStatus.getPageStatus(websiteStatus.getCurrentPageId());
	ViewerOption defaultOption = null;
	
	if (browserStatus!=null) {
	    TabStatus tabStatus = browserStatus.getCurrentTabStatus();
	    if (tabStatus != null) {
		defaultOption = new ViewerOption(tabStatus.getCurrentViewer(), null);
	    }
	}
	
	ctModel = Model.of(defaultOption);

    }

    @Override
    protected void onBeforeRender() {
	@SuppressWarnings("rawtypes")
	Form form = new Form("form");

	DropDownChoice<ViewerOption> ct = new DropDownChoice<ViewerOption>("views", ctModel, getViewers());
	ct.add(new OnChangeAjaxBehavior() {

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		ViewerOption option = ctModel.getObject();
		
		BrowserPageStatus status = findParent(BrowserPage.class).getStatus();
		if (option != null && status != null) {
		    status.getCurrentTabStatus().setCurrentViewer(option.getId());
		}
		
		sendEvent(EventViewChange.EVENT);
	    }
	});

	form.add(ct);

	addOrReplace(form);

	super.onBeforeRender();
    }

    private List<ViewerOption> getViewers() {

	List<ViewerOption> viewers = new ArrayList<ViewerOption>();

	BrowserPageStatus status = findParent(BrowserPage.class).getStatus();
	BrowserPageConfig config = getBrowserConfig();

	if (status != null && config != null) {

	    String currentTabId = status.getCurrentTabId();

	    TabConfig tabConfig = config.getTab(currentTabId);

	    List<ViewerConfig> viewerConfigs = tabConfig.getViewers();

	    if (viewerConfigs != null) {
		ViewerOption currentOption = ctModel.getObject(); 
		for (ViewerConfig viewerConfig : viewerConfigs) {
		    
		    if (currentOption!=null && currentOption.getId().equals(viewerConfig.getId())) {
			currentOption.setTitle(viewerConfig.getTitle());
			viewers.add(currentOption);
		    } else {		    
			viewers.add(new ViewerOption( viewerConfig.getId(), viewerConfig.getTitle()));
		    }
		}
	    }
	}

	return viewers;
    }

    private BrowserPageConfig getBrowserConfig() {

	BrowserPage browser = findParent(BrowserPage.class);

	if (browser != null) {
	    return browser.getConfig();
	}

	return null;

    }
    
    private class ViewerOption implements Serializable {
	
	private String id;
	private String title;
	
	public ViewerOption(String id, String title) {
	    super();
	    this.id = id;
	    this.title = title;
	}

	public void setTitle(String title) {
	    this.title = title;	    
	}

	public String getId() {
	    return id;
	}

	@Override
	public String toString() {
	    return (title!=null ? title : id);
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + getOuterType().hashCode();
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    ViewerOption other = (ViewerOption) obj;
	    if (!getOuterType().equals(other.getOuterType()))
		return false;
	    if (id == null) {
		if (other.id != null)
		    return false;
	    } else if (!id.equals(other.id))
		return false;
	    return true;
	}

	private ViewerSelectorWidget getOuterType() {
	    return ViewerSelectorWidget.this;
	}
	
    }
    
}
