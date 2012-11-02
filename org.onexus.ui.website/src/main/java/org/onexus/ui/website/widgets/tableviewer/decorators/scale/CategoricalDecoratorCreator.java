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
package org.onexus.ui.website.widgets.tableviewer.decorators.scale;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ParameterKey;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecorator;
import org.onexus.ui.website.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.ui.website.widgets.tableviewer.decorators.scale.scales.CategoricalColorScale;
import org.onexus.ui.website.widgets.tableviewer.decorators.scale.scales.ColorConstants;
import org.onexus.ui.website.widgets.tableviewer.decorators.scale.scales.ColorUtils;
import org.onexus.ui.website.widgets.tableviewer.decorators.scale.scales.UniformColorScale;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CategoricalDecoratorCreator implements IDecoratorCreator {

    @Override
    public String getDecoratorId() {
        return "CATEGORICAL";
    }

    @Override
    public ParameterKey[] getParameterKeys() {
        return CategoricalDecoratorParameters.values();
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, Map<ParameterKey, String> parameters) {

        Color defaultColor = ColorUtils.stringToColor(parameters.get(CategoricalDecoratorParameters.DEFAULT));

        if (defaultColor == null) {
            defaultColor = new Color(255,255,255);
        }

        Map<String, Color> colorsMap = new HashMap<String, Color>();
        String mapValue = parameters.get(CategoricalDecoratorParameters.MAP);
        String items[] = mapValue.split("\\|");

        for (String item : items) {
            String pair[] = item.split("=");

            String key = pair[0].replace('[', ' ').replace(']', ' ').trim();
            Color color = ColorUtils.stringToColor(pair[1]);

            colorsMap.put(key, color);
        }

        return new ColorDecorator(columnField, new CategoricalColorScale(defaultColor, colorsMap), null, true);
    }
}
