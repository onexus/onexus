package org.onexus.ui.website.widgets.tableviewer.decorators.basic;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.ColorDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.IColorScaleHtml;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.LinearColorScale;

import java.awt.*;

public class RankDecoratorCreator implements IDecoratorCreator {

    private final static IColorScaleHtml rankScale = new LinearColorScale(0.0, 1.0, new Color(255, 255, 255), new Color(0, 255, 0));

    @Override
    public String getDecoratorId() {
        return "RANK";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new ColorDecorator(columnField, rankScale, null, true);
    }
}
