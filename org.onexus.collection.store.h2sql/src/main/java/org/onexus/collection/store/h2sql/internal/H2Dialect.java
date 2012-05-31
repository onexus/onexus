package org.onexus.collection.store.h2sql.internal;

import org.onexus.collection.store.sql.SqlDialect;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class H2Dialect extends SqlDialect {

    @Override
    protected Map<Class<?>, String> registerColumnTypes() {
        Map<Class<?>, String> columnTypes = new HashMap<Class<?>, String>();

        columnTypes.put(String.class, "VARCHAR_IGNORECASE(128)");
        columnTypes.put(CharSequence.class, "TEXT");
        columnTypes.put(Boolean.class, "TINYINT(1)");
        columnTypes.put(Date.class, "TIMESTAMP");
        columnTypes.put(Integer.class, "INT(11)");
        columnTypes.put(Long.class, "BIGINT");
        columnTypes.put(Double.class, "DOUBLE");

        return columnTypes;
    }
}
