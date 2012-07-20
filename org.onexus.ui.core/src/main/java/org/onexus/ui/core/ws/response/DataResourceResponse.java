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
package org.onexus.ui.core.ws.response;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.encoding.UrlDecoder;
import org.apache.wicket.util.io.Streams;
import org.onexus.resource.api.IDataManager;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.resources.Project;
import org.onexus.resource.api.utils.ResourceUtils;
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

    @Inject
    public IResourceManager resourceManager;

    private String data;
    private String filename;
    private String resourceUri;

    public DataResourceResponse(String data, String url) {
        super();

        this.data = data;
        int pos = url.indexOf(data) + data.length() + 1;
        this.filename = UrlDecoder.PATH_INSTANCE.decode(url.substring(pos), "UTF-8");
        this.resourceUri = null;
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

            if (resourceUri == null) {

                this.resourceUri = ResourceUtils.concatURIs(data, filename);

                for (Project project : resourceManager.getProjects()) {
                    String projectHash = Integer.toHexString(project.getURI().hashCode());
                    if (data.equals(projectHash)) {
                        this.resourceUri = ResourceUtils.concatURIs(project.getURI(), filename);
                        break;
                    }
                }
            }

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
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }

    }
}
