package org.onexus.website.api.pages.browser.filters.operations;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.In;

import java.util.*;

public class InListOperation extends FilterOperation {

    public static InListOperation INSTANCE = new InListOperation();

    private InListOperation() {
        super("in list", "in", true);
    }

    @Override
    public Filter createFilter(String alias, String fieldId, Object value) {

        In filter = new In(alias, fieldId);
        for (String val : parseValues(value)) {
            filter.addValue(val.trim());
        }

        return filter;
    }

    private List<String> parseValues(Object value) {

        if (value == null) {
            return Collections.EMPTY_LIST;
        }

        return Arrays.asList(String.valueOf(value).split(","));
    }

    @Override
    public String createTitle(String headerTitle, Object value) {

        StringBuilder title = new StringBuilder();

        title.append(headerTitle);
        title.append(" ");
        title.append(getSymbol());
        title.append(" (");

        Iterator<String> values = parseValues(value).iterator();

        while (values.hasNext()) {
            title.append("'").append(values.next().trim()).append("'");
            if (values.hasNext()) {
                title.append(",");
            }
        }

        title.append(")");
        return title.toString();
    }
}
