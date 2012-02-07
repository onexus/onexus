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
package org.onexus.ui.workspace.progressbar.collapsible;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

public class CollapsibleBorder extends Border {

	public CollapsibleBorder(String id,IModel<String> collapse,
			IModel<String> expand) {
		super(id, new Model<Boolean>());
		setOutputMarkupId(true);
		setCollapsed(false);

		final WebMarkupContainer body = new WebMarkupContainer("collapsiblebody") {
			@Override
			protected void onConfigure() {
				setVisible(!isCollapsed());
			}
		};
		body.setOutputMarkupId(true);
		//body.setOutputMarkupPlaceholderTag(true);				
		addToBorder(body);
		body.add(getBodyContainer());

		AjaxLink<Boolean> toggle = new AjaxLink<Boolean>("toggle") {

			@Override
			public void onClick(AjaxRequestTarget target) {			    
				setCollapsed(!isCollapsed());
				target.add(body);
				target.add(this);				
			}
		};
		toggle.setOutputMarkupId(true);
		addToBorder(toggle);
		toggle.add(new Label("toggleButton", new ExpandCollapseModel(collapse,expand)));
		addToBorder(new Image("refreshing", new PackageResourceReference(CollapsibleBorder.class, "ajax-loader2.gif")));
	
	}

	public void setCollapsed(boolean collapsed) {
		setDefaultModelObject(collapsed);
	}

	public boolean isCollapsed() {
		return Boolean.TRUE.equals(getDefaultModelObject());
	}

	private class ExpandCollapseModel extends AbstractReadOnlyModel<String> {
		private final IModel<String> collapse;
		private final IModel<String> expand;

		public ExpandCollapseModel(IModel<String> collapse,
				IModel<String> expand) {
			this.collapse = collapse;
			this.expand = expand;
		}

		@Override
		public String getObject() {
			if (isCollapsed()) {
				return expand.getObject();
			} else {
				return collapse.getObject();
			}
		}

		@Override
		public void detach() {
			collapse.detach();
			expand.detach();
		}

	}
}
