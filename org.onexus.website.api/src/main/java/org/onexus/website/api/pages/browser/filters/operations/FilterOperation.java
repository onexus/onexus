package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.*;

import java.io.Serializable;

public abstract class FilterOperation implements Serializable {

    private String label;
    private String symbol;
    private boolean needsValue;

    protected FilterOperation(String label, String symbol, boolean needsValue) {
        this.label = label;
        this.symbol = symbol;
        this.needsValue = needsValue;
    }

    public String getLabel() {
        return label;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isNeedsValue() {
        return needsValue;
    }

    public String toString() {
        return label;
    }

    public abstract Filter createFilter(String alias, String fieldId, Object value);

    public String createTitle(String headerTitle, Object value) {
        return headerTitle + " " + symbol + (needsValue ? " '" + value + "'" : "");
    }
}
