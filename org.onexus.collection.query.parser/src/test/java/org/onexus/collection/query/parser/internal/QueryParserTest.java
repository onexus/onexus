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
package org.onexus.collection.query.parser.internal;

import org.junit.Test;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;

import static org.junit.Assert.assertEquals;



public class QueryParserTest {

    private static IQueryParser queryParser = new QueryParser();

    @Test
    public void testUriWithPort() {

        String inOql = "DEFINE c='http://localhost:8181/onexus/onx/test' SELECT c (field) FROM c";

        Query query = queryParser.parseQuery(inOql);

        String outOql = query.toString(new StringBuilder(), false).toString();

        assertEquals(inOql, outOql);

    }

    @Test
    public void testCasesensitive() throws Exception {

        String inOql = "DEFINE c='http://www.onexus.org/onx/select' SELECT c (define, diseaseId) FROM c";

        Query query = queryParser.parseQuery(inOql);

        String outOql = query.toString(new StringBuilder(), false).toString();

        assertEquals("DEFINE c='http://www.onexus.org/onx/select' SELECT c (define, diseaseId) FROM c", outOql);

    }
}

