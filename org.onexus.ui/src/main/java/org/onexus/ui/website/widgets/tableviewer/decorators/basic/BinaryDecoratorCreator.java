package org.onexus.ui.website.widgets.tableviewer.decorators.basic;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.ColorDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.BinaryColorScale;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.IColorScaleHtml;

import java.awt.*;

public class BinaryDecoratorCreator implements IDecoratorCreator {

    private final static IColorScaleHtml binaryScale = new BinaryColorScale(0.0, 1.0, 1.0, Color.LIGHT_GRAY, Color.RED, Color.WHITE);

    @Override
    public String getDecoratorId() {
        return "BINARY";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new ColorDecorator(columnField, binaryScale, "st");
    }
}
