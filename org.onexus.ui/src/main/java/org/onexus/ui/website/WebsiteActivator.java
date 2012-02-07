package org.onexus.ui.website;

import org.onexus.ui.IResourceActivator;
import org.onexus.ui.IResourceRegister;

public class WebsiteActivator implements IResourceActivator {

    @Override
    public void bind(IResourceRegister resourceRegister) {
	resourceRegister.register(WebsiteConfig.class);
    }

    @Override
    public void unbind(IResourceRegister resourceRegister) {
    }

}
