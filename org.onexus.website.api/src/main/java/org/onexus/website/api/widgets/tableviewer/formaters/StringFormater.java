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

public class StringFormater implements ITextFormater {

    private int maxLength;
    private boolean addDots;

    public StringFormater(final int maxLength, final boolean addDots) {
        super();
        this.maxLength = maxLength;
        this.addDots = addDots;
    }

    @Override
    public String format(final Object value) {
        return format(value, maxLength, addDots);
    }

    public static String format(final Object value, int maxLength, boolean addDots) {

        String formatedValue = value == null ? "" : value.toString();

        if (formatedValue.length() > maxLength) {
            formatedValue = formatedValue.substring(0, maxLength)
                    + (addDots ? "..." : "");
        }

        return formatedValue;

    }

}
