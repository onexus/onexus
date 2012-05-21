package org.onexus.query.parser.internal;

import org.junit.Test;
import org.onexus.core.IQueryParser;
import org.onexus.core.query.Query;

import static org.junit.Assert.assertEquals;



public class QueryParserTest {

    private static IQueryParser queryParser = new QueryParser();

    @Test
    public void testUriWithPort() {

        String inOql = "DEFINE c='http://localhost:8181/onexus/onx/test' SELECT c ('field') FROM c";

        Query query = queryParser.parseQuery(inOql);

        String outOql = query.toString(new StringBuilder(), false).toString();

        assertEquals(inOql, outOql);

    }

    @Test
    public void testCasesensitive() throws Exception {

        String inOql = "define c='http://www.onexus.org/onx/select' SELECT c ('define') from c";


        Query query = queryParser.parseQuery(inOql);

        String outOql = query.toString(new StringBuilder(), false).toString();

        assertEquals("DEFINE c='http://www.onexus.org/onx/select' SELECT c ('define') FROM c", outOql);

    }
}

