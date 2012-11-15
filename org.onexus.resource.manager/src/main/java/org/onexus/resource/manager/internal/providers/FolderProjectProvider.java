package org.onexus.resource.manager.internal.providers;


import java.io.File;
import java.security.InvalidParameterException;

public class FolderProjectProvider extends ProjectProvider {
    public FolderProjectProvider(String projectName, String projectUrl, File projectFolder) throws InvalidParameterException {
        super(projectName, projectUrl, projectFolder);

        if (projectUrl.startsWith("file")) {
            File urlFolder = new File(projectUrl.replace("file://", ""));
            if (!urlFolder.getAbsolutePath().equals(projectFolder.getAbsolutePath())) {
                setProjectFolder(urlFolder);
            }
        }
    }

    @Override
    protected void importProject() {
        // Nothing to do. It's a always up to date project
    }
}
