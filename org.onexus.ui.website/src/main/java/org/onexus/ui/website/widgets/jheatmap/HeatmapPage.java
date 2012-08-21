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
package org.onexus.ui.website.widgets.jheatmap;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.collection.api.query.Query;
import org.onexus.ui.api.OnexusWebApplication;

public class HeatmapPage extends WebPage implements IResourceListener {

    private HeatmapViewerConfig config;
    private Query query;

    public HeatmapPage(HeatmapViewerConfig config, Query query) {
        super();
        this.config = config;
        this.query = query;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forScript(getDynamicJavascript(), "jheatmap-init"));

    }


    public CharSequence getDynamicJavascript() {
                
        StringBuilder code = new StringBuilder();

        code.append( "$(document).ready(function() {\n" +
                "\t\t$('#heatmap').heatmap({\n" +
                        "\t\t\tdata : {\n" +
                        "\t\t\t\ttype : \"tdm\",\n" +
                        "\t\t\t\tvalues : '" + urlFor(IResourceListener.INTERFACE, null) + "&antiCache=" + System.currentTimeMillis() + "', \n");

        // Column annotations
        /*TODO
        code.append("\t\t\t\tcols_annotations : [");
        for (int i=0; i < lastColumnFields; i++) {
            if (i!=0) {
                code.append(",");
            }
            code.append(i);
        }
        code.append("], \n");
        code.append("\t\t\t\trows_annotations : [");
        for (int i= lastColumnFields; i < lastRowFields; i++) {
            if (i!=lastColumnFields) {
                code.append(",");
            }
            code.append(i);
        }
        code.append("]\n");
        code.append(
                        "\t\t\t},\n" +
                        "\n" +
                        "\t\t\tinit : function(heatmap) {\n");
        code.append( config.getInit() );
        code.append(    "\t\t\t}\n" +
                        "\t\t});\n" +
                        "\t});");
                        */

        return code.toString();
    }

    @Override
    public void onResourceRequested() {

        String fileName = "file-" + Integer.toHexString(query.hashCode()) + ".tsv";
        PageParameters params = new PageParameters();
        params.add("query", query);
        params.add("filename", fileName);

        ResourceReference webservice = OnexusWebApplication.get().getWebService();
        IResource resource = webservice.getResource();
        IResource.Attributes a = new IResource.Attributes(RequestCycle.get().getRequest(), RequestCycle.get().getResponse(), params);
        resource.respond(a);

    }

}
