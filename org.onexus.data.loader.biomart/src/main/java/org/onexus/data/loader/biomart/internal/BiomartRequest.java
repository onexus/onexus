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
package org.onexus.data.loader.biomart.internal;

import org.onexus.data.api.Data;
import org.onexus.resource.api.Loader;
import org.onexus.resource.api.Resource;

public class BiomartRequest {

    private Data data;
    private String martService;
    private StringBuilder query;

    public BiomartRequest(Data data, String defaultMartService) {

        this.data = data;

        Loader task = this.data.getLoader();
        String parameterMartService = task.getParameter("MART_SERVICE");
        String parameterQuery = task.getParameter("QUERY");
        this.martService = (parameterMartService == null ? defaultMartService : parameterMartService);

        this.query = new StringBuilder();
        this.query.append("query=");
        this.query.append(parameterQuery);
    }

    public Resource getData() {
        return data;
    }

    public String getMartService() {
        return martService;
    }

    public String getQuery() {
        return query.toString();
    }


}
