package org.onexus.resource.api.exceptions;

public class OnexusException extends RuntimeException {

    public OnexusException() {
    }

    public OnexusException(String message) {
        super(message);
    }

    public OnexusException(String message, Throwable cause) {
        super(message, cause);
    }
}
