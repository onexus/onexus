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
package org.onexus.ui.workspace.progressbar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class AutoUpdatePanel<T> extends Panel implements IFormModelUpdateListener {

    private int hashCode = 0;

    public AutoUpdatePanel(String id, IModel<T> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    @Override
    protected void onBeforeRender() {
        this.hashCode = getModelHashCode();
        super.onBeforeRender();
    }

    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof AjaxRequestTarget) {
            int newHashCode = getModelHashCode();
            if (hashCode != newHashCode) {
                ((AjaxRequestTarget) event.getPayload()).add(this);
            }
        }
    }

    private int getModelHashCode() {
        IModel<?> model = getDefaultModel();
        if (model != null && model.getObject() != null) {
            return model.getObject().hashCode();
        } else {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public T getModelObject() {
        return (T) getDefaultModelObject();
    }

    @Override
    public void updateModel() {
        // Override this if the model must to be updated via Ajax.

    }

}
