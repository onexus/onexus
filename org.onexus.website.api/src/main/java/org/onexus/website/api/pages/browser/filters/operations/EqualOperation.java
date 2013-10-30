package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Equal;
import org.onexus.collection.api.query.Filter;

public class EqualOperation extends FilterOperation {

    public static EqualOperation INSTANCE = new EqualOperation();

    private EqualOperation() {
        super("equal", "=", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new Equal(alias, fieldId, value);
    }
}
