package org.onexus.core.resources;

import java.util.List;

public class AbstractMetadata implements IMetadata {

    private String label;
    private String title;
    private String description;
    private List<Property> properties;

    public AbstractMetadata() {
        super();
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public String getProperty(String key) {
        if (this.properties == null) {
            return null;
        }

        for (Property p : this.properties) {
            if (p.getKey().equals(key)) {
                return p.getValue();
            }
        }
        return null;
    }

}
