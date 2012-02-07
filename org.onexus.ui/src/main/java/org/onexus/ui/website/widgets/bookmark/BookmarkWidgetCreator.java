package org.onexus.ui.website.widgets.bookmark;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class BookmarkWidgetCreator extends AbstractWidgetCreator<BookmarkWidgetConfig, BookmarkWidgetStatus> {
    
    public BookmarkWidgetCreator() {
	super(BookmarkWidgetConfig.class, "bookmark-widget", "Create linkable bookmarks.");
    }

    @Override
    protected Widget<?,?> build(String componentId, BookmarkWidgetConfig config, IModel<BookmarkWidgetStatus> statusModel) {
	return new BookmarkWidget(componentId, config, statusModel);
    }

}
