package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.LessThanOrEqual;

public class LessThanOrEqualOperation extends FilterOperation {

    public static LessThanOrEqualOperation INSTANCE = new LessThanOrEqualOperation();

    private LessThanOrEqualOperation() {
        super("less than or equal", "<=", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new LessThanOrEqual(alias, fieldId, value);
    }
}
