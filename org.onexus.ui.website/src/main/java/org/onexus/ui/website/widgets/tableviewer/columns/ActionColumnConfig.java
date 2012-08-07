package org.onexus.ui.website.widgets.tableviewer.columns;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.Query;

import java.util.List;

@XStreamAlias("action")
public class ActionColumnConfig implements IColumnConfig {

    private String visible;

    private String decorator;

    @Override
    public void buildQuery(Query query) {
        // Nothing to do
    }

    @Override
    public void addColumns(List<IColumn<IEntityTable, String>> columns, String parentURI) {
        //TODO
    }

    @Override
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getDecorator() {
        return decorator;
    }

    public void setDecorator(String decorator) {
        this.decorator = decorator;
    }
}
