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
package org.onexus.collection.api;

/**
 * <p>An entity is one piece of identifiable data in a Collection.</p>
 * <p/>
 * <p>All the entities have the same fields.</p>
 *
 * @author Jordi Deu-Pons
 */
public interface IEntity {

    /**
     * @return The unique identifier of this entity.
     */
    String getId();


    /**
     * @return The owner collection.
     */
    Collection getCollection();

    /**
     * @param fieldId The ID of the field.
     * @return The value associated to this field.
     */
    Object get(String fieldId);


    /**
     * @param fieldName The name of the field.
     * @param value     The value to put into this field.
     */
    void put(String fieldName, Object value);

}
