package org.onexus.ui.ws;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.caching.IStaticCacheableResource;
import org.apache.wicket.util.time.Duration;
import org.onexus.ui.OnexusWebApplication;
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

        // Multiple collection query
        if (query != null) {
            return new QueryResponse(query, count, format, prettyPrint);
        }

        // Single collection query
        if (select != null) {
            return new QueryResponse(url, select, where, orderBy, limit, count, format, prettyPrint);
        }

        // Get children
        if (children != null) {
            return new ListResourceResponse(url, children, where, recursive, format, prettyPrint);
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
