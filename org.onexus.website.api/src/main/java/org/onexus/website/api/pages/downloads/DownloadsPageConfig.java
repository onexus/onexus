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
package org.onexus.website.api.pages.downloads;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.resource.api.annotations.ResourceImplicitList;
import org.onexus.resource.api.annotations.ResourceRegister;
import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.pages.PageStatus;
import org.onexus.website.api.widgets.WidgetConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ResourceAlias("downloads")
@ResourceRegister({ Download.class, DownloadsPageStatus.class })
public class DownloadsPageConfig extends PageConfig {

    @NotNull @Valid
    @ResourceImplicitList("download")
    private List<Download> downloads = new ArrayList<Download>();

    public DownloadsPageConfig() {
        super();
    }

    public List<Download> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<Download> downloads) {
        this.downloads = downloads;
    }

    @Override
    public List<WidgetConfig> getWidgets() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public PageStatus createEmptyStatus() {
        return new DownloadsPageStatus();
    }

    @Override
    public PageStatus getDefaultStatus() {
        return null;
    }


}
