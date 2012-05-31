package org.onexus.ui.ws.response;

import org.apache.wicket.request.Response;
import org.onexus.core.ICollectionManager;
import org.onexus.core.IEntity;
import org.onexus.core.IEntityTable;
import org.onexus.core.IQueryParser;
import org.onexus.core.query.Query;
import org.onexus.core.utils.QueryUtils;
import org.onexus.core.utils.ResourceUtils;

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
