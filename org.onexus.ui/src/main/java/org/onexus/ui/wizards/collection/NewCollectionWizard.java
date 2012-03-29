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
package org.onexus.ui.wizards.collection;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Resource;
import org.onexus.ui.wizards.creators.AbstractNewResourceWizard;

public class NewCollectionWizard extends AbstractNewResourceWizard<Collection> {


    public NewCollectionWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id, resourceModel);

        WizardModel model = new WizardModel();
        model.add(new ResourceName());

        init(model);
    }

    @Override
    protected Collection getDefaultResource() {
        return new Collection();
    }

    private final class ResourceName extends WizardStep {

        public ResourceName() {
            super("New collection", "Creates a new collection inside the current release");

            add(getFieldResourceName());

        }
    }


}
