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
package org.onexus.website.api.widgets.download;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.website.api.widgets.WidgetConfig;

@XStreamAlias("widget-download")
public class DownloadWidgetConfig extends WidgetConfig {

    private DownloadWidgetStatus defaultStatus;

    public DownloadWidgetConfig(String id) {
        super(id);
    }

    public DownloadWidgetConfig() {
        super();
    }

    @Override
    public DownloadWidgetStatus createEmptyStatus() {
        return new DownloadWidgetStatus(getId());
    }

    public DownloadWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(DownloadWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
