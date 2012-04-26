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
package org.onexus.ui.editor;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceManager.ResourceStatus;
import org.onexus.core.resources.Resource;
import org.onexus.ui.editor.tabs.EditorTabList;
import org.onexus.ui.workspace.events.EventResourceRevert;
import org.onexus.ui.workspace.events.EventResourceSync;

import javax.inject.Inject;

/**
 * EditorViewer is the main viewer to edit resource.
 *
 * @author armand
 */
public class EditorViewer extends Panel {

    @Inject
    private IResourceManager resourceManager;

    private FeedbackPanel feedback;
    private WebMarkupContainer actionbar;
    private Form<Resource> form;

    @SuppressWarnings("unchecked")
    public EditorViewer(String id, final IModel<? extends Resource> model) {
        super(id, model);

        // Create feedback
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        form = new Form<Resource>("form", (IModel<Resource>) model);
        form.setMultiPart(true);
        form.setOutputMarkupId(true);

        // Action bar
        actionbar = new WebMarkupContainer("actionbar");
        actionbar.setOutputMarkupId(true);

        // Save Button
        actionbar.add(new ActionButton("save", form) {
            @Override
            protected void run(IResourceManager resourceManager, Resource resource) throws Exception {
                resourceManager.save(resource);
                resourceManager.commit(resource.getURI());
                send(getPage(), Broadcast.BREADTH, EventResourceSync.EVENT);
            }
        });

        // Revert Button
        actionbar.add(new ActionButton("revert", form) {
            @Override
            protected void run(IResourceManager resourceManager, Resource resource) throws Exception {

                String resourceURI = resource.getURI();
                resourceManager.revert(resourceURI);
                Resource revertedResource = resourceManager.load(Resource.class, resourceURI);
                EditorViewer.this.getModel().setObject(revertedResource);

                send(getPage(), Broadcast.BREADTH, EventResourceSync.EVENT);
                send(getPage(), Broadcast.BREADTH, EventResourceRevert.EVENT);
            }

            @Override
            public boolean isEnabled() {
                ResourceStatus status = getResourceStatus();
                return status != ResourceStatus.SYNC && status != ResourceStatus.ADD;
            }


        });
        form.add(actionbar);

        form.add(new AjaxTabbedPanel("tabs", new EditorTabList(model)));

        add(form);

    }

    private ResourceStatus getResourceStatus() {

        Resource resource = (Resource) getDefaultModelObject();

        if (resource != null && resource.getURI() != null) {
            return resourceManager.status(resource.getURI());
        }

        return ResourceStatus.SYNC;
    }


    @Override
    public void onEvent(IEvent<?> event) {

        if (event.getPayload() == EventResourceSync.EVENT) {
            RequestCycle.get().find(AjaxRequestTarget.class).add(actionbar);
        }


        if (EventResourceRevert.EVENT == event.getPayload()) {
            RequestCycle.get().find(AjaxRequestTarget.class).add(form);
        }

    }

    @SuppressWarnings("unchecked")
    public IModel<Resource> getModel() {
        return (IModel<Resource>) getDefaultModel();
    }

    private abstract class ActionButton extends AjaxButton {

        public ActionButton(String id, Form<?> form) {
            super(id, form);

        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            try {
                Resource resource = (Resource) EditorViewer.this.getDefaultModelObject();
                run(resourceManager, resource);
            } catch (Exception e) {
                form.error(e.getMessage());
                onError(target, form);
                return;
            }
            target.add(feedback);
        }

        protected abstract void run(IResourceManager resourceManager, Resource resource) throws Exception;

        @Override
        protected void onError(AjaxRequestTarget target, Form<?> form) {
            target.add(feedback);
        }

        @Override
        public boolean isEnabled() {
            return getResourceStatus() != ResourceStatus.SYNC;
        }

    }


}
