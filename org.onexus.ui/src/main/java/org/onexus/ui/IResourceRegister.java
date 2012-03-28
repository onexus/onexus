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
package org.onexus.ui;

import java.util.List;
import java.util.Map;

public interface IResourceRegister {

    public void register(Class<?> resourceType);

    public ClassLoader getResourcesClassLoader();
    
    public void addAutocompleteHint(Class<?> resourceType, String parentTag, String hint);
    
    public Map<String, List<String>> getAutocompleteMap(Class<?> resourceType);
}
