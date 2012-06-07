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
package org.onexus.ui.ws.response;

import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.resources.Resource;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;

public class SingleResourceResponse extends AbstractResponse {

    @Inject
    public IResourceManager resourceManager;

    @Inject
    public IResourceSerializer resourceSerializer;

    private String resourceUri;
    private String format;
    private boolean prettyPrint;


    public SingleResourceResponse(String url, String format, Boolean prettyPrint) {
        super();

        this.resourceUri = url.toString();
        this.format = (format == null) ? "xml" : format;
        this.prettyPrint = ( prettyPrint == null) ? true : prettyPrint;

        setContentType("application/" + this.format);
    }

    @Override
    protected void writeData(Response response) {

        Resource resource = resourceManager.load(Resource.class, resourceUri);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        resourceSerializer.serialize(resource, output);

        response.write( output.toByteArray() );

    }
}
