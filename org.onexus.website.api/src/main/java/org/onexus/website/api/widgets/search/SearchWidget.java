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
package org.onexus.website.api.widgets.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.onexus.website.api.events.EventFiltersUpdate;
import org.onexus.website.api.widgets.Widget;

public class SearchWidget extends Widget<SearchWidgetConfig, SearchWidgetStatus> {

    public SearchWidget(String componentId, IModel<SearchWidgetStatus> statusModel) {
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

}
