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
package org.onexus.ui.website.pages.browser.boxes;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.collection.api.IEntity;
import org.onexus.resource.api.IResourceManager;
import org.onexus.collection.api.Collection;

import javax.inject.Inject;

public abstract class AbstractBox extends Panel {

    public static final String COMPONENT_ID = "box";

    @Inject
    private IResourceManager resourceManager;
    private String collectionId;

    public AbstractBox(String collectionId, IModel<IEntity> entityModel) {
        super(COMPONENT_ID, entityModel);
        this.collectionId = collectionId;
    }

    public String getTitle() {

        Collection collection = resourceManager.load(Collection.class, collectionId);

        String title = collection.getTitle();

        return (title == null ? collection.getName() : title);
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    protected IEntity getEntity() {
        return (IEntity) getDefaultModelObject();
    }

    @SuppressWarnings("unchecked")
    public IModel<IEntity> getEntityModel() {
        return (IModel<IEntity>) getDefaultModel();
    }

}
