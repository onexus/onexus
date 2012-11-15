package org.onexus.data.manager.internal.ws;

import org.apache.commons.io.IOUtils;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WsServlet extends HttpServlet {

    private IResourceManager resourceManager;
    private IDataManager dataManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        response(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        response(req, resp);
    }

    public void response(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ORI dataResource = requestToORI(req);

        if (dataResource != null) {

            try {
                IDataStreams streams = dataManager.load(dataResource);
                OutputStream out = resp.getOutputStream();
                for (InputStream in : streams) {
                    IOUtils.copy(in, out);
                }
                out.close();
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public IDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    private ORI requestToORI(HttpServletRequest req) {

        String uri = req.getRequestURI();
        String servletPath = req.getServletPath();

        if (uri.length() == servletPath.length()) {
            return null;
        }

        String projectName = uri.substring(servletPath.length() + 1);

        String projectUrl = null;
        for(Project project : resourceManager.getProjects()) {
            String name = project.getName();
            if (name.equals(projectName)) {
                projectUrl = project.getURL();
                break;
            }
        }

        if (projectUrl == null) {
            return null;
        }

        String queryString = req.getQueryString();
        return new ORI(projectUrl, '/' + queryString);

    }
}
