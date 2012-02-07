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
package org.onexus.core.query;

import java.util.Arrays;
import java.util.List;

public class In extends FieldFilter {

    private List<Object> values;

    public In() {
	super();
    }

    public In(String collectionId, String fieldName, Object... value) {
	super(collectionId, fieldName);

	values = Arrays.asList(value);
    }
    
    @Override
    public String toString() {
	return getFieldName() + " in (" + listValuesToString(",") + ")";
    }

    /**
     * Returns the values separed by specified delimitor. By default returns
     * empty string ("")
     * 
     * @param delimiter
     * @return
     */
    public String listValuesToString(String delimiter) {

	if (this.values == null || this.values.isEmpty()) {
	    return "";
	}

	StringBuilder sb = new StringBuilder();
	String str = null;
	for (Object s : this.values) {
	    str = encodeValue(s);
	    if (str != null) {
		sb.append(str);
		sb.append(delimiter);
	    }
	}
	return (sb.length() > 0) ? sb.substring(0, sb.length() - 1) : "";
    }

    /**
     * Only valid for SQL
     * 
     * @param value
     * @return
     */
    private static String encodeValue(Object value) {
	if (value == null) {
	    return "NULL";
	}

	if (value instanceof String) {
	    return "\"" + value + "\"";
	}

	return String.valueOf(value);
    }
}
