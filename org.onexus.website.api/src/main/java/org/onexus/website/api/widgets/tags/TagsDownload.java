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
package org.onexus.website.api.widgets.tags;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.IResourceStream;

public abstract class TagsDownload extends AbstractAjaxBehavior {

    private boolean addAntiCache;

    public TagsDownload() {
        this(true);
    }

    public TagsDownload(boolean addAntiCache) {
        super();
        this.addAntiCache = addAntiCache;
    }

    /**
     * Call this method to initiate the download.
     */
    public void initiate(AjaxRequestTarget target) {
        String url = getCallbackUrl().toString();

        if (addAntiCache) {
            url = url + (url.contains("?") ? "&" : "?");
            url = url + "antiCache=" + System.currentTimeMillis();
        }

        target.appendJavaScript("window.location.href='" + url + "'");
    }

    public void onRequest() {
        getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(
                new ResourceStreamRequestHandler(getResourceStream(), getFileName()));
    }

    /**
     * Override this method for a file name which will let the browser prompt with a save/open dialog.
     */
    protected abstract String getFileName();

    /**
     * Hook method providing the actual resource stream.
     */
    protected abstract IResourceStream getResourceStream();
}