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
package org.onexus.ui.editor.tabs.xmleditor;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceManager.ResourceStatus;
import org.onexus.core.resources.Resource;
import org.onexus.ui.editor.tabs.AbstractEditorTabPanel;
import org.onexus.ui.workspace.events.EventResourceSync;


public class XmlEditorTab extends AbstractEditorTabPanel {
    
    @Inject
    private IResourceManager resourceManager;
    
    private static final PackageResourceReference CODEMIRROR_JS = new PackageResourceReference(XmlEditorTab.class, "codemirror.js");
    private static final PackageResourceReference CODEMIRROR_FOLDCODE_JS = new PackageResourceReference(XmlEditorTab.class, "foldcode.js");
    private static final PackageResourceReference CODEMIRROR_HINT_JS = new PackageResourceReference(XmlEditorTab.class, "my-simple-hint.js");
    private static final PackageResourceReference CODEMIRROR_XML_HINT_JS = new PackageResourceReference(XmlEditorTab.class, "xml-hint.js");
    private static final PackageResourceReference CODEMIRROR_TAG_MAP_JS = new PackageResourceReference(XmlEditorTab.class, "tag-map.js");
    private static final PackageResourceReference CODEMIRROR_XML_JS = new PackageResourceReference(XmlEditorTab.class, "xml.js");
    private static final PackageResourceReference CODEMIRROR_CSS = new PackageResourceReference(XmlEditorTab.class, "codemirror.css");
    
    public XmlEditorTab(String id, IModel<Resource> model) {
	super(id, model);
	
	Form<Resource> form = new Form<Resource>("formXmlEditorTab", model);
	form.setOutputMarkupId(true);
	add(form);
	
	XmlTextArea textArea = new XmlTextArea("xml", model);
	
	AjaxFormValidatingBehavior behavior = new AjaxFormValidatingBehavior(form, "onChange") {

	    @Override
	    protected void onSubmit(AjaxRequestTarget target) {
		super.onSubmit(target);
		
		Resource resource = XmlEditorTab.this.getModelObject();
		
		if (resource != null) {
		    
		    boolean wasSynchronized = (resourceManager.status(resource.getURI()) == ResourceStatus.SYNC) ; 
		    resourceManager.save(resource);
		    
		    // Send only the event the first time that we pass form
		    // synchronized resource to non-synchronized.
		    if (wasSynchronized) {
			send(getPage(), Broadcast.BREADTH, EventResourceSync.EVENT);
		    }
		}
		
	    }
	    
	};
	
	behavior.setThrottleDelay(Duration.seconds(1));
	textArea.add( behavior );
	form.add( textArea );
	
    }

    @Override
    public void renderHead(IHeaderResponse response) {
	response.renderJavaScript("wicketThrottler.postponeTimerOnUpdate = true;", "throttler-postpone-true");
	response.renderCSSReference(CODEMIRROR_CSS);
	response.renderJavaScriptReference(CODEMIRROR_JS);
	response.renderJavaScriptReference(CODEMIRROR_FOLDCODE_JS);
	response.renderJavaScriptReference(CODEMIRROR_XML_JS);
	response.renderJavaScriptReference(CODEMIRROR_HINT_JS);
	response.renderJavaScriptReference(CODEMIRROR_TAG_MAP_JS);
	response.renderJavaScriptReference(CODEMIRROR_XML_HINT_JS);
    }
    
}
