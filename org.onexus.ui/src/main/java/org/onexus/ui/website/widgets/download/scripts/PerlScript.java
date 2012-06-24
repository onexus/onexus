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
package org.onexus.ui.website.widgets.download.scripts;

public class PerlScript implements IQueryScript {
    @Override
    public String getContent(String query, CharSequence url) {
        return
                "use LWP::UserAgent;\n" +
                        "use HTTP::Request;\n" +
                        "\n" +
                        "my $url = \"" + url + "\";\n" +
                        "my $query = \"" + query + "\"\n" +
                        "\n" +
                        "my $ua = LWP::UserAgent->new;\n" +
                        "my $req = HTTP::Request->new(POST => $url);\n" +
                        "$req->content_type('application/x-www-form-urlencoded');\n" +
                        "$req->content($query);\n" +
                        "\n" +
                        "my $response = $ua->request($req);\n" +
                        "my $content = $response->content();\n" +
                        "\n" +
                        "print $content;\n";
    }

    @Override
    public String getLabel() {
        return "perl";
    }



}
