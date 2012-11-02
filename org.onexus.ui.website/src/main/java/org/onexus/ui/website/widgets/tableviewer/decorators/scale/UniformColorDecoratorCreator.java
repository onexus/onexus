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
import org.onexus.ui.website.widgets.tableviewer.decorators.scale.scales.ColorUtils;
import org.onexus.ui.website.widgets.tableviewer.decorators.scale.scales.UniformColorScale;

import java.awt.*;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Map;

public class UniformColorDecoratorCreator implements IDecoratorCreator {

    @Override
    public String getDecoratorId() {
        return "UNIFORM";
    }

    @Override
    public ParameterKey[] getParameterKeys() {
        return UniformColorDecoratorParameters.values();
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, Map<ParameterKey, String> parameters) {

        Color color = ColorUtils.stringToColor(parameters.get(UniformColorDecoratorParameters.COLOR));

        return new ColorDecorator(columnField, new UniformColorScale(color), null, true);
    }
}
