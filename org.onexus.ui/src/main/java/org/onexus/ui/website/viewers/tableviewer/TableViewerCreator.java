package org.onexus.ui.website.viewers.tableviewer;

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.website.viewers.AbstractCollectionViewerCreator;
import org.onexus.ui.website.viewers.Viewer;
import org.onexus.ui.website.viewers.tableviewer.columns.ColumnConfig;

public class TableViewerCreator extends AbstractCollectionViewerCreator<TableViewerConfig, TableViewerStatus> {

    public TableViewerCreator() {
	super(TableViewerConfig.class, "table-viewer", "Collection table viewer" );
    }

    @Override
    protected Viewer<?, ?> build(String componentId, TableViewerConfig config, IModel<TableViewerStatus> statusModel) {
	return new TableViewer(componentId, config, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
	resourceRegister.register(TableViewerConfig.class);
	resourceRegister.register(ColumnConfig.class);
    }
    
    

}
