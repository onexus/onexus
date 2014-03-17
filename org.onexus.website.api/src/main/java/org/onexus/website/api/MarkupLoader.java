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
package org.onexus.website.api;

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.*;
import org.apache.wicket.util.resource.IResourceStream;
import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.resource.api.IResourceListener;
import org.onexus.resource.api.IResourceManager;
import org.onexus.resource.api.ORI;
import org.onexus.resource.api.Resource;
import org.onexus.resource.api.exceptions.ResourceNotFoundException;
import org.onexus.resource.api.utils.ResourceListener;

import javax.inject.Inject;

public class MarkupLoader {

    private static final String NAMESPACE = "onexus";
    private static final IMarkupResourceStreamProvider defaultMarkupResourceProvider = new DefaultMarkupResourceStreamProvider();

    @Inject
    private IDataManager dataManager;

    @Inject
    private IResourceManager resourceManager;

    private ORI markupOri;
    private MarkupResourceStream markupResourceStream;
    private IResourceListener listener;

    public MarkupLoader(ORI parentOri, String markup) {
        super();

        WebsiteApplication.inject(this);

        if (markup != null) {

            markupOri = new ORI(markup);
            if (!markupOri.isAbsolute()) {
                markupOri = markupOri.toAbsolute(parentOri);
            }

        }

        listener = new RemoveMarkupCache(Application.get().getMarkupSettings().getMarkupFactory().getMarkupCache());
        resourceManager.addResourceListener(listener);

    }

    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {

        if (markupOri != null) {
            try {

                IDataStreams dataStreams = dataManager.load(markupOri);

                if (dataStreams != null) {

                    IResourceStream resourceStream = new DataResourceStream(dataStreams);
                    markupResourceStream = new MarkupResourceStream(resourceStream, new ContainerInfo(container), containerClass);
                    markupResourceStream.setWicketNamespace(NAMESPACE);

                    return markupResourceStream;
                }
            } catch (ResourceNotFoundException e) {
                // If the resource is not found, then load default markup.
            }
        }

        IResourceStream stream = defaultMarkupResourceProvider.getMarkupResourceStream(container, containerClass);

        if (stream instanceof MarkupResourceStream) {
            markupResourceStream = (MarkupResourceStream) stream;
        }

        return stream;
    }

    private class RemoveMarkupCache extends ResourceListener {

        private IMarkupCache cache;

        private RemoveMarkupCache(IMarkupCache cache) {
            this.cache = cache;
        }

        @Override
        public void onResourceChange(Resource resource) {
            if (markupResourceStream != null && markupOri != null && markupOri.equals(resource.getORI())) {
                cache.removeMarkup(markupResourceStream.getCacheKey());
                markupOri = null;
                markupResourceStream = null;
            }
        }
    }


}
