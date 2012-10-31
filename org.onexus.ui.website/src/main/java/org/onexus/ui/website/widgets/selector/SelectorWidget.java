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
package org.onexus.ui.website.widgets.selector;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.ui.website.events.EventFiltersUpdate;
import org.onexus.ui.website.utils.EntityModel;
import org.onexus.ui.website.utils.EntityRenderer;
import org.onexus.ui.website.utils.SingleEntityQuery;
import org.onexus.ui.website.widgets.Widget;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SelectorWidget extends Widget<SelectorWidgetConfig, SelectorWidgetStatus> {

    @Inject
    public transient ICollectionManager collectionManager;

    @Inject
    private transient IQueryParser queryParser;

    public SelectorWidget(String componentId, IModel<SelectorWidgetStatus> statusModel) {
        super(componentId, statusModel);

        Form form = new Form("form");
        DropDownChoice<String> dropDown = new DropDownChoice<String>(
                "select",
                new PropertyModel<String>(statusModel, "selection"),
                getChoices()
        ) {
            @Override
            protected CharSequence getDefaultChoice(String selectedValue) {

                String option = SelectorWidget.this.getConfig().getTitle();

                if (Strings.isEmpty(option)) {
                    option = "Choose one...";
                }

                // The <option> tag buffer
                final AppendingStringBuffer buffer = new AppendingStringBuffer(64 + option.length());

                // Add option tag
                buffer.append("\n<option");

                // If null is selected, indicate that
                if ("".equals(selectedValue))
                {
                    buffer.append(" selected=\"selected\"");
                }

                // Add body of option tag
                buffer.append(" value=\"\">").append(option).append("</option>");

                return buffer;
            }
        };

        dropDown.add( new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, EventFiltersUpdate.EVENT);
            }
        });

        dropDown.setNullValid(true);

        add( new AjaxIndicatorAppender() );

        form.add(dropDown);
        add(form);

    }

    private List<String> getChoices() {

        String field = getConfig().getField();
        ORI collection = getConfig().getCollection();


        Query query = new Query();
        query.setOn(getBaseUri());

        String collectionAlias = QueryUtils.newCollectionAlias(query, collection);
        query.setFrom(collectionAlias);
        query.addSelect(collectionAlias, Arrays.asList(field));
        query.addOrderBy(new OrderBy(collectionAlias, field));

        String oqlWhere = getConfig().getWhere();

        if (oqlWhere != null && !oqlWhere.isEmpty()) {
            Filter where = queryParser.parseWhere(collectionAlias + "." + getConfig().getWhere().trim());
            query.setWhere(where);
        }

        Iterator<IEntity> choicesIt = new EntityIterator(collectionManager.load(query), collection);

        List<String> choices = new ArrayList<String>();

        while (choicesIt.hasNext()) {
            choices.add(String.valueOf(choicesIt.next().get(field)));
        }

        return choices;
    }




}
