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
package org.onexus.website.widgets.map;

import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.website.api.widgets.WidgetStatus;

public class MapWidgetStatus extends WidgetStatus<MapWidgetConfig> {

    public MapWidgetStatus() {
        super();
    }

    public MapWidgetStatus(String widgetId) {
        super(widgetId);
    }

    @Override
    public void onQueryBuild(Query query) {
        MapWidgetConfig config = getConfig();
        String collectionAlias = QueryUtils.newCollectionAlias(query, config.getCollection());
    }

}
