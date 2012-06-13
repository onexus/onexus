package org.onexus.ui.workspace.pages.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.onexus.core.IResourceManager;
import org.onexus.core.resources.Project;
import org.onexus.ui.workspace.pages.ResourcesPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AddNewProjectTool extends AbstractTool<String> {

    private static final Logger log = LoggerFactory.getLogger(AddNewProjectTool.class);

    @Inject
    public IResourceManager resourceManager;

    public AddNewProjectTool() {
        super(Model.of(""));

        Form form = new Form<String>("form") {
            @Override
            protected void onSubmit() {
                String projectURL = AddNewProjectTool.this.getModelObject();

                if (StringUtils.isEmpty(projectURL)) {
                    error("Empty project URL");
                } else {

                    if (projectURL.startsWith(File.separator)) {
                        projectURL = "file:" + projectURL;
                    }

                    try {
                        URL url = new URL(projectURL);

                        ZipInputStream zis = new ZipInputStream(url.openStream());


                        String userHome = System.getProperty("user.home");
                        if (userHome == null) {
                            throw new IllegalStateException("user.home==null");
                        }
                        File home = new File(userHome);
                        File projectsDirectory = new File(home, ".onexus/projects");
                        if (!projectsDirectory.exists()) {
                            if (!projectsDirectory.mkdirs()) {
                                throw new IllegalStateException(projectsDirectory.toString());
                            }
                        }

                        byte[] buffer = new byte[1024];
                        ZipEntry ze = zis.getNextEntry();

                        Properties properties = new Properties();
                        File projectsIni = new File(home, ".onexus" + File.separator + "projects.ini");
                        properties.load(new FileInputStream(projectsIni));

                        String projectName = url.getFile() + "-" + Integer.toHexString(url.hashCode());
                        if (ze.isDirectory()) {
                            projectName = ze.getName();
                            projectName = projectName.substring(0, projectName.length() - 1);
                        } else {
                            projectsDirectory = new File(projectsDirectory, projectName);

                            if (!projectsDirectory.exists()) {
                                projectsDirectory.mkdir();
                            }
                        }

                        while (ze != null) {

                            String fileName = ze.getName();

                            File newFile = new File(projectsDirectory, fileName);
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


                        File projectHome = new File(home, ".onexus" + File.separator + "projects" + File.separator + projectName);
                        properties.setProperty(projectName, projectHome.getAbsolutePath());
                        properties.store(new FileOutputStream(projectsIni), "Onexus projects");

                        resourceManager.checkout();

                        List<Project> projects = resourceManager.loadChildren(Project.class, null);

                        Project newProject = null;
                        for (Project project : projects) {
                            if (project.getName().equals(projectName)) {
                                newProject = project;
                                break;
                            }
                        }

                        if (newProject != null) {
                            PageParameters parameters = new PageParameters();
                            parameters.set(ResourcesPage.RESOURCE, newProject.getURI());
                            setResponsePage(ResourcesPage.class, parameters);
                        }

                    } catch (MalformedURLException e) {
                        error("Malformed URL");
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }


            }
        };

        form.add(new TextField<String>("text", getModel()));

        add(form);
    }
}
