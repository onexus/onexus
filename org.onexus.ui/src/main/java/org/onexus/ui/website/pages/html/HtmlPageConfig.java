/**
 *  Copyright 2011 Universitat Pompeu Fabra.
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
package org.onexus.ui.website.pages.html;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.ui.website.pages.PageConfig;
import org.onexus.ui.website.pages.PageStatus;
import org.onexus.ui.website.pages.browser.TabConfig;
import org.onexus.ui.website.utils.reflection.ListComposer;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XStreamAlias("html")
public class HtmlPageConfig extends PageConfig {
    
    private String content;
    private String css;
    
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
    public List<WidgetConfig> getWidgetConfigs() {
        return Collections.EMPTY_LIST;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
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
