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
package org.onexus.website.widget.details;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.website.api.widget.WidgetConfig;
import org.onexus.website.api.widget.WidgetStatus;
import org.onexus.website.widget.tableviewer.columns.ColumnConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ResourceAlias("widget-details")
public class DetailsWidgetConfig extends WidgetConfig {

    @ResourceImplicitList("entity")
    private List<DetailsEntity> entities = new ArrayList<DetailsEntity>();

    public DetailsWidgetConfig() {
        super();
    }

    public List<DetailsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<DetailsEntity> entities) {
        this.entities = entities;
    }

    @Override
    public List<WidgetConfig> getChildren() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public WidgetStatus createEmptyStatus() {
        return new DetailsWidgetStatus();
    }

    @Override
    public WidgetStatus getDefaultStatus() {
        return null;
    }


}
