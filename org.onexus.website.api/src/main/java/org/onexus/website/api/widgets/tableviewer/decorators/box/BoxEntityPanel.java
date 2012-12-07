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
package org.onexus.website.api.widgets.tableviewer.decorators.box;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;

import java.util.ArrayList;
import java.util.List;

public class BoxEntityPanel extends Panel {

	public static CssReferenceHeaderItem CSS = CssHeaderItem.forReference(new PackageResourceReference(BoxEntityPanel.class, "BoxEntityPanel.css"));

    public BoxEntityPanel(String id, IEntity entity, List<String> fieldIds) {
        super(id);

		Collection collection = entity.getCollection();

		List<Field> fields = new ArrayList<Field>();
		if (fieldIds == null || fieldIds.isEmpty()) {
			fields.addAll(collection.getFields());
		} else {
			for (String fieldId : fieldIds) {
				fields.add( collection.getField(fieldId) );
			}
		}

		RepeatingView fieldsView = new RepeatingView("fields");
		for (Field field : fields) {

			Object value = entity.get(field.getId());
			if (value != null && !StringUtils.isEmpty(value.toString())) {

				WebMarkupContainer fc = new WebMarkupContainer(fieldsView.newChildId());
				fc.setRenderBodyOnly(true);
				fc.add(new Label("label", field.getLabel()).add(new AttributeModifier("title", field.getTitle())));
				fc.add(new Label("value", StringUtils.abbreviate(value.toString(), 50)));
				fieldsView.add(fc);
			}
		}

		add(fieldsView);


    }

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(CSS);
	}
}
