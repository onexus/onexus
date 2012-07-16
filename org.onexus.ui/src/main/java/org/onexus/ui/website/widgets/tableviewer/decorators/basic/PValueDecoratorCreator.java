package org.onexus.ui.website.widgets.tableviewer.decorators.basic;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.ColorDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.IColorScaleHtml;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.PValueColorScale;
import org.onexus.ui.website.widgets.tableviewer.formaters.PValueFormater;

public class PValueDecoratorCreator implements IDecoratorCreator{

    private final static IColorScaleHtml pvalueScale = new PValueColorScale();

    @Override
    public String getDecoratorId() {
        return "PVALUE";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new ColorDecorator(columnField, pvalueScale, null, PValueFormater.INSTANCE);
    }
}
