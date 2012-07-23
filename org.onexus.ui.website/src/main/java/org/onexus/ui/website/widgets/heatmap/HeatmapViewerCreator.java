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

import org.apache.wicket.model.IModel;
import org.onexus.ui.api.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class HeatmapViewerCreator extends AbstractWidgetCreator<HeatmapViewerConfig, HeatmapViewerStatus> {

    public HeatmapViewerCreator() {
        super(HeatmapViewerConfig.class, "heatmap-viewer", "Heatmap viewer");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<HeatmapViewerStatus> statusModel) {
        return new HeatmapViewer(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        resourceRegister.register(HeatmapViewerConfig.class);
        resourceRegister.addAutoComplete(WebsiteConfig.class, "widgets", "<viewer-heatmap>\n" +
                "          <id>heatmap</id>\n" +
                "          <collection>[main collection]</collection>\n" +
                "          <init></init>\n" +
                "          <column>\n" +
                "            <collection>[column collection]</collection>\n" +
                "            <fields>[field names]</fields>\n" +
                "          </column>\n" +
                "          <row>\n" +
                "            <collection>[row collection]</collection>\n" +
                "            <fields>[field names]</fields>\n" +
                "          </row>\n" +
                "          <cell>\n" +
                "            <collection>[cell collection]</collection>\n" +
                "            <fields>[field names]</fields>\n" +
                "          </cell>\n" +
                "        </viewer-heatmap>");
    }


}
