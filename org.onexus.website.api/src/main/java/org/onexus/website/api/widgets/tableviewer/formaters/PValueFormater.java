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
package org.onexus.website.api.widgets.tableviewer.formaters;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PValueFormater implements ITextFormater {

    public static final ITextFormater INSTANCE = new PValueFormater();

    public PValueFormater() {
    }

    @Override
    public String format(Object obj) {

        if (obj == null) {
            return "";
        }

        if (obj instanceof Double) {
            double value = (Double) obj;
            if (value < 1e-16) {
                return "< 1E-16";
            }

            if (value < 0.01) {
                NumberFormat f = new DecimalFormat("0.###E0");
                return f.format(value);
            } else {
                NumberFormat f = NumberFormat.getNumberInstance();
                f.setMaximumFractionDigits(3);
                return f.format(value);
            }
        }

        return obj.toString();
    }

}
