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
package org.onexus.website.widget.tags;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

public class TagColumnHeader extends Panel {

    public static final PackageResourceReference JAVA_SCRIPT = new PackageResourceReference(TagColumn.class, "tagcolumn.js");

    private WebMarkupContainer checkbox;

    public TagColumnHeader(String id) {
        this(id, Model.of("ALL"));
    }


    public TagColumnHeader(String id, IModel<String> model) {
        super(id);
        checkbox = new WebMarkupContainer("checkbox");
        checkbox.add(new AttributeModifier("value", model));

        // We decided to disable this feature (because it's confusing
        // for the user) until we implement the ALL rows selection feature #807
        checkbox.setVisible(false);

        add(checkbox);
    }

    protected WebMarkupContainer getCheckBox() {
        return checkbox;
    }

    protected String getTableId() {
        return findParent(DataTable.class).getMarkupId();
    }

    @Override
    protected void onBeforeRender() {
        checkbox.add(new AttributeModifier("onclick", "toggleCheckBoxes(this, '" + getTableId() + "');"));
        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(JAVA_SCRIPT));
    }

}
