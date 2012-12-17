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

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.Link;
import org.onexus.resource.api.ORI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkUtils {

    public final static String FIELDS_SEPARATOR = "==";

    public static List<FieldLink> getLinkFields(ORI parentURI, Collection a, Collection b) {
        return getLinkFields(parentURI, a, b, Collections.EMPTY_LIST);
    }

    public static List<FieldLink> getLinkFields(ORI parentURI, Collection a, Collection b, List<ORI> fixedCollections) {

        List<FieldLink> fieldLinks = new ArrayList<FieldLink>();

        List<Link> linksA = a.getLinks();
        List<Link> linksB = b.getLinks();

        if (linksA == null) {
            linksA = Collections.EMPTY_LIST;
        }

        if (linksB == null) {
            linksB = Collections.EMPTY_LIST;
        }

        // Case 1: A has a direct link to B
        Map<String, FieldLink> fieldToLink = new HashMap<String, FieldLink>();
        for (Link link : linksA) {
            ORI linkCollection = link.getCollection().toAbsolute(parentURI);
            if (linkCollection.equals(b.getORI())) {
                for (String field : link.getFields()) {
                    String fromField = getFromFieldName(field);
                    String toField = getToFieldName(field);
                    fieldToLink.put(fromField, new FieldLink(a.getORI(), fromField, b.getORI(), toField));
                }
            } else {
                if (fixedCollections.contains(linkCollection)) {
                    for (String field : link.getFields()) {
                        String fromField = getFromFieldName(field);
                        String toField = getToFieldName(field);
                        fieldToLink.put(fromField, new FieldLink(a.getORI(), fromField, linkCollection, toField));
                    }
                }
            }
        }

        // Check that all A primary keys are linked
        if (!fieldToLink.isEmpty()) {
            if (areAllPrimaryKeyLinked(a.getFields(), fieldToLink)) {
                fieldLinks.addAll(fieldToLink.values());
                return fieldLinks;
            }
        }

        // Case 1b: B has a direct link to A
        for (Link link : linksB) {
            if (link.getCollection().toAbsolute(parentURI).equals(a.getORI())) {
                for (String field : link.getFields()) {
                    String fromField = getFromFieldName(field);
                    String toField = getToFieldName(field);
                    fieldToLink.put(toField, new FieldLink(b.getORI(), fromField, a.getORI(), toField));
                }
            }
        }

        // Check that all A primary keys are linked
        if (!fieldToLink.isEmpty()) {
            if (areAllPrimaryKeyLinked(a.getFields(), fieldToLink)) {
                fieldLinks.addAll(fieldToLink.values());
                return fieldLinks;
            }
        }

        // Case 2: All the primary fields of A has a link to collections linked
        // by B
        int totalPrimaryKeyFields = 0;
        List<FieldLink> primaryKeyLinks = new ArrayList<FieldLink>();
        for (Field field : a.getFields()) {
            if (field.isPrimaryKey() != null && field.isPrimaryKey()) {
                totalPrimaryKeyFields++;

                // The links that link to this field
                List<Link> keyLinks = new ArrayList<Link>();
                for (Link link : linksA) {
                    for (String fieldName : link.getFields()) {
                        String fromField = getFromFieldName(fieldName);
                        if (fromField.equals(field.getId())) {
                            keyLinks.add(link);
                        }
                    }
                }

                // Look if there is any match with B links
                for (Link linkB : linksB) {
                    for (Link linkA : keyLinks) {
                        ORI linkBCollection = linkB.getCollection().toAbsolute(parentURI);
                        ORI linkACollection = linkA.getCollection().toAbsolute(parentURI);

                        if (linkBCollection.equals(linkACollection)) {

                            // Try to match the field links
                            for (String fieldLinkA : linkA.getFields()) {
                                String toFieldA =
                                        getToFieldName(fieldLinkA);

                                for (String fieldLinkB : linkB.getFields()) {
                                    String toFieldB =
                                            getToFieldName(fieldLinkB);
                                    if (toFieldA.equals(toFieldB)) {
                                        String fromFieldA =
                                                getFromFieldName(fieldLinkA);
                                        String fromFieldB =
                                                getFromFieldName(fieldLinkB);
                                        primaryKeyLinks.add(new FieldLink(
                                                a.getORI(), fromFieldA, b
                                                .getORI(), fromFieldB));
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }

        if (totalPrimaryKeyFields == primaryKeyLinks.size()) {
            fieldLinks.addAll(primaryKeyLinks);
        }

        return fieldLinks;

    }

    private static boolean areAllPrimaryKeyLinked(List<Field> fields, Map<String, FieldLink> fieldToLink) {
        boolean allKeyLinked = false;
        if (fields != null) {
            for (Field field : fields) {
                if (field.isPrimaryKey() != null && field.isPrimaryKey()) {
                    allKeyLinked = true;
                    if (!fieldToLink.containsKey(field.getId())) {
                        allKeyLinked = false;
                        break;
                    }
                }
            }
        }
        return allKeyLinked;
    }

    public static String getToFieldName(String fieldLink) {
        String values[] = fieldLink.split(FIELDS_SEPARATOR);
        return (values.length == 2 ? values[1].trim() : values[0].trim());
    }

    public static String getFromFieldName(String fieldLink) {
        String values[] = fieldLink.split(FIELDS_SEPARATOR);
        return values[0].trim();
    }
}
