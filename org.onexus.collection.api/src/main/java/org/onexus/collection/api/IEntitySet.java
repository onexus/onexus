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
 * <p>
 * A set of {@link IEntity}.
 * </p>
 * <p/>
 * <p>
 * This class extends {@link IEntity} class because it follows the Cursor
 * pattern. On each next() call the instance of the IEntitySet points to the
 * next entity.
 * </p>
 *
 * @author Jordi Deu-Pons
 */
public interface IEntitySet extends IEntity, Iterable<IEntity> {

    /**
     * Move the cursor to the next interface
     *
     * @return Returns true if there is a next value, false otherwise.
     */
    public boolean next();


    /**
     * This method is optionally implemented.
     *
     * @return The number of entities into this set.
     * @throws UnsupportedOperationException If it is not supported.
     */
    public long size();

    /**
     * The user must close the IEntitySet when it's not needed anymore.
     */
    public void close();

    /**
     * Clones the current cursor entity
     *
     * @return A new Entity instance detached of the EntitySet
     */
    public IEntity detachedEntity();

}
