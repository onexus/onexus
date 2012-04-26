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
package org.onexus.ui;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import org.onexus.core.resources.Resource;

public interface IViewerCreator extends IClusterable {

    /**
     * @return IModel used to represent the title of the tab. Must contain a
     *         string.
     */
    String getTitle();

    /**
     * @param containerId returned panel MUST have this id
     * @return a container object (e.g. Panel or Fragment) that will be placed
     *         as the content of the tab
     */
    Panel getPanel(final String containerId, final IModel<? extends Resource> model);

    double getOrder();

    boolean isVisible(Class<? extends Resource> resourceType);

}
