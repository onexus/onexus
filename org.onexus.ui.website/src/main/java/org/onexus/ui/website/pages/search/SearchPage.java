package org.onexus.ui.website.pages.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.onexus.resource.api.ICollectionManager;
import org.onexus.resource.api.IEntity;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.query.Contains;
import org.onexus.resource.api.query.Query;
import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.utils.EntityIterator;
import org.onexus.resource.api.utils.QueryUtils;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.website.pages.Page;
import org.onexus.ui.website.pages.search.boxes.BoxesPanel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SearchPage extends Page<SearchPageConfig, SearchPageStatus> {

    @Inject
    public ICollectionManager collectionManager;

    @Inject
    public IResourceManager resourceManager;

    public SearchPage(String componentId, IModel<SearchPageStatus> statusModel) {
        super(componentId, statusModel);

        IModel<SearchPageStatus> pageStatusModel = new PropertyModel<SearchPageStatus>(this, "status");

        Form form = new Form<SearchPageStatus>("form", new CompoundPropertyModel<SearchPageStatus>(pageStatusModel)) {
            @Override
            protected void onSubmit() {
                String baseUri = ResourceUtils.getParentURI(SearchPage.this.getConfig().getWebsiteConfig().getURI());
                SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri).setOutputMarkupId(true));
            }
        };

        // By default use the first search type
        List<SearchType> types = getConfig().getTypes();
        if (getStatus().getType() == null && !types.isEmpty()) {
            getStatus().setType(types.get(0));
        }


        final TextField<String> search = new TextField<String>("search");
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
                String baseUri = ResourceUtils.getParentURI(SearchPage.this.getConfig().getWebsiteConfig().getURI());
                SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri).setOutputMarkupId(true));
                target.add(SearchPage.this.get("boxes"));
            }
        });

        form.add(search);

        RadioChoice<SearchType> typeSelect = new RadioChoice<SearchType>("type", types, new SearchTypeRenderer());
        typeSelect.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                SearchPage.this.addOrReplace(new EmptyPanel("boxes"));
                getStatus().setSearch("");
                target.add(search);
                target.add(SearchPage.this.get("form").get("examplesContainer"));
                target.add(SearchPage.this.get("boxes"));
            }
        });
        form.add(typeSelect);

        add(form);

        WebMarkupContainer examples = new WebMarkupContainer("examplesContainer");
        examples.setOutputMarkupId(true);
        examples.add(new ListView<String>("examples", new ExamplesModel(new PropertyModel<SearchType>(pageStatusModel, "type"))) {

            @Override
            protected void populateItem(ListItem<String> item) {

                AjaxLink<String> link = new AjaxLink<String>("link", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getStatus().setSearch(getModelObject());
                        String baseUri = ResourceUtils.getParentURI(SearchPage.this.getConfig().getWebsiteConfig().getURI());
                        SearchPage.this.addOrReplace(new BoxesPanel("boxes", SearchPage.this.getStatus(), baseUri));
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

        add(new EmptyPanel("boxes").setMarkupId("boxes"));

    }

    private Iterator<IEntity> getAutocompleteChoices(String input) {

        Query query = new Query();
        SearchType type = getStatus().getType();

        String collectionUri = getAbsoluteUri(type.getCollection());
        String collectionAlias = QueryUtils.newCollectionAlias(query, collectionUri);
        query.setFrom(collectionAlias);

        List<String> fieldList = type.getFieldsList();
        query.addSelect(collectionAlias, fieldList);

        for (String field : fieldList) {
            QueryUtils.or(query, new Contains(collectionAlias, field, input));
        }

        query.setCount(10);

        return new EntityIterator(collectionManager.load(query), collectionUri);
    }

    private String getAbsoluteUri(String partialUri) {
        String baseUri = ResourceUtils.getParentURI(SearchPage.this.getConfig().getWebsiteConfig().getURI());
        return ResourceUtils.getAbsoluteURI(baseUri, partialUri);
    }


    private class SearchTypeRenderer implements IChoiceRenderer<SearchType> {

        @Override
        public Object getDisplayValue(SearchType type) {

            String collectionUri = type.getCollection();

            Collection collection = resourceManager.load(Collection.class, getAbsoluteUri(collectionUri));

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

        private String getTextValue(IEntity object, String criteria) {


            SearchType type = getStatus().getType();
            List<String> fields = type.getKeysList();

            for (String field : fields) {
                String value = String.valueOf(object.get(field));

                if (StringUtils.containsIgnoreCase(value, criteria)) {
                    return value;
                }
            }

            return String.valueOf(object.get(fields.get(0)));
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
