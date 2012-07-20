package org.onexus.ui.website.widgets.tableviewer.decorators.basic;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.CTDecorator;

public class CtDecoratorCreator implements IDecoratorCreator {

    @Override
    public String getDecoratorId() {
        return "CT";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new CTDecorator(columnField, null);
    }
}
