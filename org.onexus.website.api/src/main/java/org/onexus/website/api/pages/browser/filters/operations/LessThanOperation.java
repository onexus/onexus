package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.LessThan;

public class LessThanOperation extends FilterOperation {

    public static LessThanOperation INSTANCE = new LessThanOperation();

    private LessThanOperation() {
        super("less than", "<", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new LessThan(alias, fieldId, value);
    }
}
