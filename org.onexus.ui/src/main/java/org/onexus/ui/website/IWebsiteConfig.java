package org.onexus.ui.website;

import java.io.Serializable;

public interface IWebsiteConfig extends Serializable {
    
    public abstract Serializable getDefaultStatus();

    public abstract Serializable createEmptyStatus();

}