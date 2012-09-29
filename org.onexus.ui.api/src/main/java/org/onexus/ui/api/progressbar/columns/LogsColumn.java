package org.onexus.ui.api.progressbar.columns;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.data.api.Progress;

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
