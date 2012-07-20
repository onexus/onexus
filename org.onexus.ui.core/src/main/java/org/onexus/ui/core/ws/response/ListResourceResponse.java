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

import org.apache.wicket.request.Response;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.utils.ResourceUtils;

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
