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
package org.onexus.data.manager.internal.ws;

import org.apache.commons.io.IOUtils;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.session.LoginContext;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DsServlet extends HttpServlet {

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

        HttpSession session = req.getSession();
        if (session != null && LoginContext.get(session.getId()) != null) {
            LoginContext ctx = LoginContext.get(session.getId());
            LoginContext.set(ctx, null);
        } else {
            LoginContext.set(LoginContext.ANONYMOUS_CONTEXT, null);
        }

        ORI dataResource = requestToORI(req);

        if (dataResource != null) {

            try {
                IDataStreams streams = dataManager.load(dataResource);

                // Minimum Response header information (size and MIME type)
                long size = dataManager.size(dataResource);
                resp.setContentLength((int) size);
                resp.setContentType(getServletContext().getMimeType(dataResource.getPath()));

                OutputStream out = resp.getOutputStream();
                for (InputStream in : streams) {
                    IOUtils.copy(in, out);
                    in.close();
                }
                out.close();
            } catch (SecurityException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
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

        LoginContext ctx = LoginContext.get();
        try {
            LoginContext.set(LoginContext.SERVICE_CONTEXT, null);

            String uri = req.getRequestURI();
            String servletPath = req.getServletPath();

            if (uri.length() == servletPath.length()) {
                return null;
            }

            String projectNameAndResource = uri.substring(servletPath.length() + 1);
            String projectUrl = null;
            String projectName = null;
            for (Project project : resourceManager.getProjects()) {
                projectName = project.getName();
                if (projectNameAndResource.startsWith(projectName)) {
                    projectUrl = project.getURL();
                    break;
                }
            }

            if (projectUrl == null) {
                return null;
            }

            String resourcePath = projectNameAndResource.replaceFirst(projectName, "");

            return new ORI(projectUrl, resourcePath);

        } finally {
            LoginContext.set(ctx, null);
        }

    }
}
