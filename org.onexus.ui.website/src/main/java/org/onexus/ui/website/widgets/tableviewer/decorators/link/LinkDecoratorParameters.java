package org.onexus.ui.website.widgets.tableviewer.decorators.link;

import org.onexus.resource.api.ParameterKey;


public enum LinkDecoratorParameters implements ParameterKey {

    URL("url", "The URL where do you want to link, with fields values as ${[field_id]}", false),
    TARGET("target", "The link target", true),
    SEPARATOR("separator", "The character separator of a list.", true),
    LENGTH("length", "Maximum number of characters", true),
    ICON("icon", "Use an icon to link", true),
    ICON_TITLE("icon-title", "Title to use as icon tooltip", true);

    private final String key;
    private final String description;
    private final boolean optional;

    private LinkDecoratorParameters(String key, String description, boolean optional) {
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


