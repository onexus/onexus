package org.onexus.ui.website.widgets.tags;

import org.apache.wicket.model.IModel;
import org.onexus.ui.IResourceRegister;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class TagWidgetCreator extends AbstractWidgetCreator<TagWidgetConfig, TagWidgetStatus> {
    
    public TagWidgetCreator() {
	super(TagWidgetConfig.class, "tag-widget", "Create and manage labels to the table rows.");
    }

    @Override
    protected Widget<?,?> build(String componentId, TagWidgetConfig config, IModel<TagWidgetStatus> statusModel) {
	return new TagWidget(componentId, config, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
	resourceRegister.register(TagWidgetConfig.class);
	resourceRegister.register(TagColumnConfig.class);	
    }
    
    

}
