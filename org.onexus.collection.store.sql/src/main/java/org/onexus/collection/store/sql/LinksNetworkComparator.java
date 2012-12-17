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
package org.onexus.collection.store.sql;

import org.onexus.collection.api.utils.FieldLink;
import org.onexus.resource.api.ORI;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinksNetworkComparator implements Comparator<ORI> {

    private ORI fromCollection;
    private Map<ORI, List<FieldLink>> networkLinks;

    public LinksNetworkComparator(Map<ORI, List<FieldLink>> networkLinks, ORI fromCollection) {
        this.networkLinks = networkLinks;
        this.fromCollection = fromCollection;
    }

    @Override
    public int compare(ORI a, ORI b) {

        int a_b = depend(a, b, 0, new HashSet<ORI>());
        int b_a = depend(b, a, 0, new HashSet<ORI>());

        if (a_b != -1 && b_a != -1) {
            if (a_b == 0 && b_a == 0) {

                List<FieldLink> linksA = networkLinks.get(a);
                List<FieldLink> linksB = networkLinks.get(b);

                if (linksA.size() > linksB.size()) {
                    removeLinksToOrFromCollection(linksA, b);
                } else {
                    removeLinksToOrFromCollection(linksB, a);
                }
                return compare(a, b);
            } else {
                throw new RuntimeException("Mutually dependency on different depth level");
            }
        }

        if (a_b != -1) {
            return 1;
        }

        if (b_a != -1) {
            return -1;
        }

        return 0;
    }

    private void removeLinksToOrFromCollection(List<FieldLink> links, ORI collection) {

        for (int i = 0; i < links.size(); i++) {
            FieldLink link = links.get(i);
            if (link.getFromCollection().equals(collection) || link.getToCollection().equals(collection)) {
                links.set(i, null);
            }
        }
        links.removeAll(Collections.singletonList(null));

    }

    /*
        Check if collection a depends on b

        Returns -1 if not depends, 0 if there is a direct dependency. Otherwise the depth of the dependency.

     */
    private int depend(ORI a, ORI b, int depth, Set<ORI> visitedNodes) {

        // End of the path, no dependency if we reach the fromCollection.
        if (b.equals(fromCollection)) {
            return -1;
        }


        // Break cycles
        if (visitedNodes.contains(a)) {
            return -1;
        }

        List<FieldLink> links = networkLinks.get(a);

        if (links == null || links.isEmpty()) {
            return -1;
        }

        // Check direct dependency
        for (FieldLink link : links) {
            if (link.getToCollection().equals(a) && link.getFromCollection().equals(b)) {
                return depth;
            }
            if (link.getToCollection().equals(b) && link.getFromCollection().equals(a)) {
                return depth;
            }
        }

        visitedNodes.add(a);

        // Check derived dependencies
        int minDerived = -1;
        for (FieldLink link : links) {
            ORI nextCollection = (link.getToCollection().equals(a) ? link.getFromCollection() : link.getToCollection());
            int derived = depend(nextCollection, b, depth + 1, visitedNodes);
            if (derived != -1 && (minDerived == -1 || derived < minDerived)) {
                minDerived = derived;
            }
        }

        return minDerived;

    }
}
