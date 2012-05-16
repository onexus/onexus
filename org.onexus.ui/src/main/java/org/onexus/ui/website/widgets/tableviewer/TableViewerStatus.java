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
package org.onexus.ui.website.widgets.tableviewer;

import org.onexus.core.query.OrderBy;
import org.onexus.ui.website.widgets.WidgetStatus;

public class TableViewerStatus extends WidgetStatus {

    private OrderBy order;
    
    private int currentColumnSet;

    public TableViewerStatus() {
        super();
    }

    public TableViewerStatus(String viewerId) {
        super(viewerId);
    }

    public TableViewerStatus(String viewerId, OrderBy order) {
        super(viewerId);
        this.order = order;
    }

    public OrderBy getOrder() {
        return order;
    }

    public void setOrder(OrderBy order) {
        this.order = order;
    }

    public int getCurrentColumnSet() {
        return currentColumnSet;
    }

    public void setCurrentColumnSet(int currentColumnSet) {
        this.currentColumnSet = currentColumnSet;
    }
}
