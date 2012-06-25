package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.ColorDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.IColorScaleHtml;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.LinearColorScale;

import java.awt.*;

public class DepthDecoratorCreator implements IDecoratorCreator {

    private final static IColorScaleHtml depthScale = new LinearColorScale(10.0, 100.0, new Color(255, 255, 255), new Color(0, 255, 0));

    @Override
    public String getDecoratorId() {
        return "DEPTH";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new ColorDecorator(columnField, depthScale, "st", true);
    }
}
