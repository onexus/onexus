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
package org.onexus.website.api.pages.browser.filters;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.widgets.WidgetConfig;
import org.onexus.website.api.widgets.WidgetStatus;

import javax.validation.Valid;

@ResourceAlias("filters-toolbar")
public class FiltersToolbarConfig extends WidgetConfig {

    @Valid
    private FiltersToolbarStatus defaultStatus;

    public FiltersToolbarConfig() {
        super();
    }

    public FiltersToolbarStatus getDefaultStatus() {
        return defaultStatus;
    }

    @Override
    public WidgetStatus createEmptyStatus() {
        return new FiltersToolbarStatus();
    }

    public void setDefaultStatus(FiltersToolbarStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
