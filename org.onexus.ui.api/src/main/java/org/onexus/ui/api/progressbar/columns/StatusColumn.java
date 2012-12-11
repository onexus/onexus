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
package org.onexus.ui.api.progressbar.columns;


import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.resource.api.Progress;

public class StatusColumn extends AbstractColumn<Progress, String> {


    public StatusColumn() {
        super(Model.of("status"));
    }


    @Override
    public void populateItem(Item<ICellPopulator<Progress>> cellItem, String componentId, IModel<Progress> rowModel) {

        String label = "<span class=\"label\">NA</span>";

        Progress progress = rowModel.getObject();

        if (progress != null && progress.getStatus() != null) {
            switch (progress.getStatus()) {
                case DONE:
                    label = "<span class=\"label label-success\">done</span>";
                    break;
                case FAILED:
                    label = "<span class=\"label label-important\">failed</span>";
                    break;
                case RUNNING:
                    label = "<span class=\"label label-warning\">running</span>";
                    break;
                case CANCELED:
                    label = "<span class=\"label label-inverse\">canceled</span>";
                    break;
                case WAITING:
                    label = "<span class=\"label label-info\">waiting</span>";
                    break;
                default:
                    label = "<span class=\"label\">Unknown</span>";
            }
        }

        cellItem.add(new Label(componentId, label).setEscapeModelStrings(false));
    }
}
