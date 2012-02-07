/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.core;

import org.onexus.core.resources.Resource;

import java.util.List;

public interface IResourceManager {

    public enum ResourceStatus {SYNC, REMOVE, UPDATE, ADD}

    ;

    public <T extends Resource> T load(Class<T> resourceType, String resourceURI);

    public <T extends Resource> List<T> loadChildren(Class<T> resourceType,
                                                     String parentURI);

    public <T extends Resource> void save(T resource);

    public void remove(String resourceURI);

    public ResourceStatus status(String resourceURI);

    public void revert(String resourceURI);

    public void checkout();

    public void commit(String resourceURI);

}
