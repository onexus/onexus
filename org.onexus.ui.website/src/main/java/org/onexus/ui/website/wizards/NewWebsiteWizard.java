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
package org.onexus.ui.website.wizards;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.wizard.*;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.resource.api.IResourceManager;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.Folder;
import org.onexus.resource.api.Resource;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.TabConfig;
import org.onexus.ui.website.pages.browser.ViewConfig;
import org.onexus.ui.website.pages.html.HtmlPageConfig;
import org.onexus.ui.website.widgets.WidgetConfig;
import org.onexus.ui.website.widgets.download.DownloadWidgetConfig;
import org.onexus.ui.website.widgets.search.SearchField;
import org.onexus.ui.website.widgets.search.SearchWidgetConfig;
import org.onexus.ui.website.widgets.share.ShareWidgetConfig;
import org.onexus.ui.website.widgets.tableviewer.ColumnSet;
import org.onexus.ui.website.widgets.tableviewer.TableViewerConfig;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;
import org.onexus.ui.website.widgets.tableviewer.columns.IColumnConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewWebsiteWizard extends AbstractNewResourceWizard<WebsiteConfig> {

    private Boolean addTab = true;
    private String tabName = "Tab-0";
    private String mainTabCollection;
    private List<String> linkCollections = new ArrayList<String>();
    private TabConfig currentTab;

    @Inject
    public transient IResourceManager resourceManager;

    public NewWebsiteWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id, resourceModel);

        AddTabCondition condition = new AddTabCondition();
        WizardModel model = new WizardModel();
        model.add(new ResourceName());
        model.add(new AddTabs());
        model.add(new TabName(), condition);
        model.add(new MainTabCollection(), condition);
        model.add(new LinkedCollections(), condition);
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

        @Override
        public void applyState() {

            BrowserPageConfig browserPage = getBrowserPage();
            ViewConfig view = new ViewConfig();
            view.setTitle("default");

            String tableWidgetId = currentTab.getId() + "-table";
            String searchWidgetId = currentTab.getId() + "-search";
            view.setMain(tableWidgetId);
            view.setTop(searchWidgetId);
            view.setTopRight("download, share");

            // Create search widget
            SearchWidgetConfig searchWidget = new SearchWidgetConfig(searchWidgetId);
            List<SearchField> searchFields = new ArrayList<SearchField>();
            searchWidget.setFields(searchFields);
            browserPage.getWidgets().add(searchWidget);

            // Create table widget
            TableViewerConfig tableWidget = new TableViewerConfig(tableWidgetId, mainTabCollection);
            tableWidget.setColumnSets(new ArrayList<ColumnSet>());

            // Main collection columns
            Collection collection = resourceManager.load(Collection.class, ResourceUtils.getAbsoluteURI(getParentUri(), mainTabCollection));

            List<String> keyFields = new ArrayList<String>();
            List<String> fields = new ArrayList<String>();
            List<String> search = new ArrayList<String>();

            for (Field field : collection.getFields()) {

                if (field.isPrimaryKey() != null && field.isPrimaryKey()) {
                    keyFields.add(field.getId());
                } else {
                    fields.add(field.getId());
                }

                if (String.class.equals(field.getType())) {
                    search.add(field.getId());
                }
            }

            searchFields.add(new SearchField(mainTabCollection, StringUtils.join(search, ',')));

            ColumnSet columnSet = new ColumnSet();
            columnSet.setTitle("default");
            columnSet.setColumns(new ArrayList<IColumnConfig>());

            if (!keyFields.isEmpty()) {
                columnSet.getColumns().add(new ColumnConfig(mainTabCollection, StringUtils.join(keyFields, ','), "LINK"));
            }

            if (!fields.isEmpty()) {
                columnSet.getColumns().add(new ColumnConfig(mainTabCollection, StringUtils.join(fields, ',')));
            }

            tableWidget.getColumnSets().add(columnSet);
            browserPage.getWidgets().add(tableWidget);

            // Add view
            currentTab.setViews(new ArrayList<ViewConfig>());
            currentTab.getViews().add(view);

            // Add current tab
            browserPage.getTabs().add(currentTab);

        }
    }

    private final class LinkedCollections extends WizardStep {

        private List<String> linkedCollections;

        public LinkedCollections() {
            super("New website", "Linked collections");

        }

        @Override
        protected void onBeforeRender() {

            List<String> projectCollections = new ArrayList<String>();
            addAllCollections(projectCollections, getParentUri());

            linkedCollections = new ArrayList<String>();


            Collection colA = resourceManager.load(Collection.class, ResourceUtils.getAbsoluteURI(getParentUri(), getMainTabCollection()));

            for (String collection : projectCollections) {


                Collection colB = resourceManager.load(Collection.class, ResourceUtils.getAbsoluteURI(getParentUri(), collection));

                if (colA.getURI().equals(colB.getURI())) {
                    continue;
                }

                List<FieldLink> links = LinkUtils.getLinkFields(getParentUri(), colA, colB);

                if (!links.isEmpty()) {
                    linkedCollections.add(collection);
                }
            }

            addOrReplace(new ListMultipleChoice<String>("linkCollections", linkedCollections));

            super.onBeforeRender();
        }

        @Override
        public void applyState() {

            BrowserPageConfig browserPage = getBrowserPage();

            if (!linkCollections.isEmpty()) {

                String tableWidgetId = currentTab.getId() + "-table";
                TableViewerConfig tableWidget = (TableViewerConfig) browserPage.getWidget(tableWidgetId);
                ColumnSet columnSet = tableWidget.getColumnSets().get(0);

                for (String linkCollection : linkCollections) {

                    String resourceUri = ResourceUtils.getAbsoluteURI(getParentUri(), linkCollection);
                    Collection collection = resourceManager.load(Collection.class, resourceUri);

                    List<String> otherFields = new ArrayList<String>();

                    for (Field field : collection.getFields()) {
                        otherFields.add(field.getId());
                    }

                    if (!otherFields.isEmpty()) {
                        columnSet.getColumns().add(new ColumnConfig(linkCollection, StringUtils.join(otherFields, ',')));
                    }
                }
            }

            // Prepare next tab iteration
            tabName = "Tab-" + browserPage.getTabs().size();
            mainTabCollection = null;
            linkCollections.clear();
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

    public String getMainTabCollection() {
        return mainTabCollection;
    }

    public void setMainTabCollection(String mainTabCollection) {
        this.mainTabCollection = mainTabCollection;
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


}
