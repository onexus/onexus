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
package org.onexus.website.api.widgets.selector;

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
import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.OrderBy;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.events.EventFiltersUpdate;
import org.onexus.website.api.widgets.Widget;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectorWidget extends Widget<SelectorWidgetConfig, SelectorWidgetStatus> {

    @PaxWicketBean(name = "collectionManager" )
    private ICollectionManager collectionManager;

    @PaxWicketBean(name = "queryParser" )
    private IQueryParser queryParser;

    private transient EntityChoice selection;
    private transient List<EntityChoice> choices;

    public SelectorWidget(String componentId, IModel<SelectorWidgetStatus> statusModel) {
        super(componentId, statusModel);

        Form form = new Form("form" );
        DropDownChoice<EntityChoice> dropDown = new DropDownChoice<EntityChoice>(
                "select",
                new PropertyModel<EntityChoice>(this, "selection" ),
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
                buffer.append("\n<option" );

                // If null is selected, indicate that
                if ("".equals(selectedValue)) {
                    buffer.append(" selected=\"selected\"" );
                }

                // Add body of option tag
                buffer.append(" value=\"\">" ).append(option).append("</option>" );

                return buffer;
            }
        };

        dropDown.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, EventFiltersUpdate.EVENT);
            }
        });

        dropDown.setNullValid(true);

        add(new AjaxIndicatorAppender());

        form.add(dropDown);
        add(form);

    }

    public EntityChoice getSelection() {

        if (selection == null) {
            SelectorWidgetStatus status = getStatus();
            if (status != null && status.getSelection() != null) {
                for (EntityChoice choice : getChoices()) {
                    if (choice.getId().equals(status.getSelection())) {
                        this.selection = choice;
                        break;
                    }
                }
            }
        }

        return selection;
    }

    public void setSelection(EntityChoice entityChoice) {
        selection = entityChoice;
        String id = (entityChoice == null ? null : entityChoice.getId());
        getStatus().setSelection(id);
    }

    private List<EntityChoice> getChoices() {

        if (choices == null) {

            String field = getConfig().getField();
            ORI collection = getConfig().getCollection();

            Query query = new Query();
            query.setOn(getWebsiteOri());

            String collectionAlias = QueryUtils.newCollectionAlias(query, collection);
            query.setFrom(collectionAlias);
            query.addSelect(collectionAlias, null);
            query.addOrderBy(new OrderBy(collectionAlias, field));

            String oqlWhere = getConfig().getWhere();

            if (oqlWhere != null && !oqlWhere.isEmpty()) {
                Filter where = queryParser.parseWhere(collectionAlias + "." + getConfig().getWhere().trim());
                query.setWhere(where);
            }

            Iterator<IEntity> choicesIt = new EntityIterator(collectionManager.load(query), collection);

            choices = new ArrayList<EntityChoice>();

            while (choicesIt.hasNext()) {
                IEntity entity = choicesIt.next();
                choices.add(new EntityChoice(entity.getId(), String.valueOf(entity.get(field))));
            }

        }

        return choices;
    }

    public static class EntityChoice implements Serializable {

        private String id;
        private String field;

        public EntityChoice(String id, String field) {
            this.id = id;
            this.field = field;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String toString() {
            return getField();
        }
    }


}
