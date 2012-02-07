package org.onexus.ui.workspace.events;

import java.io.Serializable;

public class EventResourceSync implements Serializable {
    
    public final static EventResourceSync EVENT = new EventResourceSync();

    private EventResourceSync() {
	super();
    }

}
