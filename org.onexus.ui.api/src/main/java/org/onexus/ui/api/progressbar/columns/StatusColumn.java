package org.onexus.ui.api.progressbar.columns;


import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.data.api.Progress;

public class StatusColumn extends AbstractColumn<Progress, String> {


    public StatusColumn() {
        super(Model.of("status"));
    }


    @Override
    public void populateItem(Item<ICellPopulator<Progress>> cellItem, String componentId, IModel<Progress> rowModel) {

        String label = "<span class=\"label\">NA</span>";

        Progress progress = rowModel.getObject();

        if (progress!= null && progress.getStatus() != null) {
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
