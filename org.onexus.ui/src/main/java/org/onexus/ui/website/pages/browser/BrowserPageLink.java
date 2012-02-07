package org.onexus.ui.website.pages.browser;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.onexus.ui.website.events.AbstractEvent;

public abstract class BrowserPageLink<T> extends AjaxLink<T> {
    
    public BrowserPageLink(String id) {
	super(id);
	
    }
    
    public BrowserPageLink(String id, IModel<T> model) {
	super(id, model);
    }

    protected BrowserPageStatus getBrowserPageStatus() {
	BrowserPage browser = findParent(BrowserPage.class);
	return browser.getStatus();	
    }
    
    protected void sendEvent(AbstractEvent event) {
	send(getPage(), Broadcast.BREADTH, event);
    }
    
    

}
