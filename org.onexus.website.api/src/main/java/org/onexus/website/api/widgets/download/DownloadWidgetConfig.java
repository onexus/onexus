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

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.widgets.WidgetConfig;

import javax.validation.Valid;

@ResourceAlias("widget-download")
public class DownloadWidgetConfig extends WidgetConfig {

    @Valid
    private DownloadWidgetStatus defaultStatus;

    private String formats;

    private String scripts;

    public DownloadWidgetConfig(String id) {
        super(id);
    }

    public DownloadWidgetConfig() {
        super();
    }

    public String getFormats() {
        return formats;
    }

    public void setFormats(String formats) {
        this.formats = formats;
    }

    public String getScripts() {
        return scripts;
    }

    public void setScripts(String scripts) {
        this.scripts = scripts;
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
