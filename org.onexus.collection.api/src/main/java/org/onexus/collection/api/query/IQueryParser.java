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
package org.onexus.collection.api.query;

import org.onexus.resource.api.ORI;

import java.util.List;
import java.util.Map;

public interface IQueryParser {

    Query parseQuery(String expression);

    Map<String, ORI> parseDefine(String expression);

    Map<String, List<String>> parserSelect(String expression);

    Filter parseWhere(String expression);

    List<OrderBy> parseOrder(String expression);

}
