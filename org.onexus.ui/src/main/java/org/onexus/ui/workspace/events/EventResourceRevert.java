package org.onexus.ui.workspace.events;

import java.io.Serializable;

public class EventResourceRevert implements Serializable {
    
    public final static EventResourceRevert EVENT = new EventResourceRevert();

    private EventResourceRevert() {
	super();
    }

}
