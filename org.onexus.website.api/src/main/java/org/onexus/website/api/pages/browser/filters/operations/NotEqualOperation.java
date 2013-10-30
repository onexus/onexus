package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.NotEqual;

public class NotEqualOperation extends FilterOperation {

    public static NotEqualOperation INSTANCE = new NotEqualOperation();

    private NotEqualOperation() {
        super("not equal", "&ne;", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new NotEqual(alias, fieldId, value);
    }
}
