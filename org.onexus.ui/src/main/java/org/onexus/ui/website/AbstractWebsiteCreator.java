package org.onexus.ui.website;

import org.onexus.ui.IResourceRegister;

public abstract class AbstractWebsiteCreator<C extends IWebsiteConfig, S extends IWebsiteStatus> implements IWebsiteCreator<C, S> {
    
    private Class<? extends C> configType;
    private String title;
    private String description;
    
    public AbstractWebsiteCreator(Class<? extends C> configType, String title, String description) {
	super();
	this.configType = configType;
	this.title = title;
	this.description = description;
    }
    
    public boolean canCreate(C config) {
	if (config == null) {
	    return false;
	}
	
	return config.getClass().equals(configType);
    }
    
    public String getTitle() {
	return title;
    }

    public String getDescription() {
	return description;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void register(IResourceRegister resourceRegister) {
	resourceRegister.register(configType);	
    }

}
