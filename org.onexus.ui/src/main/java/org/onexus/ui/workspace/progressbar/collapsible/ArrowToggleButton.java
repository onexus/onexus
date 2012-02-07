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

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.ui.website.utils.panels.icons.Icons;

/**
 * ArrowToggleButton
 * <p/>
 * Simple toggle button is suposed to change the state via ajax.
 * <p/>
 * This Class uses img from : http://sourceforge.net/projects/openiconlibrary/
 *
 * @author armand
 */
public class ArrowToggleButton extends Panel {

    IModel<Boolean> state;

    /**
     * @param id
     * @param model
     */
    public ArrowToggleButton(String id, IModel<Boolean> model) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        this.state = model;
        add(new Image("toggleimg", new ButtonImageModel()));

    }

    private Boolean getState() {
        return this.state.getObject();
    }

    private class ButtonImageModel extends Model<ResourceReference> {

        public ButtonImageModel() {
        }

        @Override
        public ResourceReference getObject() {
            // Returns kind of icon
            if (getState()) {
                return Icons.ARROW_RIGHT;
            } else {
                return Icons.ARROW_DOWN;
            }

        }
    }

}
