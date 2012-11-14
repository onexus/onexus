package org.onexus.collection.manager.internal.ws;

import org.onexus.collection.api.*;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;
import org.onexus.collection.api.utils.QueryUtils;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WsServlet extends HttpServlet {

    private IResourceManager resourceManager;
    private ICollectionManager collectionManager;
    private IQueryParser queryParser;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String oqlQuery = req.getParameter("query");
        String filename = req.getParameter("filename");

        try {
            response(resp, oqlQuery, filename);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            BufferedReader reader = new BufferedReader(req.getReader());

            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line + "\n");
                line = reader.readLine();
            }
            reader.close();
            if (sb.length() > 0) {
                response(resp, sb.toString(), null);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Missing query");
            }

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }

    }

    public void response(HttpServletResponse resp, String oqlQuery, String filename) throws ServletException, IOException {

        if (filename != null) {
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        }

        PrintWriter pw = resp.getWriter();

        Query query = queryParser.parseQuery(oqlQuery);

        IEntityTable result = collectionManager.load(query);

        writeHeader(pw, result);

        while (result.next()) {
            writeRow(pw, result);
        }

        pw.close();
        resp.flushBuffer();
    }

    private void writeHeader(PrintWriter response, IEntityTable table) {

        Iterator<Map.Entry<String, List<String>>> selectIt = table.getQuery().getSelect().entrySet().iterator();
        while (selectIt.hasNext()) {
            Map.Entry<String, List<String>> select = selectIt.next();

            ORI collectionUri = QueryUtils.getCollectionOri(table.getQuery(), select.getKey());
            Collection collection = resourceManager.load(Collection.class, collectionUri);

            Iterator<String> fieldId = select.getValue().iterator();
            while (fieldId.hasNext()) {
                Field field = collection.getField(fieldId.next());
                if (field == null) {
                    continue;
                }
                String label = field.getLabel();
                if (label == null) {
                    label = field.getId();
                }
                response.write(label);

                if (fieldId.hasNext() || selectIt.hasNext()) {
                    response.write("\t");
                }
            }

        }


        response.write("\n");
    }

    private static void writeRow(PrintWriter response, IEntityTable table) {

        Iterator<Map.Entry<String, List<String>>> selectIt = table.getQuery().getSelect().entrySet().iterator();
        while (selectIt.hasNext()) {
            Map.Entry<String, List<String>> select = selectIt.next();

            ORI collection = QueryUtils.getCollectionOri(table.getQuery(), select.getKey());
            IEntity entity = table.getEntity(collection);

            Iterator<String> fieldId = select.getValue().iterator();
            while (fieldId.hasNext()) {
                response.write(String.valueOf(entity.get(fieldId.next())));
                if (fieldId.hasNext() || selectIt.hasNext()) {
                    response.write("\t");
                }
            }
        }

        response.write("\n");
    }

    public ICollectionManager getCollectionManager() {
        return collectionManager;
    }

    public void setCollectionManager(ICollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public IQueryParser getQueryParser() {
        return queryParser;
    }

    public void setQueryParser(IQueryParser queryParser) {
        this.queryParser = queryParser;
    }
}
