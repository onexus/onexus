package org.onexus.ui.website.widgets.tableviewer.decorators.basic;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.ui.website.widgets.tableviewer.decorators.basic.decorators.ColorDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.IColorScaleHtml;
import org.onexus.ui.website.widgets.tableviewer.decorators.scales.PValueColorScale;
import org.onexus.ui.website.widgets.tableviewer.formaters.PValueFormater;

public class PValue2DecoratorCreator implements IDecoratorCreator {

    private final static IColorScaleHtml pvalueScale = new PValueColorScale();

    @Override
    public String getDecoratorId() {
        return "PVALUE2";
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, String[] parameters) {
        return new ColorDecorator(columnField, columnField, pvalueScale, null, true, PValueFormater.INSTANCE);
    }
}
