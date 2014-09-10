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

/**
 * Some utils to discover links between two collections, and parse link strings.
 */
public final class LinkUtils {

    private final static String FIELDS_SEPARATOR = "==";

    private LinkUtils() {
    }

    /**
     * Find all possible links between two collections.
     *
     * @param parentORI Base ORI to use on relative links.
     * @param a         First collection.
     * @param b         Second collection.
     * @return A list of links that link collection 'a' and 'b'.
     */
    public static List<FieldLink> getLinkFields(ORI parentORI, Collection a, Collection b) {
        return getLinkFields(parentORI, a, b, Collections.EMPTY_LIST);
    }

    /**
     * Find all possible links between two collections, with some fixed collections. A fixed collection is a collection
     * that is fixed to a single entity (that means that the where section of the query is using a EQUAL ID filter to
     * select a single IEntity for that collection).
     *
     * @param parentORI        Base ORI to use on relative links.
     * @param a                First collection.
     * @param b                Second collection.
     * @param fixedCollections A list of fixed collections.
     * @return A list of links that link collection 'a' and 'b' given that list of fixed collections.
     */
    public static List<FieldLink> getLinkFields(ORI parentORI, Collection a, Collection b, List<ORI> fixedCollections) {

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
            ORI linkCollection = link.getCollection().toAbsolute(a.getORI());
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

        // Case 1b: B has a direct link to A
        for (Link link : linksB) {
            if (link.getCollection().toAbsolute(b.getORI()).equals(a.getORI())) {
                for (String field : link.getFields()) {
                    String fromField = getFromFieldName(field);
                    String toField = getToFieldName(field);
                    fieldToLink.put(toField, new FieldLink(b.getORI(), fromField, a.getORI(), toField));
                }
            }
        }

        // Check that all A primary keys are linked
        if (!fieldToLink.isEmpty() && areAllPrimaryKeyLinked(a.getFields(), fieldToLink)) {
            fieldLinks.addAll(fieldToLink.values());
            return fieldLinks;
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
                        ORI linkBCollection = linkB.getCollection().toAbsolute(parentORI);
                        ORI linkACollection = linkA.getCollection().toAbsolute(parentORI);

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

    /**
     * Get the 'to' (right) field id given a field link expression.
     *
     * @param fieldLink A field link expression (ie: 'from_field_id == to_field_id')
     * @return The 'to' (right) field id. (ie: returns 'to_field_id')
     */
    public static String getToFieldName(String fieldLink) {
        String values[] = fieldLink.split(FIELDS_SEPARATOR);
        return values.length == 2 ? values[1].trim() : values[0].trim();
    }

    /**
     * Get the 'to' (right) field id given a field link expression.
     *
     * @param fieldLink A field link expression (ie: 'from_field_id == to_field_id')
     * @return The 'to' (right) field id. (ie: returns 'to_field_id')
     */
    public static String getFromFieldName(String fieldLink) {
        String values[] = fieldLink.split(FIELDS_SEPARATOR);
        return values[0].trim();
    }
}
