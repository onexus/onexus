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
package org.onexus.core.query;

import org.onexus.core.IEntity;

import java.io.Serializable;

public class FixedEntity implements Serializable {

    private String collectionURI;
    private String entityId;
    private boolean deletable;

    public FixedEntity() {
        super();
    }

    public FixedEntity(IEntity entity) {
        this(entity, true);
    }

    public FixedEntity(IEntity entity, boolean deletable) {
        this(entity.getCollection().getURI(), entity.getId(), deletable);
    }

    public FixedEntity(String collectionURI, String entityId) {
        this(collectionURI, entityId, true);
    }

    public FixedEntity(String collectionURI, String entityId, boolean deletable) {
        super();
        this.collectionURI = collectionURI;
        this.entityId = entityId;
        this.deletable = deletable;
    }

    public String getCollectionURI() {
        return collectionURI;
    }

    public void setCollectionURI(String collectionURI) {
        this.collectionURI = collectionURI;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FixedEntity [collectionId=");
        builder.append(collectionURI);
        builder.append(", entityId=");
        builder.append(entityId);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((collectionURI == null) ? 0 : collectionURI.hashCode());
        result = prime * result
                + ((entityId == null) ? 0 : entityId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FixedEntity other = (FixedEntity) obj;
        if (collectionURI == null) {
            if (other.collectionURI != null)
                return false;
        } else if (!collectionURI.equals(other.collectionURI))
            return false;
        if (entityId == null) {
            if (other.entityId != null)
                return false;
        } else if (!entityId.equals(other.entityId))
            return false;
        return true;
    }

}
