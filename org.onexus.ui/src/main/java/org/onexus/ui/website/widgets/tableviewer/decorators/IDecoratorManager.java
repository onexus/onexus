package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;

public interface IDecoratorManager {
    IDecorator getDecorator(String decoratorId,
                            Collection collection, Field field);
}
