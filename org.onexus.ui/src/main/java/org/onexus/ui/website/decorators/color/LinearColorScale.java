/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.decorators.color;

import java.awt.Color;

public class LinearColorScale extends AbstractColorScale {

    public LinearColorScale(double minPoint, double maxPoint, Color minColor,
	    Color maxColor) {
	super(minPoint, maxPoint);
	this.minColor = minColor;
	this.maxColor = maxColor;
    }

    public LinearColorScale(double minPoint, double maxPoint) {
	super(minPoint, maxPoint);
    }

    public LinearColorScale() {
	super(0.0, 1.0);
    }

    @Override
    public Color valueColor(double value) {
	if (Double.isNaN(value))
	    return notANumberColor;
	else if (value > maxPoint || value == Double.POSITIVE_INFINITY)
	    return posInfinityColor;
	else if (value < minPoint || value == Double.NEGATIVE_INFINITY)
	    return negInfinityColor;

	double range = maxPoint - minPoint;

	double f = value / range;

	return f <= 0 ? ColorUtils.mix(minColor, maxColor, -f) : ColorUtils
		.mix(maxColor, minColor, f);
    }

}
