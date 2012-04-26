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
package org.onexus.ui.editor.tabs.xmleditor;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceManager.ResourceStatus;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.editor.tabs.AbstractEditorTabPanel;
import org.onexus.ui.workspace.events.EventResourceSync;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class XmlEditorTab extends AbstractEditorTabPanel {

    @Inject
    public IResourceManager resourceManager;

    @Inject
    public IResourceRegister resourceRegister;

    public static final PackageResourceReference CODEMIRROR_JS = new PackageResourceReference(XmlEditorTab.class, "codemirror.js");
    public static final PackageResourceReference CODEMIRROR_FOLDCODE_JS = new PackageResourceReference(XmlEditorTab.class, "foldcode.js");
    public static final PackageResourceReference CODEMIRROR_HINT_JS = new PackageResourceReference(XmlEditorTab.class, "simple-hint.js");
    public static final PackageResourceReference CODEMIRROR_XML_HINT_JS = new PackageResourceReference(XmlEditorTab.class, "xml-hint.js");
    public static final PackageResourceReference CODEMIRROR_XML_JS = new PackageResourceReference(XmlEditorTab.class, "xml.js");
    public static final PackageResourceReference CODEMIRROR_CSS = new PackageResourceReference(XmlEditorTab.class, "codemirror.css");
    public static final PackageResourceReference LOAD_JS = new PackageResourceReference(XmlEditorTab.class, "XmlEditorTab.js");


    private XmlTextArea textArea;

    public XmlEditorTab(String id, IModel<Resource> model) {
        super(id, model);

        Form<Resource> form = new Form<Resource>("formXmlEditorTab", model);
        form.setOutputMarkupId(true);
        add(form);

        textArea = new XmlTextArea("xml", model);
        textArea.setOutputMarkupId(true);

        AjaxFormValidatingBehavior behavior = new AjaxFormValidatingBehavior(form, "onChange") {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);

                Resource resource = XmlEditorTab.this.getModelObject();

                if (resource != null) {

                    boolean wasSynchronized = (resourceManager.status(resource.getURI()) == ResourceStatus.SYNC);
                    resourceManager.save(resource);

                    // Send only the event the first time that we pass form
                    // synchronized resource to non-synchronized.
                    if (wasSynchronized) {
                        send(getPage(), Broadcast.BREADTH, EventResourceSync.EVENT);
                    }
                }

            }

            @Override
            protected void updateAjaxAttributes(final AjaxRequestAttributes attributes)
            {
                super.updateAjaxAttributes(attributes);

                String id = "throttle-" + textArea.getMarkupId();
                ThrottlingSettings throttlingSettings = new ThrottlingSettings(id, Duration.seconds(1));
                attributes.setThrottlingSettings(throttlingSettings);
            }

        };

        textArea.add(behavior);

        AbstractAjaxBehavior loadCodeMirror = new AbstractDefaultAjaxBehavior() {

            @Override
            protected void respond(AjaxRequestTarget target) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        textArea.add(loadCodeMirror);

        form.add(textArea);

    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        if (target != null) {
             target.appendJavaScript( "initCodeMirror('"+textArea.getMarkupId()+"');");
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CODEMIRROR_CSS));
        response.render(JavaScriptHeaderItem.forReference(CODEMIRROR_JS));
        response.render(JavaScriptHeaderItem.forReference(CODEMIRROR_FOLDCODE_JS));
        response.render(JavaScriptHeaderItem.forReference(CODEMIRROR_XML_JS));
        response.render(JavaScriptHeaderItem.forReference(CODEMIRROR_HINT_JS));

        String autocomplete = createAutocompleteJS(resourceRegister.getAutocompleteMap(getModelObject().getClass()));
        response.render(JavaScriptHeaderItem.forScript(autocomplete, Integer.toString(autocomplete.hashCode())));
        response.render(JavaScriptHeaderItem.forReference(CODEMIRROR_XML_HINT_JS));
        response.render(JavaScriptHeaderItem.forReference(LOAD_JS));
        response.render(JavaScriptHeaderItem.forScript(
                "Wicket.Event.add(window, \"load\", function(event) { initCodeMirror('"+textArea.getMarkupId()+"'); });",
                "load-codemirror-js")
        );
    }




    private static String createAutocompleteJS(Map<String, List<String>> autocompleteMap) {
        StringBuilder js = new StringBuilder();

        if (autocompleteMap != null && !autocompleteMap.isEmpty()) {
            js.append("var tagMap = {};\n");

            for (String keyTag : autocompleteMap.keySet()) {

                Iterator<String> subTags = autocompleteMap.get(keyTag).iterator();

                if (subTags.hasNext()) {
                    js.append("tagMap['").append(keyTag).append("'] = [");

                    while (subTags.hasNext()) {
                        String subTag = subTags.next();

                        js.append("'").append(StringEscapeUtils.escapeEcmaScript(XmlUtils.formatXml(subTag))).append("'");

                        if (subTags.hasNext()) {
                            js.append(",");
                        }
                    }

                    js.append("];\n");
                }


            }
        }

        return js.toString();
    }

}
