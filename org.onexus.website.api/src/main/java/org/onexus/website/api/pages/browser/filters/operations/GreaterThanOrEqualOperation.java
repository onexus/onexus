package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.GreaterThanOrEqual;

public class GreaterThanOrEqualOperation extends FilterOperation {

    public static GreaterThanOrEqualOperation INSTANCE = new GreaterThanOrEqualOperation();

    private GreaterThanOrEqualOperation() {
        super("greater than or equal", ">=", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new GreaterThanOrEqual(alias, fieldId, value);
    }
}
