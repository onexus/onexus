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
package org.onexus.website.api.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.util.string.Strings;
import org.onexus.data.api.Data;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.Folder;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.session.LoginContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DsServlet extends HttpServlet {

    private Project project;
    private IDataManager dataManager;
    private IResourceManager resourceManager;

    public DsServlet(Project project, IDataManager dataManager, IResourceManager resourceManager) {
        this.project = project;
        this.dataManager = dataManager;
        this.resourceManager = resourceManager;
    }

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

        String uri = req.getRequestURI();
        String servletPath = req.getServletPath();

        if (uri.length() <= servletPath.length()) {
            resp.sendRedirect(req.getRequestURI() + "/");
            return;
        }

        ORI ori = requestToORI(uri, servletPath);

        try {
            Resource resource = resourceManager.load(Resource.class, ori);

            if (resource instanceof Folder || resource instanceof Project) {
                if (req.getRequestURI().endsWith("/")) {
                    ori = new ORI(project.getURL(), (ori.getPath() == null ? "" : ori.getPath() + "/") + "index.html");
                } else {
                    resp.sendRedirect(req.getRequestURI() + "/");
                    return;
                }
            }

        } catch (Exception e) {

            // Resource not found
            ori = null;
        }

        if (ori != null) {

            try {
                IDataStreams streams = dataManager.load(ori);

                // Minimum Response header information (size and MIME type)
                long size = dataManager.size(ori);
                resp.setContentLength((int) size);
                resp.setContentType(getServletContext().getMimeType(ori.getPath()));

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

    private ORI requestToORI(String uri, String servletPath) {
        String resourcePath = uri.substring(servletPath.length() + 1);
        return new ORI(project.getURL(), (resourcePath.endsWith("/") ? resourcePath.substring(0, resourcePath.length() - 1) : resourcePath));
    }
}
