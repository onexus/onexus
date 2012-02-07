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
package org.onexus.ui.website.widgets.bookmark;

import org.apache.wicket.model.IModel;
import org.onexus.ui.website.widgets.AbstractWidgetCreator;
import org.onexus.ui.website.widgets.Widget;

public class BookmarkWidgetCreator extends AbstractWidgetCreator<BookmarkWidgetConfig, BookmarkWidgetStatus> {
    
    public BookmarkWidgetCreator() {
	super(BookmarkWidgetConfig.class, "bookmark-widget", "Create linkable bookmarks.");
    }

    @Override
    protected Widget<?,?> build(String componentId, BookmarkWidgetConfig config, IModel<BookmarkWidgetStatus> statusModel) {
	return new BookmarkWidget(componentId, config, statusModel);
    }

}
