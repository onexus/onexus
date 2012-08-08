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
package org.onexus.ui.api.ws.response;

import org.apache.wicket.request.Response;
import org.onexus.collection.api.ICollectionManager;
import org.onexus.collection.api.IEntity;
import org.onexus.collection.api.IEntityTable;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.utils.ResourceUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QueryResponse extends AbstractResponse {

    @Inject
    public IQueryParser queryParser;

    @Inject
    public ICollectionManager collectionManager;

    private String query;
    private boolean count;
    private String format;
    private boolean prettyPrint;
    private String fileName;

    public QueryResponse(String query, Boolean count, String format, Boolean prettyPrint, String fileName) {
        super();

        this.query = query;
        this.count = (count == null) ? false : count;
        this.format = (format == null) ? "tsv" : format;
        this.prettyPrint = (prettyPrint == null) ? false : prettyPrint;
        this.fileName = (fileName == null) ? "query-" + Integer.toHexString(query.hashCode()) + ".tsv" : fileName;

        setContentType("text/tab-separated-values");
        setFileName(fileName);
    }

    public QueryResponse(String url, String select, String where, String orderBy, String limit, Boolean count, String format, Boolean prettyPrint) {
        this(buildQuery(url, select, where, orderBy, limit), count, format, prettyPrint, ResourceUtils.getResourceName(url) + ".tsv");


    }


    @Override
    protected void writeData(Response response) {

        Query query = queryParser.parseQuery(this.query);

        IEntityTable result = collectionManager.load(query);

        if (count) {

            long size = result.size();

            response.write(Long.toString(size));
        } else {

            writeHeader(response, result);

            while (result.next()) {
                writeRow(response, result);
            }
        }

    }

    private static void writeHeader(Response response, IEntityTable table) {

        for (Map.Entry<String, List<String>> select : table.getQuery().getSelect().entrySet()) {

            for (String field : select.getValue()) {
                response.write(select.getKey());
                response.write(".");
                response.write(field);
                response.write("\t");
            }

        }

        response.write("\n");
    }

    private static void writeRow(Response response, IEntityTable table) {

        for (Map.Entry<String, List<String>> select : table.getQuery().getSelect().entrySet()) {

            String collection = QueryUtils.getCollectionUri(table.getQuery(), select.getKey());
            IEntity entity = table.getEntity(collection);

            for (String field : select.getValue()) {
                response.write(String.valueOf(entity.get(field)));
                response.write("\t");
            }
        }

        response.write("\n");
    }

    private static String buildQuery(String url, String select, String where, String orderBy, String limit) {

        StringBuilder query = new StringBuilder();

        query.append("DEFINE c='").append(url);

        Iterator<String> fields = Arrays.asList(select.split(",")).iterator();

        query.append("' SELECT c (");

        while (fields.hasNext()) {
            String field = fields.next();

            if (field.startsWith("'")) {
                query.append(field);
            } else {
                query.append(Query.escapeString(field));
            }

            if (fields.hasNext()) {
                query.append(",");
            }
        }

        query.append(")");
        query.append(" FROM c ");

        if (where != null) {
            //TODO
        }

        if (orderBy != null) {
            //TODO
        }

        if (limit != null) {
            query.append(" LIMIT ").append(limit);
        }

        return query.toString();
    }

}