package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.IsNull;
import org.onexus.collection.api.query.Not;

public class IsNotEmptyOperation extends FilterOperation {

    public static IsNotEmptyOperation INSTANCE = new IsNotEmptyOperation();

    private IsNotEmptyOperation() {
        super("is not empty", "is not empty", false);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new Not(new IsNull(alias, fieldId));
    }
}
