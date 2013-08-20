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
package org.onexus.website.api.pages.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.onexus.website.api.widgets.selection.FilterConfig;
import org.onexus.website.api.widgets.selection.FiltersWidget;
import org.onexus.website.api.widgets.selection.FiltersWidgetStatus;

public abstract class SearchFiltersWidget extends FiltersWidget {

    public SearchFiltersWidget(String componentId, IModel<FiltersWidgetStatus> statusModel) {
        super(componentId, statusModel);
    }

    @Override
    protected abstract void applyFilter(FilterConfig filterConfig, AjaxRequestTarget target);

}
