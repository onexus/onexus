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

public class BinaryColorScale extends AbstractColorScale {

    private double cutoff = 1.0;
    private CutoffCmp cutoffCmp = CutoffCmp.EQ;

    public BinaryColorScale(double minPoint, double maxPoint, double cutoff,
                            Color minColor, Color maxColor) {
        super(minPoint, maxPoint);
        this.cutoff = cutoff;
        this.minColor = minColor;
        this.maxColor = maxColor;
    }

    public BinaryColorScale() {
        super(0.0, 1.0);
    }

    @Override
    public Color valueColor(Object obj) {

        double value;
        if (obj instanceof Double) {
            value = (Double) obj;
        } else {
            value = Double.valueOf(String.valueOf(obj));
        }

        if (Double.isNaN(value))
            return notANumberColor;
        else if (value > maxPoint || value == Double.POSITIVE_INFINITY)
            return posInfinityColor;
        else if (value < minPoint || value == Double.NEGATIVE_INFINITY)
            return negInfinityColor;

        boolean isSig = cutoffCmp.compare(value, cutoff);
        return isSig ? maxColor : minColor;
    }

    public double getCutoff() {
        return cutoff;
    }

    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    public CutoffCmp getCutoffCmp() {
        return cutoffCmp;
    }

    public void setCutoffCmp(CutoffCmp cutoffCmp) {
        this.cutoffCmp = cutoffCmp;
    }

}
