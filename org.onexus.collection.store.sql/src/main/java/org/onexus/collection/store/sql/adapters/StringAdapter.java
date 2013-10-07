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
package org.onexus.collection.store.sql.adapters;

import org.apache.commons.lang3.StringUtils;
import org.onexus.collection.store.sql.SqlDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class StringAdapter extends SqlAdapter {

    private static final Logger log = LoggerFactory.getLogger(StringAdapter.class);
    private SqlDialect sqlUtils;

    public StringAdapter(SqlDialect sqlUtils) {
        super(String.class);
        this.sqlUtils = sqlUtils;
    }

    @Override
    public void append(StringBuilder container, Object object) throws Exception {
        String value = (String) object;
        if (value.length() > 128) {
            value = StringUtils.abbreviate(value, 128);
            log.info("Value '" + object + "' abbreviated.");
        }
        container.append(sqlUtils.quoteString(value));
    }

    @Override
    public Object extract(ResultSet container, Object... parameters)
            throws Exception {
        return container.getString((String) parameters[0]);
    }

}
