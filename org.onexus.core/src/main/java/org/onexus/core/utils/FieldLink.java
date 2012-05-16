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
package org.onexus.core.utils;

public class FieldLink {

    private String fromCollection;
    private String fromFieldName;
    private String toCollection;
    private String toFieldName;


    public FieldLink(String fromCollection, String fromFieldName,
                     String toCollection, String toFieldName) {
        super();
        this.fromCollection = fromCollection;
        this.fromFieldName = fromFieldName;
        this.toCollection = toCollection;
        this.toFieldName = toFieldName;
    }


    public String getFromCollection() {
        return fromCollection;
    }


    public void setFromCollection(String fromCollection) {
        this.fromCollection = fromCollection;
    }


    public String getFromFieldName() {
        return fromFieldName;
    }


    public void setFromFieldName(String fromFieldName) {
        this.fromFieldName = fromFieldName;
    }


    public String getToCollection() {
        return toCollection;
    }


    public void setToCollection(String toCollection) {
        this.toCollection = toCollection;
    }


    public String getToFieldName() {
        return toFieldName;
    }


    public void setToFieldName(String toFieldName) {
        this.toFieldName = toFieldName;
    }


}
