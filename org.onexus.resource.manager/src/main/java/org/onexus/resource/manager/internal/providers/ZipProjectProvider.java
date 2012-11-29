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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.onexus.resource.api.IResourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipProjectProvider extends AbstractProjectProvider {

    private static final Logger log = LoggerFactory.getLogger(ZipProjectProvider.class);

    public ZipProjectProvider(String projectName, String projectUrl, File projectFolder, FileAlterationMonitor monitor, List<IResourceListener> listeners) throws InvalidParameterException {
        super(projectName, projectUrl, projectFolder, monitor, listeners);
    }

    @Override
    protected void importProject() {

        try {

            byte[] buffer = new byte[1024];
            URL url = new URL(getProjectUrl());
            ZipInputStream zis = new ZipInputStream(url.openStream());
            ZipEntry ze = zis.getNextEntry();

            File projectFolder = getProjectFolder();
            if (!projectFolder.exists()) {
                projectFolder.mkdir();
            } else {
                FileUtils.cleanDirectory(projectFolder);
            }

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(projectFolder, fileName);
                if (ze.isDirectory()) {
                    newFile.mkdir();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }

                ze = zis.getNextEntry();

            }
            zis.close();

        } catch (Exception e) {

            log.error("Importing project '" + getProjectUrl() +"'", e);
            throw new InvalidParameterException("Invalid Onexus URL project");
        }

        //To change body of implemented methods use File | Settings | File Templates.
    }

}