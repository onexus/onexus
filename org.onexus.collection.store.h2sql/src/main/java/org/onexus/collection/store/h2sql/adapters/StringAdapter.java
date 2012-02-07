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
package org.onexus.collection.store.h2sql.adapters;

import org.h2.util.StringUtils;

import java.sql.ResultSet;

public class StringAdapter extends SQLAdapter {

    public StringAdapter() {
        super(String.class);
    }

    @Override
    public void append(StringBuilder container, Object object) throws Exception {

        String quoteValue;
        if (object == null) {
            quoteValue = "NULL";
        } else {
            quoteValue = StringUtils.quoteStringSQL(String.valueOf(object));
        }

        ((StringBuilder) container).append(quoteValue);

    }

    @Override
    public Object extract(ResultSet container, Object... parameters)
            throws Exception {
        return container.getString((String) parameters[0]);
    }

}
