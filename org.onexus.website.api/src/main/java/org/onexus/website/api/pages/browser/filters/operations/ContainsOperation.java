package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Contains;
import org.onexus.collection.api.query.Filter;

public class ContainsOperation extends FilterOperation {

    public static ContainsOperation INSTANCE = new ContainsOperation();

    private ContainsOperation() {
        super("contains", "contains", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {
        return new Contains(alias, fieldId, value);
    }
}
