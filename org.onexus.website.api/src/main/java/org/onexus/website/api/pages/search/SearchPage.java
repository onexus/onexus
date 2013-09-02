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
package org.onexus.website.api.pages.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.query.Contains;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.pages.Page;
import org.onexus.website.api.pages.search.boxes.BoxesPanel;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.selection.FiltersWidgetConfig;
import org.onexus.website.api.widgets.selection.FiltersWidgetStatus;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SearchPage extends Page<SearchPageConfig, SearchPageStatus> {

    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    private transient FiltersWidgetStatus filtersStatus;

    private transient FilterConfig userFilter;

    public SearchPage(String componentId, IModel<SearchPageStatus> statusModel) {
        this(componentId, statusModel, true, true);
    }

    public SearchPage(String componentId, IModel<SearchPageStatus> statusModel, boolean showTypes, boolean showLogo) {
        super(componentId, statusModel);

        IModel<SearchPageStatus> pageStatusModel = new PropertyModel<SearchPageStatus>(this, "status");

        WebMarkupContainer container = new WebMarkupContainer("container");
        container.add(new AttributeModifier("class", (showLogo ? "show-logo" : "hide-logo")));

        Form form = new Form<SearchPageStatus>("form") {
            @Override
            protected void onSubmit() {
                ORI baseUri = SearchPage.this.getConfig().getWebsiteConfig().getORI().getParent();
                SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri, userFilter).setOutputMarkupId(true));
            }
        };
        form.setMultiPart(true);

        // By default use the first search type
        List<SearchType> types = getConfig().getTypes();
        if (getStatus().getType() == null && !types.isEmpty()) {
            getStatus().setType(types.get(0));
        }


        final TextField<String> search = new TextField<String>("search", new PropertyModel<String>(pageStatusModel, "search"));
        search.setOutputMarkupId(true);

        search.add(new AutoCompleteBehavior<IEntity>(new EntityRenderer(), new AutoCompleteSettings()) {
            @Override
            protected Iterator<IEntity> getChoices(String input) {
                return getAutocompleteChoices(input);
            }
        });
        search.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                userFilter = null;
                ORI baseUri = SearchPage.this.getConfig().getWebsiteConfig().getORI().getParent();
                SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri, userFilter).setOutputMarkupId(true));
                target.add(SearchPage.this.get("boxes"));
            }
        });

        form.add(search);

        // Filters list modal
        final WebMarkupContainer widgetModal = new WebMarkupContainer("widgetModal");
        widgetModal.setOutputMarkupId(true);
        widgetModal.add(new Label("header", ""));
        widgetModal.add(new EmptyPanel("widget"));
        widgetModal.add(new AjaxLink<String>("close") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('hide')");
            }
        });
        form.add(widgetModal);
        final AjaxLink<String> list = new AjaxLink<String>("list") {

            @Override
            public void onClick(AjaxRequestTarget target) {

                FiltersWidgetConfig filters = getStatus().getType().getFilters();

                if (filters != null) {
                    FiltersWidgetStatus status = filters.createEmptyStatus();
                    status.setConfig(filters);
                    setFiltersStatus(status);
                    widgetModal.addOrReplace(new Label("header", filters.getTitle()));
                    widgetModal.addOrReplace(new SearchFiltersWidget("widget", new PropertyModel<FiltersWidgetStatus>(SearchPage.this, "filtersStatus")) {

                        @Override
                        protected void applyFilter(FilterConfig filterConfig, AjaxRequestTarget target) {
                            filterConfig.setDeletable(true);
                            search.setModelValue(new String[]{filterConfig.getName()});
                            target.add(search);
                            ORI baseUri = SearchPage.this.getConfig().getWebsiteConfig().getORI().getParent();
                            userFilter = filterConfig;
                            SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri, filterConfig).setOutputMarkupId(true));
                            target.add(SearchPage.this.get("boxes"));
                            target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('hide')");
                        }
                    });
                    target.add(widgetModal);
                    target.appendJavaScript("$('#" + widgetModal.getMarkupId() + "').modal('show')");
                }
            }
        };
        list.setOutputMarkupPlaceholderTag(true);
        form.add(list);

        FiltersWidgetConfig filters = getStatus().getType().getFilters();
        if (filters == null) {
            list.setVisible(false);
        } else {
            list.add(new AttributeModifier("rel", "tooltip"));
            list.add(new AttributeModifier("title", filters.getTitle()));
            list.setVisible(true);
        }
        setFiltersStatus(null);

        // Choose type
        RadioChoice<SearchType> typeSelect = new RadioChoice<SearchType>("type", new PropertyModel<SearchType>(pageStatusModel, "type"), types, new SearchTypeRenderer());
        typeSelect.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                SearchPage.this.addOrReplace(new EmptyPanel("boxes"));
                getStatus().setSearch("");
                target.add(search);
                target.add(SearchPage.this.get("form").get("examplesContainer"));
                target.add(SearchPage.this.get("boxes"));

                if (getStatus().getType().getFilters() == null) {
                    list.setVisible(false);
                } else {
                    list.setVisible(true);
                }
                setFiltersStatus(null);
                target.add(list);
            }
        });
        form.add(typeSelect);
        typeSelect.setVisible(showTypes);

        container.add(form);
        add(container);

        // Examples
        WebMarkupContainer examples = new WebMarkupContainer("examplesContainer");
        examples.setOutputMarkupId(true);
        examples.add(new ListView<String>("examples", new ExamplesModel(new PropertyModel<SearchType>(pageStatusModel, "type"))) {

            @Override
            protected void populateItem(ListItem<String> item) {

                AjaxLink<String> link = new AjaxLink<String>("link", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getStatus().setSearch(getModelObject());
                        ORI baseUri = SearchPage.this.getConfig().getWebsiteConfig().getORI().getParent();
                        SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri, null));
                        target.add(search);
                        target.add(SearchPage.this.get("boxes"));
                    }
                };

                link.add(new Label("label", item.getModel()));
                item.add(link);

                WebMarkupContainer sep = new WebMarkupContainer("sep");
                sep.setVisible(item.getIndex() + 1 != getModelObject().size());
                item.add(sep);

            }
        });

        form.add(examples);

        ORI baseUri = SearchPage.this.getConfig().getWebsiteConfig().getORI().getParent();
        add(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri, null));

    }

    public FiltersWidgetStatus getFiltersStatus() {
        return filtersStatus;
    }

    public void setFiltersStatus(FiltersWidgetStatus filtersStatus) {
        this.filtersStatus = filtersStatus;
    }

    private Iterator<IEntity> getAutocompleteChoices(String in) {

        int lastComma = in.lastIndexOf(',');
        String input = (lastComma > -1 ? in.substring(lastComma+1).trim() : in.trim());

        Query query = new Query();
        SearchType type = getStatus().getType();

        ORI collectionUri = getAbsoluteUri(type.getCollection());
        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
        query.setFrom(collectionAlias);

        List<String> fieldList = type.getFieldsList();
        query.addSelect(collectionAlias, fieldList);

        for (String field : fieldList) {
            QueryUtils.or(query, new Contains(collectionAlias, field, input));
        }
        query.addOrderBy(new OrderBy(collectionAlias, fieldList.get(0)));

        query.setCount(10);

        return new EntityIterator(collectionManager.load(query), collectionUri);
    }

    private ORI getAbsoluteUri(ORI partialUri) {
        ORI baseUri = SearchPage.this.getConfig().getWebsiteConfig().getORI().getParent();
        return partialUri.toAbsolute(baseUri);
    }

    private IResourceManager getResourceManager() {
        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }


    private class SearchTypeRenderer implements IChoiceRenderer<SearchType> {

        @Override
        public Object getDisplayValue(SearchType type) {

            ORI collectionUri = type.getCollection();

            Collection collection = getResourceManager().load(Collection.class, getAbsoluteUri(collectionUri));

            if (collection == null) {
                return collectionUri;
            }

            String title = collection.getTitle();
            return (title == null ? collection.getName() : title);
        }

        @Override
        public String getIdValue(SearchType object, int index) {
            return Integer.toString(index);
        }
    }

    /**
     * Generic IEntity renderer that show all the fields.
     */
    private class EntityRenderer implements IAutoCompleteRenderer<IEntity> {

        public final void render(final IEntity object, final Response response, final String criteria) {
            String textValue = getTextValue(object, criteria);
            if (textValue == null) {
                throw new IllegalStateException(
                        "A call to textValue(Object) returned an illegal value: null for object: " +
                                object.toString());
            }
            textValue = textValue.replaceAll("\\\"", "&quot;");

            response.write("<li textvalue=\"" + textValue + "\"");
            response.write(">");
            renderChoice(object, response, criteria);
            response.write("</li>");
        }

        private String getTextValue(IEntity object, String in) {

            int lastComma = in.lastIndexOf(',');
            String criteria = (lastComma > -1 ? in.substring(lastComma+1).trim() : in.trim());
            String previous = (lastComma > -1 ? in.substring(0, lastComma) + ", " : "");


            SearchType type = getStatus().getType();
            List<String> fields = type.getKeysList();

            for (String field : fields) {
                String value = String.valueOf(object.get(field));

                if (StringUtils.containsIgnoreCase(value, criteria)) {
                    return previous + value;
                }
            }

            return previous + String.valueOf(object.get(fields.get(0)));
        }

        public final void renderHeader(final Response response) {
            response.write("<ul>");
        }

        public final void renderFooter(final Response response, int count) {
            response.write("</ul>");
        }

        protected void renderChoice(IEntity object, Response response, String criteria) {

            SearchType type = getStatus().getType();
            List<String> keys = type.getKeysList();
            List<String> fields = type.getFieldsList();


            boolean keyFieldAdded = false;
            for (String field : fields) {
                String value = String.valueOf(object.get(field));

                if (StringUtils.containsIgnoreCase(value, criteria)) {
                    if (keys.contains(field)) {
                        keyFieldAdded = true;
                    }

                    renderValue(response, value, criteria, field);

                }
            }

            if (!keyFieldAdded) {
                String field = keys.get(0);
                String value = String.valueOf(object.get(field));
                renderValue(response, value, criteria, field);
            }
        }

        private void renderValue(Response response, String value, String criteria, String field) {

            String hlValue = value.replaceAll("(?i)(" + criteria + ")", "<strong>$1</strong>");
            response.write("<span class='f'>" + field.toLowerCase() + ":</span>" + hlValue);
            response.write("<br />");
        }


    }


    private class ExamplesModel extends AbstractReadOnlyModel<List<String>> {

        private IModel<SearchType> model;

        private ExamplesModel(IModel<SearchType> model) {
            this.model = model;
        }

        @Override
        public List<String> getObject() {

            SearchType searchType = model.getObject();

            if (searchType == null || searchType.getExamples() == null) {
                return Collections.EMPTY_LIST;
            }

            List<String> values = new ArrayList<String>();
            for (String value : searchType.getExamples().split(",")) {
                values.add(value.trim());
            }
            return values;
        }
    }

}
