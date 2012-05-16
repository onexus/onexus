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
package org.onexus.ui.website.utils.visible;

import org.apache.commons.collections.Predicate;
import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.utils.EntityModel;
import org.onexus.ui.website.utils.FixedEntity;

import java.util.Set;

public class FixedEntitiesVisiblePredicate implements Predicate {

    private String releaseURI;
    private Set<FixedEntity> fixedEntities;

    public FixedEntitiesVisiblePredicate(String releaseURI,
                                         Set<FixedEntity> fixedEntities) {
        super();

        this.releaseURI = releaseURI;
        this.fixedEntities = fixedEntities;
    }

    @Override
    public boolean evaluate(Object object) {

        IVisible visible = (IVisible) object;

        if (object == null) {
            return true;
        }

        String visibleQuery = visible.getVisible();

        if (visibleQuery == null) {
            return true;
        }

        String visibleRules[] = visibleQuery.split(",");

        for (String rule : visibleRules) {
            if (rule.isEmpty()) {
                continue;
            }

            if (!isVisible(rule)) {
                return false;
            }

        }

        return true;

    }

    private boolean isVisible(String rule) {

        rule = rule.trim();

        String collectionURI = getRuleCollection(rule);
        String entityId = getEntityId(rule);
        boolean negative = isNegative(rule);

        for (FixedEntity fe : fixedEntities) {

            if (
                    ResourceUtils.getAbsoluteURI(releaseURI, fe.getCollectionURI()).equals(collectionURI) &&
                            isValidEntity(entityId, fe)
                    ) {
                return !negative;
            }

        }

        return negative;
    }

    private boolean isValidEntity(String entityId, FixedEntity fe) {

        if (entityId == null) {
            return true;
        }

        int equal = entityId.indexOf('=');

        if (equal == -1) {
            return entityId.equals(fe.getEntityId());
        }

        String fieldName = entityId.substring(0, equal);
        String entityValue = entityId.substring(equal + 1);

        IModel<IEntity> entity = new EntityModel(fe);

        return entityValue.equals(entity.getObject().get(fieldName));

    }

    private boolean isNegative(String rule) {
        return rule.startsWith("!");
    }

    private String getRuleCollection(String rule) {

        String collectionURI = rule.replace("!", "");

        int ini = collectionURI.indexOf("[");
        if (ini != -1) {
            collectionURI = collectionURI.substring(0, ini);
        }

        if (releaseURI != null && !releaseURI.isEmpty()) {
            collectionURI = ResourceUtils.getAbsoluteURI(releaseURI,
                    collectionURI);
        }

        return collectionURI;
    }

    private String getEntityId(String rule) {

        int ini = rule.indexOf("[");

        if (ini == -1) return null;

        int end = rule.indexOf("]");

        if (end == -1 || (end - ini) < 1) {
            return null;
        }

        return rule.substring(ini + 1, end);
    }


}
