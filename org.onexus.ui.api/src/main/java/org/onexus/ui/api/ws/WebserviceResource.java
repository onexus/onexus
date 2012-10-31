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
package org.onexus.ui.api.ws;

import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.onexus.ui.api.ws.response.DataResourceResponse;
import org.onexus.ui.api.ws.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;


public class WebserviceResource extends AbstractResource {

    private static final Logger log = LoggerFactory.getLogger(WebserviceResource.class);

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {

        PageParameters parameters = attributes.getParameters();


        // Get server url
        HttpServletRequest request = (HttpServletRequest) attributes.getRequest().getContainerRequest();
        String url = request.getRequestURL().toString();

        // Parse parameters
        String query = parameters.get("query").toOptionalString();

        if (query == null) {

            try {
                BufferedReader reader = request.getReader();

                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line + "\n");
                    line = reader.readLine();
                }
                reader.close();
                if (sb.length() > 0) {
                    query = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Boolean count = parameters.get("count").toBooleanObject();
        Boolean prettyPrint = parameters.get("prettyPrint").toOptionalBoolean();
        String format = parameters.get("format").toOptionalString();
        String filename = parameters.get("filename").toOptionalString();
        String data = parameters.get("data").toOptionalString();

        // Multiple collection query
        if (query != null) {
            return new QueryResponse(query, count, format, prettyPrint, filename);
        }

        // Get data
        if (data != null) {
            return new DataResourceResponse(data, url);
        }

        throw new AbortWithHttpErrorCodeException(404);

    }


    @Override
    public void respond(Attributes attributes) {


        ResourceResponse data = newResourceResponse(attributes);

        // set response header
        setResponseHeaders(data, attributes);

        try {
            data.getWriteCallback().writeData(attributes);
        } catch (IOException e) {
            log.error("Webservice response", e);
        }
    }
}
