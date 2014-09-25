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
package org.onexus.collection.store.elasticsearch.internal.ws;

import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.onexus.collection.store.elasticsearch.internal.ElasticSearchCollectionStore;
import org.onexus.resource.api.session.LoginContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class EsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsServlet.class);

    private ElasticSearchCollectionStore collectionStore;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        initLoginContext(req);

        String uri[] = parseURI(req);
        String query = req.getParameter("q");

        try {
            response(resp, uri, query);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        initLoginContext(req);

        String uri[] = parseURI(req);

        try {
            BufferedReader reader = new BufferedReader(req.getReader());

            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = reader.readLine();
            }
            reader.close();
            if (sb.length() > 0) {
                response(resp, uri, sb.toString());
            } else {

                String query = req.getParameter("q");

                if (query == null) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing query");
                } else {
                    response(resp, uri, query);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Query service", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    private String[] parseURI(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String servletPath = req.getServletPath();

        return uri.substring(servletPath.length() + 1).split("/");
    }

    public void response(HttpServletResponse resp, String[] uri, String body) throws Exception {

        String method = uri[uri.length - 1];

        ToXContent response = null;

        if ("_search".equals(method)) {
            response = search(resp, body, uri[0]);
        } else if ("_msearch".equals(method)) {
            response = msearch(resp, body);
        }

        if (response != null) {

            // Output the response
            OutputStream out = resp.getOutputStream();
            XContentBuilder builder = XContentFactory.jsonBuilder(out);
            resp.setContentType(builder.contentType().restContentType());
            builder.startObject();
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();

            // Close and flush the buffers.
            builder.close();
            out.close();
            resp.flushBuffer();
        } else {
            throw new UnsupportedOperationException(method);
        }

    }

    private ToXContent msearch(HttpServletResponse resp, String body) throws Exception {

        // Load search request
        MultiSearchRequest searchRequest = new MultiSearchRequest();
        searchRequest.add(new BytesArray(body), false,  null, null, null, null);

        // Execute the search
        return collectionStore.getClient().multiSearch(searchRequest).actionGet();

    }


    private ToXContent search(HttpServletResponse resp, String query, String index) throws IOException {

        // Load search request
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(query);

        // Return the response
        return collectionStore.getClient().search(searchRequest).actionGet();
    }


    public ElasticSearchCollectionStore getCollectionStore() {
        return collectionStore;
    }

    public void setCollectionStore(ElasticSearchCollectionStore collectionStore) {
        this.collectionStore = collectionStore;
    }

    private void initLoginContext(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session != null && LoginContext.get(session.getId()) != null) {
            LoginContext ctx = LoginContext.get(session.getId());
            LoginContext.set(ctx, null);
        } else {
            LoginContext.set(LoginContext.ANONYMOUS_CONTEXT, null);
        }
    }
}
