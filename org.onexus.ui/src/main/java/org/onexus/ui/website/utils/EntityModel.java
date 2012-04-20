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
package org.onexus.ui.website.utils;

import org.apache.wicket.model.IModel;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IEntity;
import org.onexus.core.query.FixedEntity;
import org.onexus.core.resources.Collection;
import org.onexus.core.utils.EntityIterator;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.OnexusWebSession;

import javax.inject.Inject;

public class EntityModel implements IModel<IEntity> {

    private String collectionURI;
    private String entityId;

    private transient IEntity entity;

    @Inject
    public ICollectionManager collectionManager;

    public EntityModel() {
        this(null, null);
    }

    public EntityModel(FixedEntity fe) {
        this(fe.getCollectionURI(), fe.getEntityId());
    }

    public EntityModel(String collectionId, String entityId) {
        super();
        OnexusWebApplication.get().getInjector().inject(this);

        this.collectionURI = collectionId;
        this.entityId = entityId;
    }


    @Override
    public IEntity getObject() {
        if (entity == null && entityId != null && collectionURI != null) {
            entity = new EntityIterator(
                    collectionManager.load(new SingleEntityQuery(collectionURI, entityId)),
                    collectionURI
            ).next();
        }
        return entity;
    }

    @Override
    public void setObject(IEntity object) {
        this.entity = object;
    }

    @Override
    public void detach() {
    }
}
