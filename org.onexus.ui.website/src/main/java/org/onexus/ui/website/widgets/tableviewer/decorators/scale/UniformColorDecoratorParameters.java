package org.onexus.ui.website.widgets.tableviewer.decorators.scale;

import org.onexus.resource.api.ParameterKey;

public enum UniformColorDecoratorParameters implements ParameterKey {

    COLOR("color", "Background color. Syntax: [r,g,b]", false);

    private final String key;
    private final String description;
    private final boolean optional;

    private UniformColorDecoratorParameters(String key, String description, boolean optional) {
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
