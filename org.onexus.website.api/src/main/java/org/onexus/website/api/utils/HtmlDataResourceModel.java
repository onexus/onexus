package org.onexus.website.api.utils;

import org.apache.wicket.Component;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.io.IOUtils;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteApplication;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlDataResourceModel extends LoadableDetachableModel<String> {

    private static final Logger log = LoggerFactory.getLogger(HtmlDataResourceModel.class);

    @PaxWicketBean(name="dataManager")
    private IDataManager dataManager;

    private ORI contentUri;
    private String dataResourceUrl;

    public HtmlDataResourceModel(ORI contentUri, Component component) {
        this.contentUri = contentUri;
        CharSequence urlDataResource = component.urlFor(DataResource.getResourceReference(), null);
        this.dataResourceUrl = urlDataResource.toString();
    }



    @Override
    protected String load() {

        if (contentUri == null) {
            return "";
        }

        try {
            IDataStreams stream = getDataManager().load(contentUri);
            return filterRelativeUrls(IOUtils.toString(stream.iterator().next()));
        } catch (Exception e) {
            log.error("Error loading HTML source '" + contentUri + "'");
            return "";
        }
    }

    private IDataManager getDataManager() {
        if (dataManager == null) {
            WebsiteApplication.inject(this);
        }

        return dataManager;
    }

    private final static Pattern PATTERN_HREF = Pattern.compile("(<a\\s+href\\s*=\\s*[\"|']?)(.*?)([\"|'])", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN_SRC = Pattern.compile("(src\\s*=\\s*\"?)(.*?)(\")", Pattern.CASE_INSENSITIVE);


    private String filterRelativeUrls(String html) {

        String resourcePath = contentUri.getPath();
        resourcePath = resourcePath.substring(1, resourcePath.lastIndexOf('/'));

        Matcher mHref = PATTERN_HREF.matcher(html);
        StringBuffer sbHref = new StringBuffer();
        while (mHref.find()) {
            String link = mHref.group(2).trim();

            if (!link.contains("://") && !link.startsWith("#")) {
                if (!link.startsWith("/")) {
                    link = dataResourceUrl + '/' + resourcePath + '/' + link;
                } else {
                    link = dataResourceUrl + '/' + link;
                }
                mHref.appendReplacement(sbHref, mHref.group(1) + link + mHref.group(3));
            }
        }
        mHref.appendTail(sbHref);

        Matcher mSrc = PATTERN_SRC.matcher(sbHref);
        StringBuffer sbSrc = new StringBuffer();
        while (mSrc.find()) {
            String link = mSrc.group(2).trim();

            if (!link.contains("://")) {
                if (!link.startsWith("/")) {
                    link = dataResourceUrl + '/' + resourcePath + '/' + link;
                } else {
                    link = dataResourceUrl + '/' + link;
                }

                mSrc.appendReplacement(sbSrc, mSrc.group(1) + link + mSrc.group(3));
            }
        }
        mSrc.appendTail(sbSrc);

        return sbSrc.toString();
    }
}
