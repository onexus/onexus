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
package org.onexus.ui.api.ws.response;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.encoding.UrlDecoder;
import org.apache.wicket.util.io.Streams;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataResourceResponse extends AbstractResponse {

    private static final Logger log = LoggerFactory.getLogger(DataResourceResponse.class);

    @PaxWicketBean(name="dataManager")
    private IDataManager dataManager;

    @PaxWicketBean(name="resourceManager")
    private IResourceManager resourceManager;

    private ORI resourceUri;

    public DataResourceResponse(String data, String url) {
        super();

        int pos = url.indexOf(data) + data.length() + 1;
        String filename = UrlDecoder.PATH_INSTANCE.decode(url.substring(pos), "UTF-8");

        for (Project project : resourceManager.getProjects()) {
            String projectHash = Integer.toHexString(project.getURL().hashCode());
            if (data.equals(projectHash)) {
                this.resourceUri = new ORI(project.getURL(), filename);
                break;
            }
        }

        // Content length
        setContentLength(dataManager.size(resourceUri));

    }

    @Override
    protected void writeData(final Response response) {

        OutputStream s = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                response.write(new byte[]{(byte) b});
            }

            @Override
            public void write(byte[] b) throws IOException {
                response.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                response.write(b, off, len);
            }
        };
        try {

            IDataStreams dataStreams = dataManager.load(resourceUri);

            for (InputStream stream : dataStreams) {
                Streams.copy(stream, s);
            }

        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }

    }

}
