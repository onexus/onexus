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
package org.onexus.ui.website.widgets.tags;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.*;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.onexus.core.query.*;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.events.EventViewChange;
import org.onexus.ui.website.pages.IPageModel;
import org.onexus.ui.website.pages.browser.BrowserPageConfig;
import org.onexus.ui.website.pages.browser.BrowserPageStatus;
import org.onexus.ui.website.widgets.IQueryContributor;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;
import org.onexus.ui.website.widgets.tags.tagstore.ITagStoreManager;
import org.onexus.ui.website.widgets.tags.tagstore.TagStore;

import javax.inject.Inject;
import java.util.*;

public class TagWidget extends Widget<TagWidgetConfig, TagWidgetStatus> implements IQueryContributor {

    public static final CssResourceReference CSS = new CssResourceReference(TagWidget.class, "TagWidget.css");

    @Inject
    public ITagStoreManager tagStoreManager;

    private List<String> selectedTags;

    private boolean filter = false;

    public TagWidget(String componentId, IWidgetModel<TagWidgetStatus> statusModel) {
        super(componentId, statusModel);

        Form<TagWidgetStatus> form = new Form<TagWidgetStatus>("form", new CompoundPropertyModel<TagWidgetStatus>(statusModel));

        form.add(new HiddenField<String>("selection").setMarkupId("rows-selection-container"));

        form.add(new AjaxSubmitLink("apply") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                TagWidget.this.filter = false;

                TagStore tagStore = getTagStore();

                for (String tagKey : getSelectedTags()) {
                    for (String tagValue : getSelectedValues()) {
                        tagStore.putTagValue(tagKey, tagValue);
                    }
                }

                // Clear selected values
                getStatus().setSelection("");
                target.appendJavaScript("clearSelected();");

                // Clear selected tags
                selectedTags.clear();

                sendEvent(EventViewChange.EVENT);

                target.add(TagWidget.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(TagWidget.this);
            }

        });

        form.add(new AjaxSubmitLink("filter") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                TagWidget.this.filter = !getSelectedTags().isEmpty();
                sendEvent(EventFiltersUpdate.EVENT);
                target.add(TagWidget.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(TagWidget.this);
            }

        });

        final IModel<String> newLabel = Model.of("");
        form.add(new TextField<String>("newlabel", newLabel));

        form.add(new AjaxSubmitLink("newbutton") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                TagStore tagStore = getTagStore();
                String label = newLabel.getObject();

                if (label != null && !label.isEmpty()) {
                    label = label.trim().replaceAll("[^a-zA-Z0-9\\-_]", "_");
                    tagStore.putTagKey(label);
                    newLabel.setObject("");
                }

                target.add(TagWidget.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(TagWidget.this);
            }

        });

        form.add(new AjaxSubmitLink("remove") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                TagStore tagStore = getTagStore();
                for (String tagKey : getSelectedTags()) {
                    tagStore.removeTag(tagKey);
                }

                sendEvent(EventViewChange.EVENT);

                target.add(TagWidget.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(TagWidget.this);
            }

        });

        form.add(new CheckBoxMultipleChoice<String>("tags", new PropertyModel<List<String>>(this, "selectedTags"),
                new TagsModel()) {

            @Override
            protected String getSuffix(int index, String choice) {

                TagStore tagStore = getTagStore();
                int count = tagStore.getTagValues(choice).size();

                return " [" + Integer.toString(count) + "] " + super.getSuffix(index, choice);
            }

        });

        final TagsDownload download = new TagsDownload() {

            @Override
            protected IResourceStream getResourceStream() {
                return new StringResourceStream(createDownload(), "text/csv");

            }

            @Override
            protected String getFileName() {
                return getDownloadFileName();
            }

        };
        form.add(download);

        form.add(new AjaxSubmitLink("download") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(TagWidget.this);
                download.initiate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(TagWidget.this);
            }

        });

        add(form);
    }

    private List<String> getSelectedTags() {
        return selectedTags;
    }

    @SuppressWarnings("unchecked")
    private List<String> getSelectedValues() {
        TagWidgetStatus status = getStatus();
        String selection = status.getSelection();

        return (selection == null ? Collections.EMPTY_LIST : Arrays.asList(selection.split(":::")));
    }

    private CharSequence createDownload() {

        StringBuilder output = new StringBuilder();

        TagStore tagStore = getTagStore();
        for (String tagKey : getSelectedTags()) {
            for (String value : tagStore.getTagValues(tagKey)) {
                output.append(value).append("\n");
            }
        }

        return output;
    }

    private String getDownloadFileName() {

        StringBuilder fileName = new StringBuilder();

        Iterator<String> it = getSelectedTags().iterator();

        while (it.hasNext()) {
            fileName.append(it.next());
            if (it.hasNext()) {
                fileName.append("-");
            }
        }

        fileName.append(".tsv");

        return fileName.toString();

    }

    private TagStore getTagStore() {

        IPageModel pageModel = getPageModel();
        if (pageModel != null && pageModel.getConfig() instanceof BrowserPageConfig) {
            BrowserPageStatus status = (BrowserPageStatus) pageModel.getObject();

            String tagId = status.getCurrentTabId();
            String releaseURI = status.getReleaseURI();

            String namespace = releaseURI + "#" + tagId;

            TagStore store = tagStoreManager.getUserStore(namespace);

            // Check that the default tags are present
            Collection<String> keys = store.getTagKeys();
            List<String> defaultTags = getConfig().getTags();
            if (defaultTags != null) {
                for (String defaultTag : defaultTags) {
                    if (!keys.contains(defaultTag)) {
                        store.putTagKey(defaultTag);
                    }
                }
            }

            return store;
        } else {
            return null;
        }

    }

    private class TagsModel extends AbstractReadOnlyModel<List<String>> {

        @Override
        public List<String> getObject() {
            return new ArrayList<String>(getTagStore().getTagKeys());
        }

    }

    @Override
    public void onQueryBuild(Query query) {

        if (filter) {
            Set<String> selectedValues = new HashSet<String>();

            TagStore tagStore = getTagStore();

            for (String tagKey : getSelectedTags()) {
                selectedValues.addAll(tagStore.getTagValues(tagKey));
            }

            if (!selectedValues.isEmpty()) {

                List<Filter> rules = new ArrayList<Filter>(selectedValues.size());
                for (String value : selectedValues) {
                    rules.add(new EqualId(query.getFrom(), value));
                }

                QueryUtils.and(query, QueryUtils.joinOr(rules));
            }

        }

    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

}
