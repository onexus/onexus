package org.onexus.core;

import org.onexus.core.query.Filter;
import org.onexus.core.query.OrderBy;
import org.onexus.core.query.Query;

import java.util.List;
import java.util.Map;

public interface IQueryParser {

    Query parseQuery(String expression);

    Map<String, String> parseDefine(String expression);

    Map<String, List<String>> parserSelect(String expression);

    Filter parseWhere(String expression);

    List<OrderBy> parseOrder(String expression);

}
