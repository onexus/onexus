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
package org.onexus.website.api.pages.browser;

import org.onexus.collection.api.query.Filter;
import org.onexus.collection.api.query.Query;
import org.onexus.resource.api.ORI;
import org.onexus.website.api.utils.visible.VisibleRule;
import org.onexus.website.api.widgets.filters.FilterConfig;

import java.io.Serializable;

public interface IFilter extends Serializable {

    String getTitle(Query query);

    ORI getFilteredCollection();

    FilterConfig getFilterConfig();

    boolean isEnable();

    void setEnable(boolean enabled);

    boolean isDeletable();

    void setDeletable(boolean deletable);

    Filter buildFilter(Query query);

    String toUrlParameter(boolean global, ORI parentOri);

    void loadUrlPrameter(String parameter);

    boolean match(VisibleRule rule);
}
