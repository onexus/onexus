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
package org.onexus.ui.website.widgets.filters;

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.website.WebsiteConfig;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.IWidgetModel;
import org.onexus.ui.website.widgets.Widget;

public class FiltersWidgetCreator extends AbstractWidgetCreator<FiltersWidgetConfig, FiltersWidgetStatus> {

    public FiltersWidgetCreator() {
        super(FiltersWidgetConfig.class, "filters-widget", "Add predefined filters");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IWidgetModel<FiltersWidgetStatus> statusModel) {
        return new FiltersWidget(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        resourceRegister.register(FiltersWidgetConfig.class);
        resourceRegister.register(FieldSelection.class);
        resourceRegister.register(FilterConfig.class);

        //resourceRegister.addAutoComplete(WebsiteConfig.class, "widgets",  "<id>[tab-id]</id>");
    }

}
