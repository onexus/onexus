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
package org.onexus.ui.website.widgets.columnsets;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventQueryUpdate;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.pages.browser.ViewConfig;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.tableviewer.ColumnSet;
import org.onexus.ui.website.widgets.tableviewer.TableViewerConfig;
import org.onexus.ui.website.widgets.tableviewer.TableViewerStatus;

import java.util.List;

public class ColumnsetsWidget extends Widget<ColumnsetsWidgetConfig, ColumnsetsWidgetStatus> {

    public ColumnsetsWidget(String componentId, IWidgetModel statusModel) {
        super(componentId, statusModel);

        onEventFireUpdate(EventQueryUpdate.class);

        Form<String> form = new Form<String>("form");
        add(form);

        final IModel<ColumnSet> csModel = new Model<ColumnSet>();
        TableViewerStatus status = getTableViewerStatus();
        
        int cs = (status == null ? 0 : status.getCurrentColumnSet());
        csModel.setObject(getTableViewerConfig().getColumnSets().get(cs));
        
        form.add(new AjaxColumnSetSelector("columnsets", csModel, getTableViewerConfig().getColumnSets()));

    }

    private BrowserPageStatus getPageStatus() {
        IPageModel pageModel = getPageModel();

        return (BrowserPageStatus) (pageModel == null ? null : pageModel.getObject());
    };

    private BrowserPageConfig getPageConfig() {
        IPageModel pageModel = getPageModel();

        return (BrowserPageConfig) (pageModel == null ? null : pageModel.getConfig());
    };

    private TableViewerStatus getTableViewerStatus() {

        String widgetId = getTableViewerConfig().getId();
        return (TableViewerStatus) (widgetId==null? null : getPageStatus().getWidgetStatus(widgetId));

    }

    private TableViewerConfig getTableViewerConfig() {

        String currentTab = getPageStatus().getCurrentTabId();
        String currentView = getPageStatus().getCurrentView();
        String mainWidgetId = getPageConfig().getTab(currentTab).getView(currentView).getMain().trim();
        WidgetConfig widgetConfig = getPageConfig().getWidget(mainWidgetId);

        if (widgetConfig instanceof TableViewerConfig) {
            return (TableViewerConfig) widgetConfig;
        }

        return null;
    }

    private class AjaxColumnSetSelector extends DropDownChoice<ColumnSet> {

        private AjaxColumnSetSelector(String id, final IModel<ColumnSet> selectItemModel, List<ColumnSet> listFilters) {
            super(id, selectItemModel, listFilters);

            setNullValid(false);
            add(new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    ColumnSet columnSet = (ColumnSet) getDefaultModelObject();
                    int cs = getTableViewerConfig().getColumnSets().indexOf(columnSet);
                    getTableViewerStatus().setCurrentColumnSet(cs);
                    sendEvent(EventViewChange.EVENT);
                }
            });
        }
    }
}