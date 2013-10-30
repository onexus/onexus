package org.onexus.website.api.pages.browser.filters.panels;

import org.onexus.website.api.pages.browser.filters.operations.FilterOperation;

import java.io.Serializable;

public class FilterOption<T> implements Serializable {

    private FilterOperation operation;
    private T value;

    public FilterOption(FilterOperation operation) {
        this.operation = operation;
    }

    public FilterOption(FilterOperation operation, T value) {
        this.operation = operation;
        this.value = value;
    }

    public FilterOperation getOperation() {
        return operation;
    }

    public void setOperation(FilterOperation operation) {
        this.operation = operation;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "operation='" + operation + '\'' + ", value='" + value + '\'';
    }
}