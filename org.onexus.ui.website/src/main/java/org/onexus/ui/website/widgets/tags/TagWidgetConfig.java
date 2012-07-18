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
package org.onexus.ui.website.widgets.tags;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.onexus.ui.website.widgets.WidgetConfig;

import java.util.List;

@XStreamAlias("widget-tags")
public class TagWidgetConfig extends WidgetConfig {

    private TagWidgetStatus defaultStatus;

    @XStreamImplicit(itemFieldName = "tag")
    private List<String> tags;

    public TagWidgetConfig() {
        super();
    }

    public TagWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(TagWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public TagWidgetStatus createEmptyStatus() {
        return new TagWidgetStatus(getId());
    }

}
