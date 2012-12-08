package org.onexus.website.api.utils.visible;

import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.pages.browser.IFilter;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VisibleTest {

    @Test
    public void testVisiblePredicate() {

        Predicate emptyPredicate = createPredicate();
        Predicate otherVariantSelected = createPredicate(new MockEntityFilter(new ORI("/data/variants"), "other-value"));
        Predicate myVariantSelected = createPredicate(new MockEntityFilter(new ORI("/data/variants"), "9834yt908008"));

        assertTrue(emptyPredicate.evaluate(new MockVisible("(data/variants[VARIANT=9834yt908008] OR !data/variants)")));
        assertTrue(emptyPredicate.evaluate(new MockVisible("NOT data/variants OR data/variants[VARIANT=9834yt908008]")));

        assertFalse(otherVariantSelected.evaluate(new MockVisible("NOT data/variants OR data/variants[VARIANT=9834yt908008]")));

        assertTrue(myVariantSelected.evaluate(new MockVisible("NOT data/variants OR data/variants[VARIANT=9834yt908008]")));


    }

    private static Predicate createPredicate(IFilter... filter) {
        return new VisiblePredicate(new ORI("http://tests.onexus.org"), Arrays.asList(filter));
    }
}
