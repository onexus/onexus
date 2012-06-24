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

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.resource.FileResourceStream;
import org.onexus.core.IDataManager;
import org.onexus.core.IResourceManager;
import org.onexus.core.IResourceSerializer;
import org.onexus.core.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class DataResourceResponse extends AbstractResponse {

    private static final Logger log = LoggerFactory.getLogger(DataResourceResponse.class);
    @Inject
    public IDataManager dataManager;

    private String resourceUri;

    public DataResourceResponse(String resourceUri) {
        super();
        this.resourceUri = resourceUri;

    }

    @Override
    protected void writeData(final Response response) {




        OutputStream s = new OutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                response.write(new byte[] { (byte)b });
            }

            @Override
            public void write(byte[] b) throws IOException
            {
                response.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException
            {
                response.write(b, off, len);
            }
        };
        try
        {
            List<URL> urls = dataManager.retrieve(resourceUri);

            if (!urls.isEmpty()) {

                URI uri = null;
                try {
                    uri = urls.get(0).toURI();
                    InputStream stream = new FileInputStream(new File(uri));
                    Streams.copy(stream, s);
                } catch (URISyntaxException e) {
                    log.error("Malformed URI", e);
                }
            }
        }
        catch (IOException e)
        {
            throw new WicketRuntimeException(e);
        }

    }
}
