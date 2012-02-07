package org.onexus.ui.website;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;

public interface IWebsiteCreator<C extends IWebsiteConfig, S extends IWebsiteStatus> {

    public void register(IResourceRegister resourceRegister);

    public boolean canCreate(C config);

    public String getTitle();

    public String getDescription();

    public Panel create(String componentId, C config, IModel<S> statusModel);

}