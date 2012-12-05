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
package org.onexus.website.api.widgets.jheatmap;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.*;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryPluginResourceReference;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.tableviewer.columns.ColumnConfig;
import org.ops4j.pax.wicket.api.PaxWicketBean;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HeatmapViewer extends Widget<HeatmapViewerConfig, HeatmapViewerStatus> {

    private final static HeaderItem CSS = CssHeaderItem.forReference(new CssResourceReference(HeatmapViewer.class, "css/jheatmap-1.0.0-alpha.css"));
    private final static HeaderItem JS_JHEATMAP = JavaScriptHeaderItem.forReference(new JQueryPluginResourceReference(HeatmapViewer.class, "js/jheatmap-1.0.0-alpha.js"));

    private final static ResourceReference LOADING_IMG = new PackageResourceReference(HeatmapViewer.class, "images/loading.gif");


    @PaxWicketBean(name = "collectionManager")
    private ICollectionManager collectionManager;

    public HeatmapViewer(String componentId, IModel<HeatmapViewerStatus> status) {
        super(componentId, status);

        add(new Image("loading", LOADING_IMG));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CSS);
        response.render(JS_JHEATMAP);
        response.render(OnLoadHeaderItem.forScript(newJavaScriptHeatmap()));
    }

    private String newJavaScriptHeatmap() {
        StringBuilder code = new StringBuilder();

        HeatmapViewerConfig config = getConfig();

        List<IColumn<IEntityTable, String>> columns = new ArrayList<IColumn<IEntityTable, String>>();

        ORI parentUri = getPageBaseOri();
        for (ColumnConfig column : getConfig().getColumns()) {
            column.addColumns(columns, parentUri);
        }
        int lastColumnFields = columns.size();
        columns.clear();

        for (ColumnConfig row : getConfig().getRows()) {
            row.addColumns(columns, parentUri);
        }
        int lastRowFields = lastColumnFields + columns.size();
        columns.clear();

        // Webservice URL
        code.append(
                "$('#heatmap').heatmap({\n" +
                        "       data : {\n" +
                        "                 type : \"tdm\",\n" +
                        "                 values : '" + getQueryUrl() + "', \n");

        code.append("\t\t\t\tcols_annotations : [");
        if (config.getColAnnotations() == null) {
            for (int i = 0; i < lastColumnFields; i++) {
                if (i != 0) {
                    code.append(",");
                }
                code.append(i);
            }
        } else {
            code.append(config.getColAnnotations());
        }
        code.append("], \n");
        code.append("\t\t\t\trows_annotations : [");
        if (config.getRowAnnotations() == null) {
            for (int i = lastColumnFields; i < lastRowFields; i++) {
                if (i != lastColumnFields) {
                    code.append(",");
                }
                code.append(i);
            }
        } else {
            code.append(config.getRowAnnotations());
        }
        code.append("]\n" +
                "              },\n" +
                "       init : function(heatmap) {\n");
        code.append(config.getInit());
        code.append("}});");

        return code.toString();
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
