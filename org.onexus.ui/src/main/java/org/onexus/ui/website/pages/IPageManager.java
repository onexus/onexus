package org.onexus.ui.website.pages;

import org.apache.wicket.model.IModel;

public interface IPageManager {

    Page<?,?> create(String componentId, PageConfig config, IModel<PageStatus> statusModel);

}
