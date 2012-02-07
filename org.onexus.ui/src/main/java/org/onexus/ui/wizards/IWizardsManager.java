package org.onexus.ui.wizards;

import java.util.List;

import org.onexus.core.resources.Resource;
import org.onexus.ui.IWizardCreator;

public interface IWizardsManager {
    
    public List<IWizardCreator> getWizardCreators(Resource resource);

}
