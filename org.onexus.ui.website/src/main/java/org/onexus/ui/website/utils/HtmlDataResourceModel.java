package org.onexus.ui.website.utils;

import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.io.IOUtils;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.utils.ResourceUtils;
import org.onexus.ui.api.OnexusWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlDataResourceModel extends LoadableDetachableModel<String> {

    private static final Logger log = LoggerFactory.getLogger(HtmlDataResourceModel.class);
    @Inject
    private transient IDataManager dataManager;

    private String contentUri;

    public HtmlDataResourceModel(String contentUri) {
        this.contentUri = contentUri;
    }

    @Override
    protected String load() {

        if (contentUri == null) {
            return "";
        }

        try {
            IDataStreams stream = getDataManager().load(contentUri);
            return filterRelativeUrls(IOUtils.toString(stream.iterator().next()), contentUri);
        } catch (Exception e) {
            log.error("Error loading HTML source '" + contentUri + "'", e);
            return "";
        }
    }

    private final static Pattern PATTERN_HREF = Pattern.compile("(<a\\s+href\\s*=\\s*[\"|']?)(.*?)([\"|'])", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN_SRC = Pattern.compile("(src\\s*=\\s*\"?)(.*?)(\")", Pattern.CASE_INSENSITIVE);


    private static String filterRelativeUrls(String html, String contentUri) {
        String projectUrl = "../../data" + Resource.SEPARATOR + Integer.toHexString(ResourceUtils.getProjectURI(contentUri).hashCode()) + Resource.SEPARATOR + ResourceUtils.getResourcePath(contentUri);
        projectUrl = projectUrl.substring(0, projectUrl.lastIndexOf('/')) + '/';

        Matcher mHref = PATTERN_HREF.matcher(html);
        StringBuffer sbHref = new StringBuffer();
        while (mHref.find()) {
            String link = mHref.group(2).trim();

            if (!link.contains("://")) {
                link = projectUrl + link;
                mHref.appendReplacement(sbHref, mHref.group(1) + link + mHref.group(3));
            }
        }
        mHref.appendTail(sbHref);

        Matcher mSrc = PATTERN_SRC.matcher(sbHref);
        StringBuffer sbSrc = new StringBuffer();
        while (mSrc.find()) {
            String link = mSrc.group(2).trim();

            if (!link.contains("://")) {
                link = projectUrl + link;
                mSrc.appendReplacement(sbSrc, mSrc.group(1) + link + mSrc.group(3));
            }
        }
        mSrc.appendTail(sbSrc);

        return sbSrc.toString();
    }

    private IDataManager getDataManager() {
        if (dataManager == null) {
            OnexusWebApplication.inject(this);
        }

        return dataManager;
    }
}
