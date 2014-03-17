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
package org.onexus.website.widgets.mocks;

import org.onexus.resource.api.IResourceSerializer;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.exceptions.UnserializeException;

import java.io.InputStream;
import java.io.OutputStream;

public class MockResourceSerializer implements IResourceSerializer {

    @Override
    public String getMediaType() {
        return "text/xml";
    }

    @Override
    public void serialize(Resource resource, OutputStream output) {

    }

    @Override
    public <T extends Resource> T unserialize(Class<T> resourceType, ORI resourceOri, InputStream input) throws UnserializeException {
        return null;
    }

    @Override
    public void register(Class<? extends Resource> type) {

    }
}
