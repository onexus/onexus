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

public class ColorUtils {

    public static Color mix(Color src, Color dst, double factor) {

        double fs = factor / 255.0;
        double fd = (1.0 - factor) / 255.0;

        double r = src.getRed() * fs + dst.getRed() * fd;
        double g = src.getGreen() * fs + dst.getGreen() * fd;
        double b = src.getBlue() * fs + dst.getBlue() * fd;

        int ir = Math.max(0, Math.min(255, (int) Math.round(r * 255)));
        int ig = Math.max(0, Math.min(255, (int) Math.round(g * 255)));
        int ib = Math.max(0, Math.min(255, (int) Math.round(b * 255)));

        return new Color(ir, ig, ib);
    }

    public static String colorToRGBHtml(Color color) {
        StringBuilder sb = new StringBuilder();

        sb.append("rgb(");
        sb.append(color.getRed()).append(',');
        sb.append(color.getGreen()).append(',');
        sb.append(color.getBlue()).append(')');

        return sb.toString();
    }

    public static String colorToHexHtml(Color color) {
        return "#"
                + Integer.toHexString((color.getRGB() & 0xffffff) | 0x1000000)
                .substring(1);
    }

    public static Color stringToColor(String color) {

        if (color == null) {
            return null;
        }

        int r = 0, g = 0, b = 0;
        try {
            String tmpcolor = color.trim();
            tmpcolor = tmpcolor.substring(1, tmpcolor.length() - 1);
            String rgb[] = tmpcolor.split(",");

            r = Integer.valueOf(rgb[0]).intValue();
            g = Integer.valueOf(rgb[1]).intValue();
            b = Integer.valueOf(rgb[2]).intValue();

            return new Color(r, g, b);
        } catch (Exception e) {
            throw new UnsupportedOperationException("The color '" + color + "' is malformed. Syntax: [r,g,b] Example: \"[128,12,34]\"", e);
        }
    }
}


