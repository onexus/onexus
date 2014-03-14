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
package org.onexus.website.widgets.tableviewer.decorators.scale;

import org.apache.commons.lang3.StringUtils;
import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ParameterKey;
import org.onexus.resource.api.Parameters;
import org.onexus.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.website.widgets.tableviewer.decorators.scale.scales.HeatColorScale;
import org.onexus.website.widgets.tableviewer.formaters.DoubleFormater;

import java.awt.*;

public class HeatDecoratorCreator implements IDecoratorCreator {

    @Override
    public String getDecoratorId() {
        return "HEAT";
    }

    @Override
    public ParameterKey[] getParameterKeys() {
        return HeatDecoratorParameters.values();
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, Parameters parameters) {

        Boolean showValue = true;

        double minValue = HeatColorScale.DEFAULT_MIN_VALUE;
        double midValue = HeatColorScale.DEFAULT_MID_VALUE;
        double maxValue = HeatColorScale.DEFAULT_MAX_VALUE;

        // Show value parameter
        String parameterValue = parameters.get(HeatDecoratorParameters.SHOW_VALUE);
        if (!StringUtils.isEmpty(parameterValue)) {
            showValue = Boolean.valueOf(parameterValue);
        }

        // Min value parameter
        parameterValue = parameters.get(HeatDecoratorParameters.MIN_VALUE);
        if (!StringUtils.isEmpty(parameterValue)) {
            minValue = Double.valueOf(parameterValue);
        }

        // Mid value parameter
        parameterValue = parameters.get(HeatDecoratorParameters.MID_VALUE);
        if (!StringUtils.isEmpty(parameterValue)) {
            midValue = Double.valueOf(parameterValue);
        }

        // Max value parameter
        parameterValue = parameters.get(HeatDecoratorParameters.MAX_VALUE);
        if (!StringUtils.isEmpty(parameterValue)) {
            maxValue = Double.valueOf(parameterValue);
        }

        Color minColor = HeatColorScale.DEFAULT_MIN_COLOR;
        Color midColor = HeatColorScale.DEFAULT_MID_COLOR;
        Color maxColor = HeatColorScale.DEFAULT_MAX_COLOR;

        // Min color parameter
        parameterValue = parameters.get(HeatDecoratorParameters.MIN_COLOR);
        if (!StringUtils.isEmpty(parameterValue)) {
            minColor = parseColor(parameterValue);
        }

        // Mid color parameter
        parameterValue = parameters.get(HeatDecoratorParameters.MID_COLOR);
        if (!StringUtils.isEmpty(parameterValue)) {
            midColor = parseColor(parameterValue);
        }

        // Max color parameter
        parameterValue = parameters.get(HeatDecoratorParameters.MAX_COLOR);
        if (!StringUtils.isEmpty(parameterValue)) {
            maxColor = parseColor(parameterValue);
        }

        return new ColorDecorator(columnField, columnField, new HeatColorScale(minValue, minColor, midValue, midColor, maxValue, maxColor), null, showValue, DoubleFormater.INSTANCE, parameters.get(HeatDecoratorParameters.URL), parameters.get(HeatDecoratorParameters.URL_TITLE));
    }

    private static Color parseColor(String colorStr) {

        colorStr = colorStr.replace("[", "");
        colorStr = colorStr.replace("]", "");
        String[] values = colorStr.trim().split("-");

        int red = Integer.valueOf(values[0]);
        int green = Integer.valueOf(values[1]);
        int blue = Integer.valueOf(values[2]);

        return new Color(red, green, blue);
    }
}

