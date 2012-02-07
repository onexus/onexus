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

import java.awt.*;
import java.io.Serializable;

public class CompositeColorScale extends AbstractColorScale {

    public static class ScaleRange implements Serializable {
        public double minPoint;
        public double maxPoint;
        public IColorScale scale;

        public ScaleRange(double minPoint, double maxPoint, IColorScale scale) {
            this.minPoint = minPoint;
            this.maxPoint = maxPoint;
            this.scale = scale;
        }

        public double getMinPoint() {
            return minPoint;
        }

        public void setMinPoint(double minPoint) {
            this.minPoint = minPoint;
        }

        public double getMaxPoint() {
            return maxPoint;
        }

        public void setMaxPoint(double maxPoint) {
            this.maxPoint = maxPoint;
        }

        public IColorScale getScale() {
            return scale;
        }

        public void setScale(IColorScale scale) {
            this.scale = scale;
        }
    }

    protected Color undefinedColor;

    protected ScaleRange[] scaleRanges;

    public CompositeColorScale(double minPoint, double maxPoint,
                               Color minColor, Color maxColor, Color undefinedColor,
                               ScaleRange[] scales) {

        super(minPoint, maxPoint, minColor, maxColor);

        this.undefinedColor = undefinedColor;
        this.scaleRanges = scales;
    }

    public CompositeColorScale(double minPoint, double maxPoint,
                               Color minColor, Color maxColor, Color undefinedColor) {
        this(minPoint, maxPoint, minColor, maxColor, undefinedColor,
                new ScaleRange[0]);
    }

    public CompositeColorScale(double minPoint, double maxPoint,
                               Color minColor, Color maxColor) {
        this(minPoint, maxPoint, minColor, maxColor,
                ColorConstants.undefinedColor, new ScaleRange[0]);
    }

    public CompositeColorScale(double minPoint, double maxPoint) {
        this(minPoint, maxPoint, ColorConstants.negInfinityColor,
                ColorConstants.posInfinityColor, ColorConstants.undefinedColor,
                new ScaleRange[0]);
    }

    public Color getUndefinedColor() {
        return undefinedColor;
    }

    public void setUndefinedColor(Color undefinedColor) {
        this.undefinedColor = undefinedColor;
    }

    public ScaleRange[] getScaleRanges() {
        return scaleRanges;
    }

    public void setScaleRanges(ScaleRange[] scales) {
        this.scaleRanges = scales;
    }

    @Override
    public Color valueColor(double value) {
        if (Double.isNaN(value))
            return notANumberColor;
        else if (value == Double.POSITIVE_INFINITY)
            return posInfinityColor;
        else if (value == Double.NEGATIVE_INFINITY)
            return negInfinityColor;
        else if (value > maxPoint)
            return maxColor;
        else if (value < minPoint)
            return minColor;

        for (ScaleRange range : scaleRanges)
            if (range.minPoint <= value && value <= range.maxPoint)
                return range.scale.valueColor(value);

        return undefinedColor;
    }

}
