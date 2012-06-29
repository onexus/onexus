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
package org.onexus.ui.ws;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.ws.response.DataResourceResponse;
import org.onexus.ui.ws.response.ListResourceResponse;
import org.onexus.ui.ws.response.QueryResponse;
import org.onexus.ui.ws.response.SingleResourceResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;


public class WebserviceResource extends AbstractResource {

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

        String children = parameters.get("children").toOptionalString();
        String select = parameters.get("select").toOptionalString();
        String orderBy = parameters.get("orderby").toOptionalString();
        String limit = parameters.get("limit").toOptionalString();
        Boolean count = parameters.get("count").toBooleanObject();
        String where = parameters.get("where").toOptionalString();
        String recursive = parameters.get("recursive").toOptionalString();
        Boolean prettyPrint = parameters.get("prettyPrint").toOptionalBoolean();
        String format = parameters.get("format").toOptionalString();
        String filename = parameters.get("filename").toOptionalString();
        String data = parameters.get("data").toOptionalString();

        // Multiple collection query
        if (query != null) {
            return new QueryResponse(query, count, format, prettyPrint, filename);
        }

        // Single collection query
        if (select != null) {
            return new QueryResponse(url, select, where, orderBy, limit, count, format, prettyPrint);
        }

        // Get children
        if (children != null) {
            return new ListResourceResponse(url, children, where, recursive, format, prettyPrint);
        }

        // Get data
        if (data != null) {
            return new DataResourceResponse(data, url);
        }

        // Get resource
        return new SingleResourceResponse(url, format, prettyPrint);

    }


    @Override
    public void respond(Attributes attributes) {
        ResourceResponse data = newResourceResponse(attributes);

        // set response header
        setResponseHeaders(data, attributes);

        data.getWriteCallback().writeData(attributes);
    }
}
