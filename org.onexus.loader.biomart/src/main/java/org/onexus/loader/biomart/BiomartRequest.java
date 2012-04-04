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
package org.onexus.loader.biomart;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.core.resources.Loader;

public class BiomartRequest {

    private Collection collection;
    private String martService;
    private StringBuilder query;

    public BiomartRequest(Collection collection, String defaultMartService,
                          String defaultVirtualSchema) {

        this.collection = collection;

        Loader task = collection.getLoader();
        String parameterMartService = task.getParameter("MART_SERVICE");
        String parameterVirtualSchema = task.getParameter("VIRTUAL_SCHEMA");
        String parameterDataset = task.getParameter("DATASET");
        String parameterQuery = task.getParameter("QUERY");

        this.martService = (parameterMartService == null ? defaultMartService : parameterMartService);

        this.query = new StringBuilder();
        this.query.append("query=");

        if (parameterQuery != null) {
            this.query.append(parameterQuery);
        } else {

            String virtualSchema = (parameterVirtualSchema == null ? defaultVirtualSchema : parameterVirtualSchema);
            String dataset = (parameterDataset == null ? collection.getId() : parameterDataset);

            this.query.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            this.query.append("<!DOCTYPE Query>");
            this.query.append("<Query  virtualSchemaName = \"").append(virtualSchema).append("\" formatter=\"TSV\" header = \"0\" uniqueRows = \"0\" count = \"\" datasetConfigVersion = \"0.6\" >");
            this.query.append("<Dataset name = \"").append(dataset).append("\" interface = \"default\" >");

            for (Field field : collection.getFields()) {
                this.query.append("<Attribute name = \"").append(field.getId()).append("\" />");
            }

            this.query.append("</Dataset>");
            this.query.append("</Query>");
        }

    }

    public Collection getCollection() {
        return collection;
    }

    public String getMartService() {
        return martService;
    }

    public String getQuery() {
        return query.toString();
    }


}
