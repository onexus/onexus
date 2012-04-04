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
import org.onexus.core.IEntity;
import org.onexus.core.query.FixedEntity;
import org.onexus.core.resources.Collection;
import org.onexus.core.utils.EntityIterator;
import org.onexus.ui.OnexusWebSession;

public class EntityModel implements IModel<IEntity> {

    private Collection entityCollection;
    private String id;

    private transient IEntity entity;

    public EntityModel() {
    }

    public EntityModel(FixedEntity fe) {
        this(fe.getCollectionURI(), fe.getEntityId());
    }

    public EntityModel(IEntity es) {
        this.entity = es;
        this.entityCollection = es.getCollection();
        this.id = es.getId();
    }

    public EntityModel(String collectionId, String entityId) {
        this(OnexusWebSession.get().getResourceManager()
                .load(Collection.class, collectionId), entityId);
    }

    public EntityModel(Collection entityCollection, String id) {
        this.entityCollection = entityCollection;
        this.id = id;
    }

    @Override
    public IEntity getObject() {
        if (entity == null && id != null && entityCollection != null) {
            entity = new EntityIterator(
                    OnexusWebSession.get().getCollectionManager()
                            .load(new SingleEntityQuery(entityCollection.getURI(), id)),
                    entityCollection.getURI()
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
