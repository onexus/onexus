package org.onexus.ui.website.tabs.topleft;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.tabs.AbstractTabCreator;
import org.onexus.ui.website.tabs.Tab;

public class TopleftTabCreator extends AbstractTabCreator<TopleftTabConfig, TopleftTabStatus>{

    public TopleftTabCreator() {
	super(TopleftTabConfig.class, "topleft-tab", "Tab with a left column for widgets a top bar and a viewer");
	
    }

    @Override
    protected Tab<?, ?> build(String componentId, TopleftTabConfig config, IModel<TopleftTabStatus> statusModel) {
	return new TopleftTab(componentId, config, statusModel);
    }

}
