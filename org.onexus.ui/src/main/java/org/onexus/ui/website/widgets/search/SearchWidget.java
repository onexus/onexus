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
package org.onexus.ui.website.widgets.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Like;
import org.onexus.core.query.Or;
import org.onexus.core.query.Query;
import org.onexus.ui.website.IQueryContributor;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.widgets.Widget;

import java.util.Iterator;

public class SearchWidget extends Widget<SearchWidgetConfig, SearchWidgetStatus> implements IQueryContributor {

    public SearchWidget(String componentId, SearchWidgetConfig config, IModel<SearchWidgetStatus> statusModel) {
        super(componentId, config, statusModel);

        Form<SearchWidgetStatus> form = new Form<SearchWidgetStatus>("toolsForms", new CompoundPropertyModel<SearchWidgetStatus>(statusModel));

        // Search field & button
        form.add(new TextField<String>("search"));
        form.add(new AjaxButton("searchButton") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sendEvent(EventFiltersUpdate.EVENT);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // FIXME
            }

        });

        add(form);

    }

    @Override
    public void onQueryBuild(Query query) {

        String search = getStatus().getSearch();

        if (search != null) {

            SearchWidgetConfig config = getConfig();
            Iterator<SearchField> fields = config.getFields().iterator();

            if (fields.hasNext()) {
                SearchField field = fields.next();

                Filter filter = buildSearchFieldFilter(field, search);
                while (fields.hasNext()) {
                    field = fields.next();
                    filter = new Or(field.getCollection(), filter, buildSearchFieldFilter(field, search));
                }
                query.putFilter(getConfig().getId() + "_search", filter);
            }
        }

    }

    private Filter buildSearchFieldFilter(SearchField field, String search) {

        String collectionURI = field.getCollection();
        String fieldNames[] = field.getFieldNames().split(",");

        Filter filter = new Like(collectionURI, fieldNames[0].trim(), search);

        for (int i = 1; i < fieldNames.length; i++) {
            filter = new Or(field.getCollection(), filter, new Like(collectionURI, fieldNames[i].trim(), search));
        }

        return filter;
    }

}
