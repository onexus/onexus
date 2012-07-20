package org.onexus.ui.website.widgets.tableviewer.decorators;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.ColorDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.IColorScaleHtml;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.LinearColorScale;

import java.awt.Color;

public class QualityDecoratorCreator implements IDecoratorCreator {

    private final static IColorScaleHtml qualityScale = new LinearColorScale(
            0.0, 2500.0, new Color(255, 255, 255), new Color(0, 255, 0));

    @Override
    public String getDecoratorId() {
        return "QUALITY";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new ColorDecorator(columnField, qualityScale, "st", true);
    }
}
