package org.onexus.collection.api;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.onexus.collection.api.utils.FieldLink;
import org.onexus.collection.api.utils.LinkUtils;
import org.onexus.resource.api.ORI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        a.setLinks(Arrays.asList(new Link[]{
                new Link(bOri, aFieldId + " == " + bFieldId)
        }));
        Collection b = new Collection();
        b.setORI(bOri);
        b.setLinks(Collections.EMPTY_LIST);

        List<FieldLink> links = LinkUtils.getLinkFields(parentORI, a, b);

        assertEquals(links.size(), 1);
        assertArrayEquals(links.toArray(), new FieldLink[] { new FieldLink(aOri, aFieldId, bOri, bFieldId)});

    }
}
