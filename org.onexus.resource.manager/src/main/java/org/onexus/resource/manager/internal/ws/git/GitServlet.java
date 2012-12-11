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
