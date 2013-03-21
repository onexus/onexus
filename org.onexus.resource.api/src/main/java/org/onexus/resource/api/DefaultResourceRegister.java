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
package org.onexus.resource.api;

public class DefaultResourceRegister implements IResourceRegister {

    private IResourceSerializer serializer;

    public DefaultResourceRegister() {
    }

    @Override
    public void register(Class<?> resourceType) {
        serializer.register(resourceType);
    }

    @Override
    public void unregister(Class<?> resourceType) {

    }

    public IResourceSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(IResourceSerializer serializer) {
        this.serializer = serializer;
    }
}