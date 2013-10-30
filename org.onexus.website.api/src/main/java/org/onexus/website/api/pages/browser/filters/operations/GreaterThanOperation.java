package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.GreaterThan;

public class GreaterThanOperation extends FilterOperation {

    public static GreaterThanOperation INSTANCE = new GreaterThanOperation();

    private GreaterThanOperation() {
        super("greater than", ">", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new GreaterThan(alias, fieldId, value);
    }
}
