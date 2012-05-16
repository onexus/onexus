package org.onexus.core.exceptions;

public class UnserializeException extends RuntimeException {

    private String path;
    private String line;

    public UnserializeException(String path, String line, Throwable e) {
        super("At line " + line + " on path " + path, e);
        this.line = line;
        this.path = path;
    }

    public String getLine() {
        return line;
    }

    public String getPath() {
        return path;
    }
}
