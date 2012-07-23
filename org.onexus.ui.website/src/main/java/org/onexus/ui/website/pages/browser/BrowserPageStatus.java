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
package org.onexus.ui.website.pages.browser;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.api.OnexusWebApplication;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.widgets.WidgetStatus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class BrowserPageStatus extends PageStatus<BrowserPageConfig> {

    private String base;

    private String currentTabId;

    private String currentView;

    private List<IFilter> filters = new ArrayList<IFilter>();

    @Inject
    public ICollectionManager collectionManager;

    public BrowserPageStatus() {
    }

    public BrowserPageStatus(String id) {
        super(id);
    }

    @Override
    public List<WidgetStatus> getActiveWidgetStatuses() {

        BrowserPageConfig pageConfig = getConfig();
        TabConfig tabConfig = pageConfig.getTab(currentTabId);
        ViewConfig viewConfig = tabConfig.getView(currentView);

        return ViewConfig.getSelectedWidgetStatuses(
                this,
                viewConfig.getLeft(),
                viewConfig.getTop(),
                viewConfig.getTopRight(),
                viewConfig.getMain()
        );
    }

    public String getBase() {

        if (base == null) {
            base = getConfig().getBase();
        }

        return base;
    }

    public String getCurrentTabId() {
        return currentTabId;
    }

    public void setCurrentTabId(String currentTabId) {
        if (currentTabId != null && !currentTabId.equals(this.currentTabId)) {
            this.currentView = null;
        }
        this.currentTabId = currentTabId;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    public void setBase(String baseURI) {
        this.base = baseURI;
    }

    public List<IFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<IFilter> filters) {
        this.filters = filters;
    }

    @Override
    public void beforeQueryBuild(Query query) {

        if (!StringUtils.isEmpty(getBase())) {
            query.setOn(ResourceUtils.concatURIs(query.getOn(), getBase()));
        }

    }

    @Override
    public void afterQueryBuild(Query query) {

        if (filters != null) {
            for (IFilter fe : filters) {
                if (getCollectionManager().isLinkable(query, QueryUtils.getAbsoluteCollectionUri(query, fe.getFilteredCollection()))) {
                    Filter filter = fe.buildFilter(query);
                    QueryUtils.and(query, filter);
                    fe.setEnable(true);
                } else {
                    fe.setEnable(false);
                }
            }
        }

    }

    @Override
    public void onQueryBuild(Query query) {

    }

    private ICollectionManager getCollectionManager() {

        if (collectionManager == null) {
            OnexusWebApplication app = OnexusWebApplication.get();
            if (app != null) {
                app.getInjector().inject(this);
            }
        }

        return collectionManager;
    }

    @Override
    public void encodeParameters(PageParameters parameters, String keyPrefix) {

        BrowserPageStatus defaultStatus = getConfig().getDefaultStatus();
        if (defaultStatus == null) {
            defaultStatus = getConfig().createEmptyStatus();
        }

        if (!StringUtils.equals(currentTabId, defaultStatus.getCurrentTabId())) {
            parameters.add(keyPrefix + "tab", currentTabId);
        }

        if (!StringUtils.equals(currentView, defaultStatus.getCurrentView())) {
            parameters.add(keyPrefix + "view", currentView);
        }


        for (IFilter filter : filters) {
            parameters.add(keyPrefix + "f", filter.toUrlParameter());
        }


        super.encodeParameters(parameters, keyPrefix);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void decodeParameters(PageParameters parameters, String keyPrefix) {

        StringValue currentTabId = parameters.get(keyPrefix + "tab");
        if (!currentTabId.isEmpty()) {
            this.currentTabId = currentTabId.toString();
        }

        StringValue currentView = parameters.get(keyPrefix + "view");
        if (!currentView.isEmpty()) {
            this.currentView = currentView.toString();
        }

        List<StringValue> values = parameters.getValues(keyPrefix + "f");
        if (!values.isEmpty()) {
            this.filters = new ArrayList<IFilter>(values.size());
            for (StringValue value : values) {
                FilterEntity fe = new FilterEntity();
                fe.loadUrlPrameter(value.toString());
                this.filters.add(fe);
            }
        }


        super.decodeParameters(parameters, keyPrefix);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
