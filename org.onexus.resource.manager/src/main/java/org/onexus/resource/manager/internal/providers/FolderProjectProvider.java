/**
 *  Copyright 2012 Universitat Pompeu Fabra.
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
package org.onexus.resource.manager.internal.providers;


import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.onexus.resource.api.IResourceListener;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.List;

public class FolderProjectProvider extends AbstractProjectProvider {
    public FolderProjectProvider(String projectName, String projectUrl, File projectFolder, FileAlterationMonitor monitor, List<IResourceListener> listeners) throws InvalidParameterException {
        super(projectName, projectUrl, projectFolder, monitor, listeners);

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
