package org.onexus.query.parser.internal;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.onexus.core.IQueryParser;
import org.onexus.core.query.Filter;
import org.onexus.core.query.OrderBy;
import org.onexus.core.query.Query;
import org.onexus.query.parser.internal.OqlLexer;
import org.onexus.query.parser.internal.OqlParser;

import java.util.List;
import java.util.Map;

public class QueryParser implements IQueryParser {

    @Override
    public Map<String, String> parseDefine(String expression) {
        OqlParser parser = newParser("DEFINE " + expression);

        try {
            parser.defineClause();
        } catch (RecognitionException e) {
            return null;
        }

        return parser.getQuery().getDefine();
    }


    @Override
    public Map<String, List<String>> parserSelect(String expression) {

        OqlParser parser = newParser("SELECT " + expression);
        try {
            parser.selectClause();
        } catch (RecognitionException e) {
            return null;
        }

        return parser.getQuery().getSelect();

    }


    @Override
    public Filter parseWhere(String expression) {

        OqlParser parser = newParser("WHERE " + expression);
        try {
            parser.whereClause();
        } catch (RecognitionException e) {
            return null;
        }

        return parser.getQuery().getWhere();

    }

    @Override
    public List<OrderBy> parseOrder(String expression) {

        OqlParser parser = newParser("ORDER BY " + expression);
        try {
            parser.orderbyClause();
        } catch (RecognitionException e) {
            return null;
        }

        return parser.getQuery().getOrders();

    }

    @Override
    public Query parseQuery(String expression) {

        if (expression == null) {
            return null;
        }

        OqlParser parser = newParser(expression);

        try {
            parser.oql();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }

        return parser.getQuery();
    }

    private OqlParser newParser(String expression) {

        //lexer splits input into tokens
        ANTLRStringStream input = new ANTLRStringStream(expression);
        TokenStream tokens = new CommonTokenStream(new OqlLexer(input));

        //parser generates abstract syntax tree
        OqlParser parser = new OqlParser(tokens);

        return parser;
    }
}
