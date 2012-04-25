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
package org.onexus.ui.website.widgets.heatmap;

import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.onexus.core.query.Query;
import org.onexus.ui.website.widgets.export.ExportResource;
import org.onexus.ui.website.widgets.tableviewer.columns.ColumnConfig;

import java.util.ArrayList;
import java.util.List;

public class HeatmapPage extends WebPage implements IResourceListener {
    
    private HeatmapViewerConfig config;
    private IResource resource;
    private int lastColumnFields;
    private int lastRowFields;

    public HeatmapPage(HeatmapViewerConfig config, Query query) {
        super();
        this.config = config;

        List<ColumnConfig.ExportColumn> exportColumns = new ArrayList<ColumnConfig.ExportColumn>();

        for (ColumnConfig column : config.getColumns() ) {
            column.addExportColumns(exportColumns, query.getMainNamespace());
        }
        
        this.lastColumnFields = countColumns(exportColumns);

        for (ColumnConfig column : config.getRows() ) {
            column.addExportColumns(exportColumns, query.getMainNamespace());
        }
        
        this.lastRowFields = countColumns(exportColumns);

        for (ColumnConfig column : config.getCells() ) {
            column.addExportColumns(exportColumns, query.getMainNamespace());
        }

        this.resource = new StatefulExportResource(query, exportColumns);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderJavaScript( getDynamicJavascript() , "jheatmap-init");

    }


    public CharSequence getDynamicJavascript() {
                
        StringBuilder code = new StringBuilder();

        code.append( "$(document).ready(function() {\n" +
                "\t\t$('#heatmap').heatmap({\n" +
                        "\t\t\tdata : {\n" +
                        "\t\t\t\ttype : \"tdm\",\n" +
                        "\t\t\t\tvalues : '" + urlFor(IResourceListener.INTERFACE, null) + "&antiCache=" + System.currentTimeMillis() + "', \n");

        // Column annotations
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

        return code.toString();
    }

    @Override
    public void onResourceRequested() {
        IResource.Attributes a = new IResource.Attributes(RequestCycle.get().getRequest(), RequestCycle.get()
                .getResponse(), null);
        this.resource.respond(a);
    }
    
    private int countColumns(List<ColumnConfig.ExportColumn> columns ) {
        
        int count = 0;
        for (ColumnConfig.ExportColumn column : columns) {
               count += column.getFieldNames().length;
        }
        
        return count;
        
    }

    private static class StatefulExportResource extends ExportResource {

        private Query query;
        private List<ColumnConfig.ExportColumn> columns;

        private StatefulExportResource(Query query, List<ColumnConfig.ExportColumn> columns) {
            this.query = query;
            this.columns = columns;
        }

        protected WriteCallback newWriteCallback() {

            return new WriteCallback() {
                @Override
                public void writeData(final Attributes attributes) {
                    writeTSV(attributes.getResponse(), query, columns);
                }
            };

        }
    }
}
