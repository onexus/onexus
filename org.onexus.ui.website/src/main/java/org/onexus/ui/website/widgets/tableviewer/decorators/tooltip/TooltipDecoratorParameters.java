package org.onexus.ui.website.widgets.tableviewer.decorators.tooltip;

import org.onexus.resource.api.ParameterKey;


public enum TooltipDecoratorParameters implements ParameterKey {

    LENGTH("length", "Number of characters to show", false);

    private final String key;
    private final String description;
    private final boolean optional;

    private TooltipDecoratorParameters(String key, String description, boolean optional) {
        this.key = key;
        this.description = description;
        this.optional = optional;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }


}


