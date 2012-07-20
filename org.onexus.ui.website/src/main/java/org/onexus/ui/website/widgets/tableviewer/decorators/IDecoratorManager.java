package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.onexus.resource.api.resources.Collection;
import org.onexus.resource.api.resources.Field;

public interface IDecoratorManager {
    IDecorator getDecorator(String decoratorId,
                            Collection collection, Field field);
}
