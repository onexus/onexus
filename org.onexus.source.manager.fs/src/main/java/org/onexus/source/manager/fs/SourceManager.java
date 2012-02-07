/**
 *  Copyright 2011 Universitat Pompeu Fabra.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.onexus.source.manager.fs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.onexus.core.ISourceManager;
import org.onexus.core.resources.Resource;
import org.onexus.core.utils.ResourceTools;

public class SourceManager implements ISourceManager {
    
    public final static String ONEXUS_REPOSITORY_ENV = "ONEXUS_REPOSITORY";
    
    private String repoPath;
    
    public SourceManager() {
	super();
	
	repoPath = System.getenv(ONEXUS_REPOSITORY_ENV);
	
	if (repoPath == null) {
	    repoPath = "repository";
	}
    }

    @Override
    public void store(String sourceURI, File sourceFile, boolean moveSourceFile) {
	
	String sourceContainer = repoPath + File.separator + convertURItoContainerPath(sourceURI);
	String sourceName = ResourceTools.getResourceName(sourceURI);
	File destDir = new File(sourceContainer);
	
	try {
	    
	    if (!destDir.exists()) {
	         destDir.mkdirs();
	    }
	
	    if (moveSourceFile) {
		FileUtils.moveFile(sourceFile, new File(destDir, sourceName));
	    } else {
		FileUtils.copyFile(sourceFile, new File(destDir, sourceName), true);
	    }
	    
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
		
    }

    private String convertURItoContainerPath(String sourceURI) {
	String relativePath = ResourceTools.getParentURI(sourceURI);
	relativePath = relativePath.replace("http://", "");
	relativePath = relativePath.replaceAll(String.valueOf(Resource.SEPARATOR), File.separator);
	return relativePath;
    }

    @Override
    public List<URL> retrieve(String sourceURI) {
	
	String sourceContainer = repoPath + File.separator + convertURItoContainerPath(sourceURI);
	String sourceName = ResourceTools.getResourceName(sourceURI);
	File destDir = new File(sourceContainer);
	
	File source = new File(destDir, sourceName);
	
	List<URL> urls = new ArrayList<URL>();
	try {
	    urls.add(source.toURI().toURL());
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	}
	
	return urls;
    }

}
