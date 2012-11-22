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


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.resource.api.Progress;

public abstract class LogsColumn extends AbstractColumn<Progress, String> {

    public LogsColumn() {
        super(Model.of("logs"));
    }

    @Override
    public void populateItem(Item<ICellPopulator<Progress>> cellItem, String componentId, final IModel<Progress> rowModel) {

        cellItem.add(new LogLink(componentId) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                showLogs(rowModel.getObject(), target);
            }
        });
    }

    protected abstract void showLogs(Progress progress, AjaxRequestTarget target);
}
