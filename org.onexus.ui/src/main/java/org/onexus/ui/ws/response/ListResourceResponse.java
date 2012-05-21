package org.onexus.ui.ws.response;

import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceUtils;

import javax.inject.Inject;
import java.util.List;

public class ListResourceResponse extends AbstractResponse {

    @Inject
    public IResourceManager resourceManager;

    private String url;
    private String children;
    private String where;
    private String recursive;
    private String format;
    private boolean prettyPrint;

    public ListResourceResponse(String url, String children, String where, String recursive, String format, Boolean prettyPrint) {
        super();

        this.url = url;
        this.children = children;
        this.where = where;
        this.recursive = recursive;
        this.format = (format == null) ? "tsv" : format;
        this.prettyPrint = (prettyPrint == null) ? true : prettyPrint;

        setContentType("text/tab-separated-values");
        setFileName(ResourceUtils.getResourceName(url) + "-children.tsv");
    }

    @Override
    protected void writeData(Response response) {

        List<Resource> resources = resourceManager.loadChildren(Resource.class, url);

        for (Resource resource : resources) {
            response.write(resource.getURI() + "\n");
        }

    }
}
