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
package org.onexus.website.widget.icanplot;

import org.apache.wicket.markup.head.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.query.Query;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.widget.Widget;

import javax.inject.Inject;
import java.net.URLEncoder;


public class PlotViewer extends Widget<PlotViewerConfig, PlotViewerStatus> {

    public PlotViewer(String componentId, IModel<PlotViewerStatus> statusModel) {
        super(componentId, statusModel);
    }

    private static final HeaderItem CSS = CssHeaderItem.forReference(new CssResourceReference(PlotViewer.class, "PlotViewer.css"));
    private static final HeaderItem JS_ICANPLOT = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "icanplot.js"));
    private static final HeaderItem JS_ICANPLOT_ONEXUS = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "icanplot-onexus.js"));
    private static final HeaderItem JS_ICANPLOT_DATA = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(PlotViewer.class, "data.js"));

    @Inject
    private ICollectionManager collectionManager;

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
            defaultCols = defaultCols + (fields.getX() == null ? "0" : fields.getX()) + ", ";
            defaultCols = defaultCols + (fields.getY() == null ? "0" : fields.getY()) + ", ";
            defaultCols = defaultCols + (fields.getColor() == null ? "-1" : fields.getColor()) + ", ";
            defaultCols = defaultCols + (fields.getSize() == null ? "-1" : fields.getSize()) + "]";
        }

        return "icanplot_init('" + getQueryUrl() + "', " + defaultCols + ", 'ilinear', 'ilinear');";
    }

    private String getQueryUrl() {
        String serviceMount = collectionManager.getMount();
        String webserviceUrl = WebsiteApplication.toAbsolutePath('/' + serviceMount);
        Query query = getQuery();

        String strQuery = query.toString();
        String fileName = "file-" + Integer.toHexString(strQuery.hashCode()) + ".tsv";

        return webserviceUrl + "?query=" + URLEncoder.encode(strQuery) + "&filename=" + fileName;
    }
}
