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
package org.onexus.task.executor.loader.biomart;

import java.lang.reflect.Constructor;
import java.util.List;

import org.onexus.core.IEntity;
import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;

public class TSVEntity implements IEntity {
    
    private String line;
    private Collection collection;
    
    protected String NULL_CHAR = "-";
    protected String SEPARATOR = "\t";

    public TSVEntity(Collection collection, String line) {
	super();
	this.collection = collection;
	this.line = line;
    }

    @Override
    public String getId() {
	return null;
    }

    @Override
    public Collection getCollection() {
	return collection;
    }

    @Override
    public Object get(String fieldName) {

	List<Field> fields = collection.getFields();
	
	int position=0;
	Field field = null;
	for(; position < fields.size(); position++) {
	    field = fields.get(position);
	    if (field.getName().equals(fieldName)) {
		break;
	    }
	}
	
	String value = parseField(line, position);
	
	if (value == null) {
	    return null;
	} else {

	    // Remove "
	    value = value.replace('"', ' ');

	    // Trim blank spaces
	    value = value.trim();

	}

	try {

	    Class<?> fieldClass = field.getDataType();

	    // TODO use adapter factory as in SQL manager
	    if (fieldClass.equals(Boolean.class)) {
		return Boolean.valueOf(value.trim().equalsIgnoreCase("1"));
	    }

	    // For number types return null if the value is empty
	    if (Number.class.isAssignableFrom(fieldClass)) {
		if (value.equals("")) {
		    return null;
		}
	    }

	    Constructor<?> constructor = fieldClass
		    .getConstructor(String.class);

	    return constructor.newInstance(value);
	} catch (Exception e) {
	    throw new RuntimeException("The value '" + value
		    + "' for the field '" + fieldName
		    + "' is malformed on line '" + line + "'", e);
	}

    }

    @Override
    public void put(String fieldURI, Object value) {
	throw new UnsupportedOperationException("Read-only TSVEntity");
    }
    
    public String getLine() {
	return line;
    }

    public void setLine(String line) {
	this.line = line;
    }
    
    protected String parseField(String str, int num) {

	int start = -1;
	for (int i = 0; i < num; i++) {
	    start = str.indexOf(SEPARATOR, start + 1);
	    if (start == -1)
		return null;
	}

	int end = str.indexOf(SEPARATOR, start + 1);
	if (end == -1)
	    end = str.length();

	String result = str.substring(start + 1, end);

	if (result != null && result.equals(NULL_CHAR)) {
	    return null;
	}

	return result.replace('"', ' ').trim();

    }

}
