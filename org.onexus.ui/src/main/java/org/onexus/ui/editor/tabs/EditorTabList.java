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
package org.onexus.ui.editor.tabs;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.core.resources.Resource;
import org.onexus.ui.editor.tabs.xmleditor.XmlEditorTab;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.AbstractList;

/**
 * EditorTabList contains a set of ITab's.
 * <p/>
 * Also implements IFormModelUpdateListener in order to update correctly the resource model.
 *
 * @author armand
 */

public class EditorTabList extends AbstractList<ITab> implements Serializable {


    private IModel<? extends Resource> resource;
    private ITab[] tabs;

    /**
     * For sure there is a way to go deeper directly, but by now, we pass from parameter.
     *
     * @param resource
     * @param ajaxFormEvent
     */
    public EditorTabList(IModel<? extends Resource> resource) {

        this.resource = resource;

        this.tabs = new ITab[]{
                new EditorTab("Source", Resource.class, XmlEditorTab.class)
        };
    }

    @Override
    public ITab get(int index) {
        return this.tabs[index];
    }

    @Override
    public int size() {
        return tabs.length;
    }

    @SuppressWarnings("serial")
    public class EditorTab implements ITab {

        private IModel<String> title;
        private Class<? extends Resource> resourceType;
        private Class<? extends AbstractEditorTabPanel> resourceViewer;

        public EditorTab(String title, Class<? extends Resource> resourceType,
                         Class<? extends AbstractEditorTabPanel> resourceViewer) {
            super();
            this.title = new Model<String>(title);
            this.resourceType = resourceType;
            this.resourceViewer = resourceViewer;
        }


        @Override
        public IModel<String> getTitle() {
            return title;
        }

        @Override
        public boolean isVisible() {
            return (resource != null && resource.getObject() != null && resourceType.isAssignableFrom(resource.getObject().getClass()));
        }

        @Override
        public WebMarkupContainer getPanel(String containerId) {

            try {
                Constructor<? extends AbstractEditorTabPanel> constructor = (Constructor<? extends AbstractEditorTabPanel>) resourceViewer
                        .getConstructor(String.class, IModel.class);

                return constructor.newInstance(containerId, resource);
            } catch (Exception e) {
                throw new WicketRuntimeException(e);
            }
        }
    }

}
