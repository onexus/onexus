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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Link implements Serializable {

    private String collectionURI;
    private List<String> fieldNames = new ArrayList<String>();

    public Link() {
	super();
    }
    
    public Link(String collectionURI, List<String> fieldNames) {
	super();
	this.collectionURI = collectionURI;
	this.fieldNames = fieldNames;
    }

    public String getCollectionURI() {
	return collectionURI;
    }

    public void setCollectionURI(String collectionURI) {
	this.collectionURI = collectionURI;
    }

    public List<String> getFieldNames() {
	return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
	this.fieldNames = fieldNames;
    }

    @Override
    public String toString() {
	return "Link [collectionURI=" + collectionURI + ", fieldNames="
		+ fieldNames + "]";
    }

    public static String getToFieldName(String fieldLink) {
	String values[] = fieldLink.split("\\/\\/");
	return (values.length==2?values[1]:values[0]);
    }
    
    public static String getFromFieldName(String fieldLink) {
	String values[] = fieldLink.split("\\/\\/");
	return values[0];
    }

}
