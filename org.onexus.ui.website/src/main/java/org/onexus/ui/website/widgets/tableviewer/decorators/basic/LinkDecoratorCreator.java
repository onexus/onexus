package org.onexus.ui.website.widgets.tableviewer.decorators.basic;

import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.LinkDecorator;

public class LinkDecoratorCreator implements IDecoratorCreator{
    @Override
    public String getDecoratorId() {
        return "LINK";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new LinkDecorator(collection.getURI(), columnField);
    }
}
