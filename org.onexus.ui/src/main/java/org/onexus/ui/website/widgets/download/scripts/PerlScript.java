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

    @Override
    public String getPlugin() {
        return "perl";
    }

}
