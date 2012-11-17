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
package org.onexus.website.api.widgets.icanplot;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.collection.api.query.Query;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.widgets.Widget;


public class PlotViewer extends Widget<PlotViewerConfig, PlotViewerStatus> implements IResourceListener {

    public PlotViewer(String componentId, IModel<PlotViewerStatus> statusModel) {
        super(componentId, statusModel);
    }

    private final static HeaderItem CSS = CssHeaderItem.forReference(new CssResourceReference(PlotViewer.class, "PlotViewer.css"));
    private final static HeaderItem JS_ICANPLOT = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "icanplot.js"));
    private final static HeaderItem JS_ICANPLOT_ONEXUS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "icanplot-onexus.js"));
    private final static HeaderItem JS_ICANPLOT_DATA = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "data.js"));

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CSS);
        response.render(JS_ICANPLOT);
        response.render(JS_ICANPLOT_DATA);
        response.render(JS_ICANPLOT_ONEXUS);
        response.render(OnLoadHeaderItem.forScript(newJavaScriptPlot()));
    }

    private String newJavaScriptPlot() {

        PlotFields fields = getStatus().getFields();

        String defaultCols = "[0, 0, -1, -1]";
        if (fields != null) {
            defaultCols = "[";
            defaultCols = defaultCols + (fields.getX()==null ? "0" : fields.getX()) + ", ";
            defaultCols = defaultCols + (fields.getY()==null ? "0" : fields.getY()) + ", ";
            defaultCols = defaultCols + (fields.getColor()==null ? "-1" : fields.getColor()) + ", ";
            defaultCols = defaultCols + (fields.getSize()==null ? "-1" : fields.getSize()) + "]";
        }

        return "icanplot_init('" + urlFor(IResourceListener.INTERFACE, new PageParameters().set("ac", Integer.toHexString((int)System.currentTimeMillis()))) + "', "+defaultCols+", 'ilinear', 'ilinear');";
    }

    @Override
    public void onResourceRequested() {

        Query query = getQuery();
        String fileName = "file-" + Integer.toHexString(query.hashCode()) + ".tsv";
        PageParameters params = new PageParameters();
        params.add("query", query);
        params.add("prettyPrint", true);
        params.add("filename", fileName);

        /*TODO
        ResourceReference webservice = WebsiteApplication.get().getWebService();
        IResource resource = webservice.getResource();
        IResource.Attributes a = new IResource.Attributes(RequestCycle.get().getRequest(), RequestCycle.get().getResponse(), params);
        resource.respond(a);
        */

    }
}
