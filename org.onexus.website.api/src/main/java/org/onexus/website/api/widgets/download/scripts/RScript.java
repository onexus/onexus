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
package org.onexus.website.api.widgets.download.scripts;

public class RScript implements IQueryScript {
    @Override
    public String getContent(String query, CharSequence url) {
        return
                "require(RCurl)\n" +
                "\n" +
                "url <- \""+ url +"\"\n" +
                "oql <- \""+ query + "\" \n" +
                "\n" +
                "dataTsv <- httpPUT(url, oql)\n" +
                "data <- read.csv(textConnection(dataTsv), header = TRUE, sep=\"\\t\")" ;
    }

    @Override
    public String getLabel() {
        return "R";
    }


}
