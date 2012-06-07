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

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Link;

import java.util.ArrayList;
import java.util.List;

public class LinkUtils {

    public static List<FieldLink> getLinkFields(String releaseURI,
                                         Collection a, Collection b) {
        List<FieldLink> fieldLinks = getLeftLinkFields(releaseURI, a, b);

        if (fieldLinks.isEmpty()) {
            fieldLinks = getLeftLinkFields(releaseURI, b, a);
        }

        return fieldLinks;
    }

    private static List<FieldLink> getLeftLinkFields(String releaseURI,
                                                    Collection a, Collection b) {

        List<FieldLink> fieldLinks = new ArrayList<FieldLink>();

        List<Link> linksA = a.getLinks();
        List<Link> linksB = b.getLinks();

        // Case 1: A has a direct link to B

        for (Link link : linksA) {
            if (ResourceUtils.getAbsoluteURI(releaseURI, link.getCollection()).equals(b.getURI())) {
                for (String field : link.getFields()) {
                    fieldLinks.add(new FieldLink(a.getURI(), Link
                            .getFromFieldName(field), b.getURI(), Link
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
                                releaseURI, linkB.getCollection());
                        String linkACollection = ResourceUtils.getAbsoluteURI(
                                releaseURI, linkA.getCollection());

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
