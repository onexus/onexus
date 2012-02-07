package org.onexus.ui.website.tabs;

import org.apache.wicket.model.IModel;

public interface ITabManager {

    Tab<?,?> create(String componentId, TabConfig config, IModel<TabStatus> statusModel);

}
