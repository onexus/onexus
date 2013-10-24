package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IsNull;

public class IsEmptyOperation extends FilterOperation {

    public static IsEmptyOperation INSTANCE = new IsEmptyOperation();

    private IsEmptyOperation() {
        super("is empty", "is empty", false);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new IsNull(alias, fieldId);
    }
}
