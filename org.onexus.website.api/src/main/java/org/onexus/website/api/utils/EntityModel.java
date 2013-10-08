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
package org.onexus.website.api.utils;

import org.apache.wicket.model.IModel;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.utils.EntityIterator;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;

import javax.inject.Inject;

public class EntityModel implements IModel<IEntity> {

    private ORI collectionURI;
    private String entityId;

    private transient IEntity entity;

    @Inject
    private ICollectionManager collectionManager;

    public EntityModel() {
        this(null, null);
    }

    public EntityModel(IEntity entity) {
        this.entity = entity;
        this.collectionURI = entity.getCollection().getORI();
        this.entityId = entity.getId();
    }

    public EntityModel(ORI collectionId, String entityId) {
        super();

        this.collectionURI = collectionId;
        this.entityId = entityId;
    }


    @Override
    public IEntity getObject() {
        if (entity == null && entityId != null && collectionURI != null) {
            entity = new EntityIterator(
                    getCollectionManager().load(new SingleEntityQuery(collectionURI, entityId)),
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

    private ICollectionManager getCollectionManager() {
        if (collectionManager == null) {
            WebsiteApplication.inject(this);
        }

        return collectionManager;
    }
}
