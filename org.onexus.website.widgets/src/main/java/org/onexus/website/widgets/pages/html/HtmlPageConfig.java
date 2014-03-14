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
package org.onexus.website.widgets.pages.html;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.widgets.WidgetConfig;

import java.util.Collections;
import java.util.List;

@ResourceAlias("html")
public class HtmlPageConfig extends PageConfig {

    private String content;

    public HtmlPageConfig() {
        super();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public List<WidgetConfig> getWidgets() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public PageStatus createEmptyStatus() {
        return new HtmlPageStatus();
    }

    @Override
    public PageStatus getDefaultStatus() {
        return null;
    }


}
