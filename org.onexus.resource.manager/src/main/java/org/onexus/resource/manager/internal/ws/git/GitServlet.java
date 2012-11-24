package org.onexus.resource.manager.internal.ws.git;


import org.onexus.resource.manager.internal.ProjectsContainer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Enumeration;

public class GitServlet extends org.eclipse.jgit.http.server.GitServlet implements ServletConfig {

    private ServletConfig config;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        this.config = config;
        super.init(this);
    }

    @Override
    public String getServletName() {
        return GitServlet.class.getName();
    }

    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    public String getInitParameter(String name) {
        if (name.equals("base-path")) {
            return ProjectsContainer.ONEXUS_PROJECTS_FOLDER;
        }
        if (name.equals("export-all")) {
            return "1";
        }
        return config.getInitParameter(name);
    }

    public Enumeration getInitParameterNames() {
        return config.getInitParameterNames();
    }



}
