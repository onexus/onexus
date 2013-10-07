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
package org.onexus.website.api.pages;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.StringValue;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.WebsiteConfig;
import org.onexus.website.api.events.EventPanel;
import org.onexus.website.api.utils.CustomCssBehavior;

public abstract class Page<C extends PageConfig, S extends PageStatus> extends EventPanel {

    public Page(String componentId, IModel<S> pageModel) {
        super(componentId, pageModel);

        PageConfig config = getConfig();
        String css = config.getCss();


        WebsiteConfig websiteConfig = config.getWebsiteConfig();
        ORI parentUri = (websiteConfig != null) ? websiteConfig.getORI().getParent() : null;
        add(new CustomCssBehavior(parentUri, css));
    }

    public IModel<S> getModel() {
        return (IModel<S>) getDefaultModel();
    }

    public S getStatus() {
        return (S) getDefaultModelObject();
    }

    public C getConfig() {
        S status = getStatus();
        return status == null ? null : (C) status.getConfig();
    }

    protected boolean isEmbed() {
        StringValue embed = getPage().getPageParameters().get("embed");
        return embed.toBoolean(false);
    }

}
