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
package org.onexus.ui.workspace.wizards.folder;

import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.ui.api.progressbar.ProgressBar;
import org.onexus.ui.api.wizards.AbstractWizard;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.util.Arrays;
import java.util.List;

public class ManageCollectionsWizard extends AbstractWizard {

    // Commands
    private static String LOAD = "Load all the collections";
    private static String UNLOAD = "Unload all the collections";
    private static final List<String> COMMANDS = Arrays.asList(new String[]{LOAD, UNLOAD});

    private String selected;
    private ORI resourceURI;

    @PaxWicketBean(name="resourceManager")
    private IResourceManager resourceManager;

    @PaxWicketBean(name="collectionManager")
    private ICollectionManager collectionManager;

    public ManageCollectionsWizard(String id, IModel<? extends Resource> resourceModel) {
        super(id);

        this.resourceURI = resourceModel.getObject().getURI();

        WizardModel model = new WizardModel();
        model.add(new BasicOptions());

        init(model);
    }

    @Override
    public void onFinish() {

        super.onFinish();

        if (selected.equals(UNLOAD)) {
            List<Collection> collections = resourceManager.loadChildren(Collection.class, resourceURI);
            for (Collection collection : collections) {
                collectionManager.unload(collection.getURI());
            }
            return;
        }

        if (selected.equals(LOAD)) {
            List<Collection> collections = resourceManager.loadChildren(Collection.class, resourceURI);
            for (Collection collection : collections) {
                collectionManager.unload(collection.getURI());
                Query emptyQuery = new Query();
                emptyQuery.addDefine("c", collection.getURI());
                emptyQuery.setFrom("c");
                emptyQuery.setOffset(0);
                emptyQuery.setCount(0);
                ProgressBar.show(collectionManager.load(emptyQuery));
            }
            return;
        }

    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    private final class BasicOptions extends WizardStep {


        public BasicOptions() {
            super("Manage collections", "Basic collection tools");

            RadioChoice<String> commandOptions = new RadioChoice<String>("commands", new PropertyModel<String>(ManageCollectionsWizard.this, "selected"), COMMANDS);
            add(commandOptions);

        }

    }


}
