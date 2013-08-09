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
package org.onexus.website.api.widgets.filters;

import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceRegister;
import org.onexus.website.api.widgets.AbstractWidgetCreator;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.filters.custom.CustomFilter;

public class FiltersToolbarCreator extends AbstractWidgetCreator<FiltersToolbarConfig, FiltersToolbarStatus> {

    public FiltersToolbarCreator() {
        super(FiltersToolbarConfig.class, "filters-toolbar", "Add predefined filters");
    }

    @Override
    protected Widget<?, ?> build(String componentId, IModel<FiltersToolbarStatus> statusModel) {
        return new FiltersToolbar(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        resourceRegister.register(FiltersToolbarConfig.class);
    }

}
