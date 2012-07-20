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
package org.onexus.ui.core.wizards;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.onexus.resource.api.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultWizardsManager implements IWizardsManager {

    public static final WizardCreatorComparator WIZARD_CREATOR_COMPRATOR = new WizardCreatorComparator();

    private List<IWizardCreator> wizardCreators;

    public DefaultWizardsManager() {
        super();
    }


    public List<IWizardCreator> getWizardCreators() {
        return wizardCreators;
    }

    public void setWizardCreators(List<IWizardCreator> wizardCreators) {
        this.wizardCreators = wizardCreators;
    }

    @Override
    public List<IWizardCreator> getWizardCreators(Resource resource) {

        if (resource == null) {
            return Collections.emptyList();
        }

        List<IWizardCreator> result = new ArrayList<IWizardCreator>();
        CollectionUtils.select(wizardCreators, new VisiblePredicate(resource), result);
        Collections.sort(result, WIZARD_CREATOR_COMPRATOR);

        return result;
    }

    private static class VisiblePredicate implements Predicate, Serializable {

        private Resource resource;

        public VisiblePredicate(Resource resource) {
            super();
            this.resource = resource;
        }

        @Override
        public boolean evaluate(Object object) {

            if (object instanceof IWizardCreator) {
                IWizardCreator creator = (IWizardCreator) object;

                if (resource == null) {
                    return false;
                }

                return creator.isVisible(resource);
            }

            return false;
        }

    }

    private static class WizardCreatorComparator implements Comparator<IWizardCreator> {

        @Override
        public int compare(IWizardCreator o1, IWizardCreator o2) {
            return Double.compare(o1.getOrder(), o2.getOrder());
        }

    }

}
