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
package org.onexus.website.api.widgets.filters;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.website.api.events.EventFiltersUpdate;
import org.onexus.website.api.events.EventQueryUpdate;
import org.onexus.website.api.widgets.Widget;

import java.util.List;

public class SelectorFiltersWidget extends Widget<SelectorFiltersWidgetConfig, SelectorFiltersWidgetStatus> {

    public SelectorFiltersWidget(String componentId, IModel<SelectorFiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);
        onEventFireUpdate(EventQueryUpdate.class);

        Form form = new Form("form");

        CheckBoxMultipleChoice<FilterConfig> filters = new CheckBoxMultipleChoice<FilterConfig>(
                "filters",
                new PropertyModel<List<FilterConfig>>(statusModel, "selectedFilters"),
                getConfig().getFilters(),
                new ChoiceRenderer<FilterConfig>("name")
        );
        filters.setSuffix("");
        form.add(filters);
        form.setOutputMarkupId(true);
        add(form);

        AjaxFormValidatingBehavior.addToAllFormComponents(form, "change");

        /*
        add(new AjaxSubmitLink("apply", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                send(getPage(), Broadcast.BREADTH, EventFiltersUpdate.EVENT);
            }
        });*/

    }

    @Override
    public void onClose(AjaxRequestTarget target) {
        send(getPage(), Broadcast.BREADTH, EventFiltersUpdate.EVENT);
    }
}
