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

import org.apache.wicket.model.IModel;
import org.onexus.website.api.widget.AbstractWidgetCreator;
import org.onexus.website.api.widget.Widget;

public class PlotViewerCreator extends AbstractWidgetCreator<PlotViewerConfig, PlotViewerStatus> {


    public PlotViewerCreator() {
        super(PlotViewerConfig.class, "viewer-plot", "Interactive plot viewer");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<PlotViewerStatus> statusModel) {
        return new PlotViewer(componentId, statusModel);
    }
}
