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
package org.onexus.website.api.utils;

import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Project;
import org.onexus.website.api.WebsiteApplication;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlDataResourceModel extends LoadableDetachableModel<String> {

    private static final Logger log = LoggerFactory.getLogger(HtmlDataResourceModel.class);

    @PaxWicketBean(name = "dataManager")
    private IDataManager dataManager;

    @PaxWicketBean(name = "resourceManager")
    private IResourceManager resourceManager;

    private ORI contentUri;
    private String dataResourceUrl;

    public HtmlDataResourceModel(ORI parentUri, String htmlTag) {
        if (!Strings.isEmpty(htmlTag)) {
            contentUri = new ORI(htmlTag);
            if (!contentUri.isAbsolute()) {
                contentUri = contentUri.toAbsolute(parentUri);
            }

            String dataService = getDataManager().getMount();
            Project project = getResourceManager().getProject(contentUri.getProjectUrl());
            this.dataResourceUrl = WebsiteApplication.toAbsolutePath('/' + dataService + '/' + project.getName());
        }
    }

    @Override
    protected String load() {

        if (contentUri == null || Strings.isEmpty(contentUri.getPath())) {
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

    private IResourceManager getResourceManager() {

        if (resourceManager == null) {
            WebsiteApplication.inject(this);
        }

        return resourceManager;
    }

    private final static Pattern PATTERN_HREF = Pattern.compile("(<a\\s+href\\s*=\\s*[\"|']?)(.*?)([\"|'])", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN_SRC = Pattern.compile("(src\\s*=\\s*\"?)(.*?)(\")", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN_ANTICACHE = Pattern.compile("\\$\\{anticache\\}", Pattern.CASE_INSENSITIVE);


    private String filterRelativeUrls(String html) {

        // Replace anticache token
        html = PATTERN_ANTICACHE.matcher(html).replaceAll(Long.toHexString(System.currentTimeMillis()));

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
