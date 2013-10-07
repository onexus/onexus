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

public final class ColorConstants {

    public static final Color NOT_A_NUMBER_COLOR = Color.WHITE;
    public static final Color POS_INFINITY_COLOR = Color.GREEN;
    public static final Color NEG_INFINITY_COLOR = Color.CYAN;
    public static final Color UNDEFINED_COLOR = Color.BLACK;
    public static final Color EMPTY_COLOR = Color.WHITE;

    public static final Color NON_SIGNIFICANT_COLOR = new Color(187, 187, 187);

    public static final Color PVALUE_MIN_COLOR = new Color(255, 0, 0);
    public static final Color PVALUE_MAX_COLOR = new Color(255, 255, 0);

    private ColorConstants() {
    }
}
