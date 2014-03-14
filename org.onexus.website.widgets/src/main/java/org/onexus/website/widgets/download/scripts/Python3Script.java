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
package org.onexus.website.widgets.download.scripts;

public class Python3Script implements IQueryScript {
    @Override
    public String getContent(String query, CharSequence url) {
        return
                "import urllib.request, urllib.error, urllib.parse\n" +
                        "\n" +
                        "query=\"\"\"\n" + query + "\"\"\"\n" +
                        "\n" +
                        "binary_data = query.encode(\"utf-8\")\n" +
                        "req = urllib.request.Request(\"" + url + "\")\n" +
                        "res = urllib.request.urlopen(req, binary_data)\n" +
                        "print(res.read().decode('utf-8'))";
    }

    @Override
    public String getLabel() {
        return "Python 3";
    }


}
