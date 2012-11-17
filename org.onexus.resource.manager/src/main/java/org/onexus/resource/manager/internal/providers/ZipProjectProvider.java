package org.onexus.resource.manager.internal.providers;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipProjectProvider extends ProjectProvider {

    private static final Logger log = LoggerFactory.getLogger(ZipProjectProvider.class);

    public ZipProjectProvider(String projectName, String projectUrl, File projectFolder) throws InvalidParameterException {
        super(projectName, projectUrl, projectFolder);

        if (!projectFolder.exists()) {


        }
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