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
package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.ui.website.utils.panels.icons.Icons;

/**
 * It incorporate the checkbox, label and remove action if it is necessary.
 * <p/>
 * If it is "deletable" it gives the possibility to delete item.
 *
 * @author armand
 */
public abstract class CheckBoxItem extends Panel {

    public CheckBoxItem(String id, final ListItem<FilterConfig> item) {
        super(id);

        // Add check box
        add(new AjaxCheckBox("active", new PropertyModel<Boolean>(
                item.getModel(), "active")) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                onItemSelected(target, item.getModelObject());
            }
        });

        // Add
        add(new Label("name", new TextFormaterPropertyModel(item.getModel(),
                "name", 18, true)));
        add(new AttributeModifier("title", new PropertyModel<String>(
                item.getModel(), "name")));

        Image removeImg = new Image("removeImg", Icons.THIN_DELETE) {

            @Override
            protected boolean shouldAddAntiCacheParameter() {
                return false;
            }

        };
        add(removeImg);
        if (item.getModelObject().getDeletable()) {
            removeImg.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    onItemDeleted(target, item.getModelObject());
                }
            });

        } else {
            removeImg.setVisible(false);
        }

    }

    protected abstract void onItemSelected(AjaxRequestTarget target,
                                           FilterConfig filter);

    protected void onItemDeleted(AjaxRequestTarget target, FilterConfig fitler) {

    }
}
