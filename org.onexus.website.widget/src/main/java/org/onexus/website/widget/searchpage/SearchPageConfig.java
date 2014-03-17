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
package org.onexus.website.widget.searchpage;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.resource.api.annotations.ResourceRegister;
import org.onexus.website.api.widget.WidgetConfig;
import org.onexus.website.api.widget.WidgetStatus;
import org.onexus.website.widget.searchpage.figures.bar.BarFigureConfig;
import org.onexus.website.widget.searchpage.figures.html.HtmlFigureConfig;
import org.onexus.website.widget.searchpage.figures.table.TableFigureConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ResourceAlias("search")
@ResourceRegister({HtmlFigureConfig.class, BarFigureConfig.class, TableFigureConfig.class, SearchPageStatus.class, SearchType.class, SearchLink.class})
public class SearchPageConfig extends WidgetConfig {

    @Valid
    private SearchPageStatus defaultStatus;

    @NotNull @Valid
    @ResourceImplicitList("type")
    private List<SearchType> types = new ArrayList<SearchType>();

    public SearchPageConfig() {
        super();
    }

    public List<SearchType> getTypes() {
        return types;
    }

    public void setTypes(List<SearchType> types) {
        this.types = types;
    }

    @Override
    public List<WidgetConfig> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public WidgetStatus createEmptyStatus() {
        return new SearchPageStatus();
    }

    @Override
    public WidgetStatus getDefaultStatus() {
        return defaultStatus;
    }


}
