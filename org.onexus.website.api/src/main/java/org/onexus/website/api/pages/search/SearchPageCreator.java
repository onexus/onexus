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
package org.onexus.website.api.pages.search;

import org.apache.wicket.model.IModel;
import org.onexus.resource.api.IResourceRegister;
import org.onexus.website.api.pages.AbstractPageCreator;
import org.onexus.website.api.pages.Page;

public class SearchPageCreator extends AbstractPageCreator<SearchPageConfig, SearchPageStatus> {

    public SearchPageCreator() {
        super(SearchPageConfig.class, "search", "Search page");
    }

    @Override
    protected Page<?, ?> build(String componentId, IModel<SearchPageStatus> statusModel) {
        return new SearchPage(componentId, statusModel);
    }

    @Override
    public void register(IResourceRegister resourceRegister) {
        super.register(resourceRegister);

       resourceRegister.register(SearchPageStatus.class);
       resourceRegister.register(SearchPageStatus.class);
       resourceRegister.register(SearchType.class);
       resourceRegister.register(SearchLink.class);
    }
}
