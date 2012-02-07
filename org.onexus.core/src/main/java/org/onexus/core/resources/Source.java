package org.onexus.core.resources;


public class Source extends Resource {
    
    private String contentType;
    
    public Source() {
	super();
    }
    
    public Source(String contentType) {
	super();
	this.contentType = contentType;
    }
    
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
