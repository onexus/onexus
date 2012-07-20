package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;

public interface IDecoratorManager {
    IDecorator getDecorator(String decoratorId,
                            Collection collection, Field field);
}
