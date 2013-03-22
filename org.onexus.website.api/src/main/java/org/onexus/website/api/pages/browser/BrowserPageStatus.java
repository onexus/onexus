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
package org.onexus.website.api.pages.browser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.utils.visible.VisiblePredicate;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetStatus;
import org.onexus.website.api.widgets.filters.BrowserFilter;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.*;

public class BrowserPageStatus extends PageStatus<BrowserPageConfig> {

    private String base;

    private String currentTabId;

    private String currentView;

    private Map<ORI, IFilter> filtersMap = new LinkedHashMap<ORI, IFilter>();

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    public BrowserPageStatus() {
    }

    public BrowserPageStatus(String id) {
        super(id);
    }

    @Override
    public List<WidgetStatus> getActiveWidgetStatuses(ORI parentOri) {

        BrowserPageConfig pageConfig = getConfig();
        TabConfig tabConfig = pageConfig.getTab(currentTabId);
        ViewConfig viewConfig = tabConfig.getView(currentView);

        List<WidgetConfig> widgetConfigs = new ArrayList<WidgetConfig>();
        Predicate visible = new VisiblePredicate(parentOri, getFilters());
        CollectionUtils.select(ViewConfig.getSelectedWidgetConfigs(
                pageConfig,
                viewConfig.getLeft(),
                viewConfig.getTop(),
                viewConfig.getTopRight(),
                viewConfig.getMain()
        ), visible, widgetConfigs);


        List<WidgetStatus> statuses = new ArrayList<WidgetStatus>();
        for (WidgetConfig config : widgetConfigs) {
            WidgetStatus status = getWidgetStatus(config.getId());
            if (status != null) {
                statuses.add(status);
            }
        }

        return statuses;
    }

    public String getBase() {

        if (base == null) {
            base = getConfig().getBase();
        }

        return base;
    }

    public ORI getORI() {
        return getConfig().getWebsiteConfig().getORI();
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

    public Set<ORI> getFilteredCollections() {
        return filtersMap.keySet();
    }

    public Collection<IFilter> getFilters() {
        return filtersMap.values();
    }

    public void addFilter(IFilter filter) {
        filtersMap.put(filter.getFilteredCollection(), filter);
    }

    public void removeFilter(ORI filteredCollection) {
        filtersMap.remove(filteredCollection);
    }

    @Override
    public void beforeQueryBuild(Query query) {

        if (!StringUtils.isEmpty(getBase())) {
            query.setOn(new ORI(query.getOn(), getBase()));
        }

    }

    @Override
    public void afterQueryBuild(Query query) {

        if (getFilters() != null) {
            for (IFilter fe : getFilters()) {
                if (getCollectionManager().isLinkable(query, fe.getFilteredCollection().toAbsolute(query.getOn()))) {
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
            WebsiteApplication.inject(this);
        }

        return collectionManager;
    }

    @Override
    public void encodeParameters(PageParameters parameters, String keyPrefix, boolean global) {

        BrowserPageStatus defaultStatus = getConfig().getDefaultStatus();
        if (defaultStatus == null) {
            defaultStatus = getConfig().createEmptyStatus();
        }

        //if (!global) {
            if (!StringUtils.equals(currentTabId, defaultStatus.getCurrentTabId())) {
                parameters.add(keyPrefix + "tab", currentTabId);
            }

            if (!StringUtils.equals(currentView, defaultStatus.getCurrentView()) &&
                    getConfig().getTab(currentTabId).getViews().size() > 1) {
                parameters.add(keyPrefix + "view", currentView);
            }
        //}

        ORI parentOri = getORI();
        for (IFilter filter : getFilters()) {
            if (filter instanceof FilterEntity) {
                parameters.add(keyPrefix + "f", filter.toUrlParameter(global, parentOri));
            } else {
                parameters.add(keyPrefix + "fc", filter.toUrlParameter(global, parentOri));
            }
        }


        super.encodeParameters(parameters, keyPrefix, global);
    }

    @Override
    public void decodeParameters(PageParameters parameters, String keyPrefix) {

        StringValue currentTabId = parameters.get(keyPrefix + "tab");
        if (!currentTabId.isEmpty()) {
            this.currentTabId = currentTabId.toString();

            // Check that is a valid tabId
            if (getConfig().getTab(this.currentTabId) == null) {

                // Look for the more similar tab id
                List<TabConfig> tabs = new ArrayList<TabConfig>(getConfig().getTabs());
                Collections.sort(tabs, new Comparator<TabConfig>() {
                    @Override
                    public int compare(TabConfig o1, TabConfig o2) {
                           Integer v1 = StringUtils.getLevenshteinDistance(BrowserPageStatus.this.currentTabId, o1.getId());
                           Integer v2 = StringUtils.getLevenshteinDistance(BrowserPageStatus.this.currentTabId, o2.getId());

                           return v1.compareTo(v2);
                    }
                });
                this.currentTabId = tabs.get(0).getId();
            }
        }

        StringValue currentView = parameters.get(keyPrefix + "view");
        if (!currentView.isEmpty()) {
            this.currentView = currentView.toString();
        }

        filtersMap = new LinkedHashMap<ORI, IFilter>();
        List<StringValue> values = parameters.getValues(keyPrefix + "f");
        if (!values.isEmpty()) {
            for (StringValue value : values) {
                FilterEntity fe = new FilterEntity();
                fe.loadUrlPrameter(value.toString());
                addFilter(fe);
            }
        }

        values = parameters.getValues(keyPrefix + "fc");
        if (!values.isEmpty()) {
            for (StringValue value : values) {
                BrowserFilter fe = new BrowserFilter();
                fe.loadUrlPrameter(value.toString());
                addFilter(fe);
            }
        }


        super.decodeParameters(parameters, keyPrefix);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
