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
package org.onexus.website.api.widgets.filters;

import org.apache.wicket.model.PropertyModel;

public class TextFormaterPropertyModel extends PropertyModel<String> {

    private int maxLength;
    private boolean addDots;


    public TextFormaterPropertyModel(Object modelObject, String expression,
                                     final int maxLength, final boolean addDots) {
        super(modelObject, expression);

        this.maxLength = maxLength;
        this.addDots = addDots;

    }

    @Override
    public String getObject() {
        Object value = super.getObject();
        return (value == null ? null : format(value, maxLength, addDots));
    }

    public static String format(final Object value, int maxLength, boolean addDots) {

        String formatedValue = (value == null ? "" : value.toString());

        if (formatedValue.length() > maxLength) {
            formatedValue = formatedValue.substring(0, maxLength)
                    + (addDots ? "..." : "");
        }

        return formatedValue;

    }

}
