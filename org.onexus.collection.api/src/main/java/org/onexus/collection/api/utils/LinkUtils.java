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
import org.onexus.resource.api.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkUtils {

    public static List<FieldLink> getLinkFields(String parentURI,
                                         Collection a, Collection b) {
        List<FieldLink> fieldLinks = getLeftLinkFields(parentURI, a, b);
        return fieldLinks;
    }

    private static List<FieldLink> getLeftLinkFields(String parentURI,
                                                    Collection a, Collection b) {

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
        for (Link link : linksA) {
            if (ResourceUtils.getAbsoluteURI(parentURI, link.getCollection()).equals(b.getURI())) {
                for (String field : link.getFields()) {
                    fieldLinks.add(new FieldLink(a.getURI(), Link
                            .getFromFieldName(field), b.getURI(), Link
                            .getToFieldName(field)));
                }
                return fieldLinks;
            }
        }

        // Case 1b: B has a direct link to A
        for (Link link : linksB) {
            if (ResourceUtils.getAbsoluteURI(parentURI, link.getCollection()).equals(a.getURI())) {
                for (String field : link.getFields()) {
                    fieldLinks.add(new FieldLink(b.getURI(), Link
                            .getFromFieldName(field), a.getURI(), Link
                            .getToFieldName(field)));
                }
                return fieldLinks;
            }
        }


        // Case 2: All the primary fields of A has a link to collections linked
        // by B
        for (Field field : a.getFields()) {
            if (field.isPrimaryKey() != null && field.isPrimaryKey()) {

                // The links that link to this field
                List<Link> keyLinks = new ArrayList<Link>();
                for (Link link : linksA) {
                    for (String fieldName : link.getFields()) {
                        String fromField = Link.getFromFieldName(fieldName);
                        if (fromField.equals(field.getId())) {
                            keyLinks.add(link);
                        }
                    }
                }

                // Look if there is any match with B links
                for (Link linkB : linksB) {
                    for (Link linkA : keyLinks) {
                        String linkBCollection = ResourceUtils.getAbsoluteURI(
                                parentURI, linkB.getCollection());
                        String linkACollection = ResourceUtils.getAbsoluteURI(
                                parentURI, linkA.getCollection());

                        if (linkBCollection.equals(linkACollection)) {

                            // Try to match the field links
                            for (String fieldLinkA : linkA.getFields()) {
                                String toFieldA = Link
                                        .getToFieldName(fieldLinkA);

                                for (String fieldLinkB : linkB.getFields()) {
                                    String toFieldB = Link
                                            .getToFieldName(fieldLinkB);
                                    if (toFieldA.equals(toFieldB)) {
                                        String fromFieldA = Link
                                                .getFromFieldName(fieldLinkA);
                                        String fromFieldB = Link
                                                .getFromFieldName(fieldLinkB);
                                        fieldLinks.add(new FieldLink(
                                                a.getURI(), fromFieldA, b
                                                .getURI(), fromFieldB));
                                    }
                                }
                            }

                        }
                    }
                }

            }
        }

        return fieldLinks;

    }

}
