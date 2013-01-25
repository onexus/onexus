package org.onexus.resource.api.exceptions;

import org.onexus.resource.api.ORI;

public class ResourceNotFoundException extends OnexusException {

    public ResourceNotFoundException(ORI resourceOri) {
        super("Resource '" + resourceOri + "' is not defined in any project.");
    }
}
