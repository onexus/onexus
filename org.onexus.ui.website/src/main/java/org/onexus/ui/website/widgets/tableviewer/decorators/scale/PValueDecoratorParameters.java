package org.onexus.ui.website.widgets.tableviewer.decorators.scale;

import org.onexus.resource.api.ParameterKey;

public enum PValueDecoratorParameters implements ParameterKey {

    SHOW_VALUE("show-value", "Print the value inside the cell. Valid values: true, false", true),
    URL("url", "The URL where do you want to link, with fields values as ${[field_id]}", false);

    private final String key;
    private final String description;
    private final boolean optional;

    private PValueDecoratorParameters(String key, String description, boolean optional) {
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
