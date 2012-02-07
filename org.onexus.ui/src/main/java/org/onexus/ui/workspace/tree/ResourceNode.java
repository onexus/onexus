package org.onexus.ui.workspace.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.onexus.core.resources.Resource;

public class ResourceNode implements Serializable {
    
    private static Map<String, DefaultMutableTreeNode> nodes = new HashMap<String, DefaultMutableTreeNode>();

    private String uri;
    private String name;
    
    private ResourceNode(String name) {
	super();
	this.name = name;
    }

    private ResourceNode(Resource resource) {
	super();

	this.uri = resource.getURI();
	this.name = resource.getName();
    }
    
    public static DefaultMutableTreeNode get(Resource resource) {
	
	if (resource == null) {
	    return null;
	}
	
	String resourceURI = resource.getURI();
	
	if (resourceURI == null) {
	    return null;
	}
	
	return nodes.get(resourceURI);
    }
    
    public static DefaultMutableTreeNode create(Resource resource) {
	
	if (resource == null) {
	    return new DefaultMutableTreeNode(new ResourceNode("empty"));
	}
	
	String resourceURI = resource.getURI();
	
	if (resourceURI == null) {
	    return null;
	}
	
	nodes.put(resourceURI, new DefaultMutableTreeNode(new ResourceNode(resource)));
	
	return nodes.get(resourceURI);
    }


    public String getUri() {
	return uri;
    }

    public String getName() {
	return name;
    }

    @Override
    public String toString() {
	return name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((uri == null) ? 0 : uri.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ResourceNode other = (ResourceNode) obj;
	if (uri == null) {
	    if (other.uri != null)
		return false;
	} else if (!uri.equals(other.uri))
	    return false;
	return true;
    }
   
}
