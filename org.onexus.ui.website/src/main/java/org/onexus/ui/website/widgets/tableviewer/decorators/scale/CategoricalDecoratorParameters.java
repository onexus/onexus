package org.onexus.ui.website.widgets.tableviewer.decorators.scale;

import org.onexus.resource.api.ParameterKey;

public enum CategoricalDecoratorParameters implements ParameterKey {

    DEFAULT("default", "Set the default color. Syntax: [r,g,b]", false),
    MAP("map", "Set the color map. Example: [blue value]=[0,0,255] | [red value]=[255,0,0]", false);

    private final String key;
    private final String description;
    private final boolean optional;

    private CategoricalDecoratorParameters(String key, String description, boolean optional) {
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
