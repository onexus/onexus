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
package org.onexus.website.widgets.tableviewer.formaters;

import java.text.NumberFormat;

public class DoubleFormater implements ITextFormater {

    public static final ITextFormater INSTANCE = new DoubleFormater(3);

    private int digits;

    public DoubleFormater(int digits) {
        this.digits = digits;
    }

    @Override
    public String format(Object value) {
        return format(value, digits);
    }

    public static String format(Object value, int digits) {

        if (value == null) {
            return "";
        }

        if (value instanceof Double) {
            NumberFormat f = NumberFormat.getNumberInstance();

            double v = ((Double) value).doubleValue();
            double limit = Math.pow(10, -digits);
            if (v > 0 && v < limit) {
                return "< " + f.format(limit);
            }

            f.setMaximumFractionDigits(digits);
            return f.format(v);
        }

        return value.toString();
    }
}
