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
package org.onexus.collection.store.h2sql.internal;

import org.onexus.collection.store.sql.SqlDialect;
import org.onexus.collection.api.types.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class H2Dialect extends SqlDialect {

    @Override
    protected Map<Class<?>, String> registerColumnTypes() {
        Map<Class<?>, String> columnTypes = new HashMap<Class<?>, String>();

        columnTypes.put(String.class, "VARCHAR_IGNORECASE(128)");
        columnTypes.put(Text.class, "TEXT");
        columnTypes.put(Boolean.class, "TINYINT(1)");
        columnTypes.put(Date.class, "TIMESTAMP");
        columnTypes.put(Integer.class, "INT(11)");
        columnTypes.put(Long.class, "BIGINT");
        columnTypes.put(Double.class, "DOUBLE");

        return columnTypes;
    }
}
