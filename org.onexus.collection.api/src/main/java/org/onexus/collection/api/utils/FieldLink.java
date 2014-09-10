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
package org.onexus.collection.api.utils;

import org.onexus.resource.api.ORI;

/**
 * This class represents a link between to field collections.
 */
public class FieldLink {

    private ORI fromCollection;
    private String fromFieldName;
    private ORI toCollection;
    private String toFieldName;


    /**
     * Create a collection link
     *
     * @param fromCollection The 'from' collection of the link.
     * @param fromFieldName  The 'from' field id.
     * @param toCollection   The 'to' collection of the link.
     * @param toFieldName    The 'to' field id.
     */
    public FieldLink(ORI fromCollection, String fromFieldName,
                     ORI toCollection, String toFieldName) {
        super();
        this.fromCollection = fromCollection;
        this.fromFieldName = fromFieldName;
        this.toCollection = toCollection;
        this.toFieldName = toFieldName;
    }

    /**
     * Gets the from collection.
     *
     * @return Collection ORI.
     */
    public ORI getFromCollection() {
        return fromCollection;
    }

    /**
     * Sets the from collection.
     *
     * @param fromCollection The 'from' collection of the link.
     */
    public void setFromCollection(ORI fromCollection) {
        this.fromCollection = fromCollection;
    }

    /**
     * Gets the from field id.
     *
     * @return Field id.
     */
    public String getFromFieldName() {
        return fromFieldName;
    }

    /**
     * Sets the from field id.
     *
     * @param fromFieldName The 'from' field id.
     */
    public void setFromFieldName(String fromFieldName) {
        this.fromFieldName = fromFieldName;
    }

    /**
     * Gets the to collection.
     *
     * @return Collection ORI.
     */
    public ORI getToCollection() {
        return toCollection;
    }

    /**
     * Sets the to collection.
     *
     * @param toCollection The 'to' collection of the link.
     */
    public void setToCollection(ORI toCollection) {
        this.toCollection = toCollection;
    }

    /**
     * Gets the to field id.
     *
     * @return Field id.
     */
    public String getToFieldName() {
        return toFieldName;
    }

    /**
     * Sets the to field id.
     *
     * @param toFieldName The 'to' field id.
     */
    public void setToFieldName(String toFieldName) {
        this.toFieldName = toFieldName;
    }

    @Override
    public String toString() {
        return "FieldLink{" +
                "fromCollection=" + fromCollection +
                ", fromFieldName='" + fromFieldName + '\'' +
                ", toCollection=" + toCollection +
                ", toFieldName='" + toFieldName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldLink fieldLink = (FieldLink) o;

        if (!fromCollection.equals(fieldLink.fromCollection)) {
            return false;
        }

        if (!fromFieldName.equals(fieldLink.fromFieldName)) {
            return false;
        }

        if (!toCollection.equals(fieldLink.toCollection)) {
            return false;
        }

        if (!toFieldName.equals(fieldLink.toFieldName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromCollection.hashCode();
        result = 31 * result + fromFieldName.hashCode();
        result = 31 * result + toCollection.hashCode();
        result = 31 * result + toFieldName.hashCode();
        return result;
    }
}
