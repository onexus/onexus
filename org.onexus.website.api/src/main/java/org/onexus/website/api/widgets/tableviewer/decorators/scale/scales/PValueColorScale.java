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

public class PValueColorScale extends CompositeColorScale {

    private static final double EPSILON = 1e-16;

    private double significanceLevel;

    private UniformColorScale nonSigScale;

    private ScaleRange nonSigScaleRange;

    private LogColorScale scale;

    private ScaleRange scaleRange;

    public PValueColorScale(double significanceLevel, Color minColor,
                            Color maxColor, Color nonSignificantColor) {

        super(0.0, 1.0, minColor, maxColor);

        this.significanceLevel = significanceLevel;

        nonSigScale = new UniformColorScale(nonSignificantColor);

        nonSigScaleRange = new ScaleRange(significanceLevel + EPSILON, 1.0,
                nonSigScale);

        scale = new LogColorScale(0.0, significanceLevel + EPSILON, minColor,
                maxColor);

        scaleRange = new ScaleRange(0.0, significanceLevel + EPSILON, scale);

        ScaleRange[] scaleRanges = new ScaleRange[]{nonSigScaleRange,
                scaleRange};

        setScaleRanges(scaleRanges);
    }

    public PValueColorScale() {
        this(0.05, ColorConstants.PVALUE_MIN_COLOR,
                ColorConstants.PVALUE_MAX_COLOR,
                ColorConstants.NON_SIGNIFICANT_COLOR);
    }

    public double getSignificanceLevel() {
        return significanceLevel;
    }

    public void setSignificanceLevel(double significanceLevel) {
        this.significanceLevel = significanceLevel;
        nonSigScaleRange.setMinPoint(significanceLevel + EPSILON);
        scaleRange.setMaxPoint(significanceLevel + EPSILON);
        scale.setMaxPoint(significanceLevel + EPSILON);
    }

    @Override
    public void setMinColor(Color color) {
        super.setMinColor(color);
        scale.setMinColor(color);
    }

    @Override
    public void setMaxColor(Color color) {
        super.setMaxColor(color);
        scale.setMaxColor(color);
    }

    public Color getNonSignificantColor() {
        return nonSigScale.getColor();
    }

    public void setNonSignificantColor(Color color) {
        nonSigScale.setColor(color);
    }
}
