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
package org.onexus.ui.wizards.website;

import org.apache.wicket.extensions.wizard.*;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Folder;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.TabConfig;
import org.onexus.ui.website.pages.browser.ViewConfig;
import org.onexus.ui.website.pages.html.HtmlPageConfig;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.download.DownloadWidgetConfig;
import org.onexus.ui.website.widgets.share.ShareWidgetConfig;
import org.onexus.ui.website.widgets.tableviewer.ColumnSet;
import org.onexus.ui.website.widgets.tableviewer.TableViewerConfig;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;
import org.onexus.ui.wizards.AbstractNewResourceWizard;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NewWebsiteWizard extends AbstractNewResourceWizard<WebsiteConfig> {

    private Boolean addTab = true;
    private String tabName = "Tab-0";
    private String mainTabCollection;
    private TabConfig currentTab;

    @Inject
    public IResourceManager resourceManager;

    public NewWebsiteWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id, resourceModel);

        AddTabCondition condition = new AddTabCondition();
        WizardModel model = new WizardModel();
        model.add(new ResourceName());
        model.add(new AddTabs());
        model.add(new TabName(), condition);
        model.add(new MainTabCollection(), condition);
        model.add(new StaticContentStep(Model.of("New website"), Model.of("Confirm website"), "Done", false));

        init(model);
    }

    @Override
    protected WebsiteConfig getDefaultResource() {

        WebsiteConfig website = new WebsiteConfig();
        website.setPages(new ArrayList<PageConfig>());
        website.setName("website");
        website.setTitle("Website title");

        // Browser page
        BrowserPageConfig browserPage = new BrowserPageConfig();
        browserPage.setId("browser");
        browserPage.setLabel("Browser");
        browserPage.setTabs(new ArrayList<TabConfig>());
        browserPage.setWidgets(new ArrayList<WidgetConfig>());

        WidgetConfig downloadWidget = new DownloadWidgetConfig("download");
        downloadWidget.setButton("download");
        browserPage.getWidgets().add(downloadWidget);

        WidgetConfig shareWidget = new ShareWidgetConfig("share");
        shareWidget.setButton("share");
        browserPage.getWidgets().add(shareWidget);

        website.getPages().add(browserPage);

        // Html page
        HtmlPageConfig htmlPage = new HtmlPageConfig();
        htmlPage.setId("about");
        htmlPage.setLabel("About");
        htmlPage.setContent("Website powered by Onexus");
        website.getPages().add(htmlPage);


        return website;
    }

    private BrowserPageConfig getBrowserPage() {
        WebsiteConfig website = getResource();
        return (BrowserPageConfig) website.getPage("browser");
    }

    private final class ResourceName extends WizardStep {

        public ResourceName() {
            super("New website", "Creates a new wesite inside the current project");

            add(getFieldResourceName());

        }
    }

    private final class AddTabs extends WizardStep {

        public AddTabs() {
            super("New website", "Add a browser tabs");

            add(new DropDownChoice<Boolean>("addTab", Arrays.asList(new Boolean[]{Boolean.TRUE, Boolean.FALSE})));
        }
    }

    private final class TabName extends WizardStep {
        public TabName() {
            super("New website", "Tab name");

            add(new TextField<String>("tabName"));
        }

        @Override
        public void applyState() {

            BrowserPageConfig browserPage = getBrowserPage();
            int tabNum = browserPage.getTabs().size();
            currentTab = new TabConfig();
            currentTab.setId("tab" + tabNum);
            currentTab.setTitle(tabName);

        }


    }

    private final class MainTabCollection extends WizardStep {

        public MainTabCollection() {
            super("New website", "Main tab collection");

            List<String> projectCollections = new ArrayList<String>();
            addAllCollections(projectCollections, getParentUri());
            add(new DropDownChoice<String>("mainTabCollection", projectCollections));

        }

        private void addAllCollections(List<String> collectionUris, String parentUri) {

            List<Collection> collections = resourceManager.loadChildren(Collection.class, parentUri);
            for (Collection collection : collections) {
                collectionUris.add(ResourceUtils.getResourcePath(collection.getURI()));
            }

            List<Folder> folders = resourceManager.loadChildren(Folder.class, parentUri);
            for (Folder folder : folders) {
                addAllCollections(collectionUris, folder.getURI());
            }

        }

        @Override
        public void applyState() {

            BrowserPageConfig browserPage = getBrowserPage();
            ViewConfig view = new ViewConfig();
            view.setTitle("default");

            String tableWidgetId = currentTab.getId() + "-table";
            view.setMain(tableWidgetId);
            view.setTopRight("download, share");

            TableViewerConfig tableWidget = new TableViewerConfig(tableWidgetId, mainTabCollection);
            tableWidget.setColumnSets(new ArrayList<ColumnSet>());

            // Main collection columns
            Collection collection = resourceManager.load(Collection.class, ResourceUtils.getAbsoluteURI(getParentUri(), mainTabCollection));
            StringBuilder fields = new StringBuilder();
            Iterator<Field> fieldIterator = collection.getFields().iterator();
            while (fieldIterator.hasNext()) {
                fields.append(fieldIterator.next().getId());
                if (fieldIterator.hasNext()) {
                    fields.append(", ");
                }
            }
            ColumnConfig mainColumns = new ColumnConfig(mainTabCollection, fields.toString());

            ColumnSet columnSet = new ColumnSet();
            columnSet.setTitle("default");
            columnSet.setColumns(new ArrayList<IColumnConfig>());
            columnSet.getColumns().add(mainColumns);
            tableWidget.getColumnSets().add(columnSet);

            browserPage.getWidgets().add(tableWidget);

            // Add view
            currentTab.setViews(new ArrayList<ViewConfig>());
            currentTab.getViews().add(view);

            // Add current tab
            browserPage.getTabs().add(currentTab);

            // Prepare next tab iteration
            tabName = "Tab-" + browserPage.getTabs().size();
            currentTab = null;
            WizardModel model = (WizardModel) getWizardModel();
            IWizardStep firstStep = model.stepIterator().next();
            model.setActiveStep(firstStep);


        }
    }

    private final class AddTabCondition implements WizardModel.ICondition {

        @Override
        public boolean evaluate() {
            return getAddTab();
        }
    }

    public Boolean getAddTab() {
        return addTab;
    }

    public void setAddTab(Boolean addTab) {
        this.addTab = addTab;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }


}
