package org.onexus.core;

import java.io.File;
import java.net.URL;
import java.util.List;

public interface ISourceManager {
    
    public void store(String sourceURI, File sourceFile, boolean moveSourceFile);
    
    public List<URL> retrieve(String sourceURI);

}
