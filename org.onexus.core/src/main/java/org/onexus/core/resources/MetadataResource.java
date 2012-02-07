package org.onexus.core.resources;

import java.util.ArrayList;
import java.util.List;

public abstract class MetadataResource extends Resource implements IMetadata {
    
    private String title;
    private String description;
    private List<Property> properties = new ArrayList<Property>();

    
    public MetadataResource() {
	super();
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String getProperty(String propertyKey) {
	for (Property property : properties) {
	    if (property.getKey().equals(propertyKey)) {
		return property.getValue();
	    }
	}

	return null;
    }

    @Override
    public List<Property> getProperties() {
	return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    } 
}
