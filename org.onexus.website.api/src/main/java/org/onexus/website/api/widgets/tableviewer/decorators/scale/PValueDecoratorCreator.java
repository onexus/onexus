/**
 *  Copyright 2012 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.website.api.widgets.tableviewer.decorators.scale;

import org.apache.commons.lang3.StringUtils;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ParameterKey;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.ColorConstants;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.IColorScaleHtml;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.PValueColorScale;
import org.onexus.website.api.widgets.tableviewer.formaters.PValueFormater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PValueDecoratorCreator implements IDecoratorCreator {

    private static final Logger log = LoggerFactory.getLogger(PValueDecoratorCreator.class);
    private final static IColorScaleHtml pvalueScale = new PValueColorScale();

    @Override
    public String getDecoratorId() {
        return "PVALUE";
    }

    @Override
    public ParameterKey[] getParameterKeys() {
        return PValueDecoratorParameters.values();
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, Map<ParameterKey, String> parameters) {

        Boolean showValue = true;

        String parameterValue = parameters.get(PValueDecoratorParameters.SHOW_VALUE);
        if (!StringUtils.isEmpty(parameterValue)) {
            showValue = Boolean.valueOf(parameterValue);
        }

        IColorScaleHtml scale = pvalueScale;

        if (parameters.containsKey(PValueDecoratorParameters.SIGNIFICANCE)) {
            double significance = 0.05;

            try {
                significance = Double.valueOf(parameters.get(PValueDecoratorParameters.SIGNIFICANCE)).doubleValue();
            } catch (Exception e) {
                log.error("Impossible to convert 'significance' PVALUE decorator parameter to a double");
            }

            scale = new PValueColorScale(significance, ColorConstants.pvalueMinColor,
                    ColorConstants.pvalueMaxColor,
                    ColorConstants.nonSignificantColor);
        }


        return new ColorDecorator(columnField, columnField, scale, null, showValue, PValueFormater.INSTANCE, parameters.get(PValueDecoratorParameters.URL), parameters.get(PValueDecoratorParameters.URL_TITLE));
    }
}
