package org.onexus.ui.wizards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.onexus.core.resources.Resource;
import org.onexus.ui.IWizardCreator;

public class DefaultWizardsManager implements IWizardsManager {
    
    public static final WizardCreatorComparator WIZARD_CREATOR_COMPRATOR = new WizardCreatorComparator();
    
    private List<IWizardCreator> wizardCreators;
    
    public DefaultWizardsManager() {
	super();
    }
    

    public List<IWizardCreator> getWizardCreators() {
        return wizardCreators;
    }

    public void setWizardCreators(List<IWizardCreator> wizardCreators) {
        this.wizardCreators = wizardCreators;
    }

    @Override
    public List<IWizardCreator> getWizardCreators(Resource resource) {
	
	if (resource == null) {
	    return Collections.emptyList();
	}

	List<IWizardCreator> result = new ArrayList<IWizardCreator>();
	CollectionUtils.select(wizardCreators, new VisiblePredicate(resource), result);
	Collections.sort(result, WIZARD_CREATOR_COMPRATOR);

	return result;
    }
    
    private static class VisiblePredicate implements Predicate, Serializable {

	private Resource resource;

	public VisiblePredicate(Resource resource) {
	    super();
	    this.resource = resource;
	}

	@Override
	public boolean evaluate(Object object) {

	    if (object instanceof IWizardCreator) {
		IWizardCreator creator = (IWizardCreator) object;

		if (resource == null) {
		    return false;
		}

		return creator.isVisible(resource.getClass());
	    }

	    return false;
	}

    }

    private static class WizardCreatorComparator implements Comparator<IWizardCreator> {

	@Override
	public int compare(IWizardCreator o1, IWizardCreator o2) {
	    return Double.compare(o1.getOrder(), o2.getOrder());
	}

    }

}
