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
package org.onexus.website.widget.utils.visible;

import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.IEntitySelection;
import org.onexus.website.api.utils.visible.VisiblePredicate;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VisibleTest {

    @Test
    public void testVisiblePredicate() {

        Predicate emptyPredicate = createPredicate();
        Predicate otherVariantSelected = createPredicate(new MockEntitySelection(new ORI("/data/variants"), "other-value"));
        Predicate myVariantSelected = createPredicate(new MockEntitySelection(new ORI("/data/variants"), "9834yt908008"));

        assertTrue(emptyPredicate.evaluate(new MockVisible("(data/variants[VARIANT=9834yt908008] OR !data/variants)")));
        assertTrue(emptyPredicate.evaluate(new MockVisible("NOT data/variants OR data/variants[VARIANT=9834yt908008]")));

        assertFalse(otherVariantSelected.evaluate(new MockVisible("NOT data/variants OR data/variants[VARIANT=9834yt908008]")));

        assertTrue(myVariantSelected.evaluate(new MockVisible("NOT data/variants OR data/variants[VARIANT=9834yt908008]")));


    }

    private static Predicate createPredicate(IEntitySelection... filter) {
        return new VisiblePredicate(new ORI("http://tests.onexus.org"), Arrays.asList(filter));
    }
}
