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

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.resource.api.ParameterKey;
import org.onexus.resource.api.Parameters;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecorator;
import org.onexus.website.api.widgets.tableviewer.decorators.IDecoratorCreator;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.IColorScaleHtml;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.LinearColorScale;

import java.awt.*;

public class QualityDecoratorCreator implements IDecoratorCreator {

    private static final IColorScaleHtml QUALITY_SCALE = new LinearColorScale(
            0.0, 2500.0, new Color(255, 255, 255), new Color(0, 255, 0));

    @Override
    public String getDecoratorId() {
        return "QUALITY";
    }

    @Override
    public ParameterKey[] getParameterKeys() {
        return new ParameterKey[0];
    }

    @Override
    public IDecorator createDecorator(Collection collection, Field columnField, Parameters parameters) {
        return new ColorDecorator(columnField, QUALITY_SCALE, "st", true);
    }
}
