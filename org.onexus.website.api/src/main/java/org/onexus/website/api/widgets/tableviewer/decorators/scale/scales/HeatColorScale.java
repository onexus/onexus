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
package org.onexus.website.api.widgets.tableviewer.decorators.scale.scales;

import java.awt.*;

public class HeatColorScale extends CompositeColorScale {

    public final static Color DEFAULT_MIN_COLOR = new Color(0, 0, 255);
    public final static Color DEFAULT_MID_COLOR = new Color(255, 255, 0);
    public final static Color DEFAULT_MAX_COLOR = new Color(255, 0, 0);

    public final static double DEFAULT_MIN_VALUE = -1;
    public final static double DEFAULT_MID_VALUE = 0;
    public final static double DEFAULT_MAX_VALUE = 1;


    public HeatColorScale(double minValue, Color minColor, double midValue, Color midColor, double maxValue, Color maxColor) {
        super(minValue, maxValue, minColor, maxColor);


        ScaleRange[] scaleRanges = new ScaleRange[]{
                new ScaleRange(minValue, midValue, new LinearColorScale(minValue, midValue, minColor, midColor)),
                new ScaleRange(midValue, maxValue, new LinearColorScale(midValue, maxValue, midColor, maxColor))
        };

        setScaleRanges(scaleRanges);
    }

    public HeatColorScale(double minValue, double midValue, double maxValue) {
        this(minValue, DEFAULT_MIN_COLOR, midValue, DEFAULT_MID_COLOR, maxValue, DEFAULT_MAX_COLOR);
    }

    public HeatColorScale() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MID_VALUE, DEFAULT_MAX_VALUE);
    }

}
