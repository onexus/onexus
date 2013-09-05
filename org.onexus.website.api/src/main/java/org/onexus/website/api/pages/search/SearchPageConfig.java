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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.pages.search.figures.bar.BarFigureConfig;
import org.onexus.website.api.pages.search.figures.html.HtmlFigureConfig;
import org.onexus.website.api.pages.search.figures.table.TableFigureConfig;
import org.onexus.website.api.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("search")
@XStreamInclude({ HtmlFigureConfig.class, BarFigureConfig.class, TableFigureConfig.class })
public class SearchPageConfig extends PageConfig {

    private SearchPageStatus defaultStatus;

    @XStreamImplicit(itemFieldName = "type")
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
    public List<WidgetConfig> getWidgets() {
        return Collections.emptyList();
    }

    @Override
    public PageStatus createEmptyStatus() {
        return new SearchPageStatus();
    }

    @Override
    public PageStatus getDefaultStatus() {
        return defaultStatus;
    }


}
