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
package org.onexus.collection.api;

import org.junit.Test;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.resource.api.ORI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LinkUtilsTest {

    @Test
    public void testLinkFields() {

        ORI parentORI = new ORI("http://test.onexus.org");

        ORI aOri = new ORI("http://test.onexus.org?collections/a");
        ORI bOri = new ORI("http://test.onexus.org?collections/b");

        String aFieldId = "aTestField";
        String bFieldId = "bTestField";

        Collection a = new Collection();
        a.setORI(aOri);
        a.setFields(Arrays.asList(new Field[]{new Field(aFieldId, aFieldId, aFieldId, String.class, true)}));
        a.setLinks(Arrays.asList(new Link[]{
                new Link(bOri, aFieldId + " == " + bFieldId)
        }));
        Collection b = new Collection();
        b.setORI(bOri);
        b.setFields(Arrays.asList(new Field[]{new Field(bFieldId, bFieldId, bFieldId, String.class, false)}));
        b.setLinks(Collections.EMPTY_LIST);

        List<FieldLink> links = LinkUtils.getLinkFields(parentORI, a, b);

        assertEquals(links.size(), 1);
        assertArrayEquals(links.toArray(), new FieldLink[]{new FieldLink(aOri, aFieldId, bOri, bFieldId)});

        links = LinkUtils.getLinkFields(parentORI, b, a);
        assertEquals(links.size(), 0);

    }
}
