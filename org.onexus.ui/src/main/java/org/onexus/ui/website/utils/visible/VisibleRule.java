package org.onexus.ui.website.utils.visible;

import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;
import org.onexus.core.utils.ResourceUtils;
import org.onexus.ui.website.pages.browser.IFilter;
import org.onexus.ui.website.utils.EntityModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VisibleRule implements Serializable {

    private String releaseUri;
    private String filteredCollection;
    private String field;
    private String operator;
    private String value;
    private boolean negated;

    private VisibleRule(String releaseUri, String rule) {
        super();

        this.releaseUri = releaseUri;

        rule = rule.trim();

        negated = rule.startsWith("!");

        filteredCollection = rule.replace("!", "");
        int ini = filteredCollection.indexOf("[");
        if (ini != -1) {
            filteredCollection = filteredCollection.substring(0, ini).trim();

            int end = rule.indexOf("]");
            String values[] = rule.substring(ini + 1, end).split(" ");

            if (values.length > 0) field = values[0].trim();
            if (values.length > 1) operator = values[1].trim();
            if (values.length > 2) value = values[2].trim();

        }

    }

    public String getReleaseUri() {
        return releaseUri;
    }

    public String getFilteredCollection() {
        return filteredCollection;
    }

    public String getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public boolean isNegated() {
        return negated;
    }

    public static List<VisibleRule> parseRules(String releaseUri, String visible ) {

        List<VisibleRule> rules = new ArrayList<VisibleRule>();

        if (visible == null) {
            return rules;
        }

        for (String rule : visible.split(",")) {
            rules.add(new VisibleRule(releaseUri, rule));
        }

        return rules;
    }

}