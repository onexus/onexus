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
package org.onexus.ui.website.widgets.tableviewer.decorators.scales;

import java.awt.*;

public class LogColorScale extends AbstractColorScale {

    public static final double defaultLogFactor = 0.25;

    protected Color nonSignificantColor;

    private double logFactor;

    public LogColorScale(double minPoint, double maxPoint, double logFactor) {

        super(minPoint, maxPoint);
        this.logFactor = logFactor;
    }

    public LogColorScale(double minPoint, double maxPoint) {

        this(minPoint, maxPoint, defaultLogFactor);
    }

    public LogColorScale() {
        this(0.0, 1.0, defaultLogFactor);
    }

    public LogColorScale(double minPoint, double maxPoint, Color minColor,
                         Color maxColor) {

        this(minPoint, maxPoint);
        setMinColor(minColor);
        setMaxColor(maxColor);
    }

    public double getLogFactor() {
        return logFactor;
    }

    public void setLogFactor(double logFactor) {
        this.logFactor = logFactor;
    }

    public Color valueColor(double value) {
        if (Double.isNaN(value))
            return notANumberColor;
        else if (value > maxPoint || value == Double.POSITIVE_INFINITY)
            return posInfinityColor;
        else if (value < minPoint || value == Double.NEGATIVE_INFINITY)
            return negInfinityColor;

        double range = maxPoint - minPoint;

        double f = value / range;

        f = f > 0.0 ? 1.0 + logFactor * Math.log10(f) : f < 0.0 ? 1.0
                + logFactor * Math.log10(-f) : 0.0;

        return ColorUtils.mix(maxColor, minColor, f);
    }
}
