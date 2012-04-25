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
package org.onexus.ui.wizards.file;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;
import org.onexus.core.resources.Source;
import org.onexus.ui.IWizardCreator;
import org.onexus.ui.website.widgets.IWidgetCreator;

/**
 * Created by IntelliJ IDEA.
 * User: jordi
 * Date: 29/03/12
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public class CreateCollectionWizardCreator implements IWizardCreator {

    @Override
    public String getLabel() {
        return "Create a collection";
    }

    @Override
    public String getTitle() {
        return "Create a collection";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Panel getPanel(String containerId, IModel<? extends Resource> model) {
        return new CreateCollectionWizard(containerId, model);
    }

    @Override
    public double getOrder() {
        return 10;
    }

    @Override
    public boolean isVisible(Resource resource) {
        return Source.class.isAssignableFrom(resource.getClass());
    }
}
