package org.onexus.ui.website;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.osgi.framework.ServiceReference;

public class AbstractWebsiteManager<P extends Panel, C extends IWebsiteConfig, S extends IWebsiteStatus> {
    
    private IResourceRegister resourceRegister;
    
    private List<? extends IWebsiteCreator<C,S>> creators;

    @SuppressWarnings("unchecked")
    public P create(String componentId, C config, IModel<S> statusModel) {
		
	for (IWebsiteCreator<C,S> creator : creators) {
	    if (creator.canCreate(config)) {
		return (P) creator.create(componentId, config, statusModel);
	    }
	}
	
	return null;
    }

    public List<? extends IWebsiteCreator<C, S>> getCreators() {
        return creators;
    }

    public void setCreators(List<? extends IWebsiteCreator<C, S>> creators) {
        this.creators = creators;
    }

    public IResourceRegister getResourceRegister() {
        return resourceRegister;
    }

    public void setResourceRegister(IResourceRegister resourceRegister) {
        this.resourceRegister = resourceRegister;
    }
    
    public void bindCreators(ServiceReference serviceRef) {
	
	if (resourceRegister != null && creators != null) {
	    for (IWebsiteCreator<C,S> bc : creators) {
		bc.register(resourceRegister);
	    }
	}
    }
    
    public void unbindCreators(ServiceReference serviceRef) {
	// Nothing to do
    }

}
