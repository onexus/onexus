package org.onexus.ui;

import org.apache.wicket.IClusterable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.onexus.core.resources.Resource;

public interface IWizardCreator extends IClusterable {

    String getTitle();

    /**
     * @param containerId
     *            returned panel MUST have this id
     * @return a container object (e.g. Panel or Fragment) that will be placed
     *         as the content of the tab
     */
    Panel getPanel(final String containerId, final IModel<? extends Resource> model);
    
    double getOrder();    
    
    boolean isVisible(Class<? extends Resource> resourceType);

}
