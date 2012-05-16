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
package org.onexus.ui.website.widgets.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.onexus.core.query.Contains;
import org.onexus.core.query.Filter;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.widgets.IQueryContributor;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;

import java.util.ArrayList;
import java.util.List;

public class SearchWidget extends Widget<SearchWidgetConfig, SearchWidgetStatus> implements IQueryContributor {

    public SearchWidget(String componentId, IWidgetModel statusModel) {
        super(componentId, statusModel);

        Form<SearchWidgetStatus> form = new Form<SearchWidgetStatus>("toolsForms", new CompoundPropertyModel<SearchWidgetStatus>((IModel<SearchWidgetStatus>) statusModel));

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

            List<Filter> filters = new ArrayList<Filter>();

            for (SearchField searchField : config.getFields()) {

                String collectionAlias = QueryUtils.newCollectionAlias(query, searchField.getCollection());
                String fields[] = searchField.getFields().split(",");

                for (String field : fields) {
                    filters.add(new Contains(collectionAlias, field.trim(), search));
                }
            }

            QueryUtils.and(query, QueryUtils.joinOr(filters));

        }

    }

}
