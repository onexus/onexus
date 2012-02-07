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
package org.onexus.core.resources;

import java.util.ArrayList;
import java.util.List;

public class Collection extends MetadataResource {

    private Task task;
    private List<Field> fields = new ArrayList<Field>();
    private List<Link> links = new ArrayList<Link>();

    public Collection() {
	super();
    }
    
    public Task getTask() {
	return task;
    }

    public void setTask(Task task) {
	this.task = task;
    }

    public List<Field> getFields() {
	return fields;
    }

    public List<Link> getLinks() {
	return links;
    }

   
    @Override
    public String toString() {
	return "Collection [getURI()=" + getURI() + ", task=" + task
		+ ", fields=" + fields + ", links=" + links + "]";
    }

    public Field getField(String fieldName) {
	if (fieldName != null) {
	    for (Field field : fields) {
		if (field.getName().equals(fieldName)) {
		    return field;
		}
	    }
	}

	return null;
    }
    
    public void setFields(List<Field> fields) {
	this.fields = fields;
    }

}
