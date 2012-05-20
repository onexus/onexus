package org.onexus.ui.ws;

import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.onexus.ui.OnexusWebApplication;
import org.onexus.ui.ws.response.ListResourceResponse;
import org.onexus.ui.ws.response.QueryResponse;
import org.onexus.ui.ws.response.SingleResourceResponse;

import javax.servlet.http.HttpServletRequest;


public class WebserviceResource extends AbstractResource {

    public WebserviceResource() {
        super();
    }

    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {

        PageParameters parameters = attributes.getParameters();


        // Get server url
        HttpServletRequest request = (HttpServletRequest) attributes.getRequest().getContainerRequest();
        String url = request.getRequestURL().toString();

        // Parse parameters
        String query = parameters.get("query").toOptionalString();
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


}
