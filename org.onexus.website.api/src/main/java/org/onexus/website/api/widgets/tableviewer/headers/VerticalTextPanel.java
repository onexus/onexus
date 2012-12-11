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
package org.onexus.website.api.widgets.tableviewer.headers;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;

import java.awt.*;

public class VerticalTextPanel extends Panel {

    private VerticalTextPanel(String id, VerticalTextResource resource) {
        super(id);
        Image image = new Image("image", resource);
        add(image);
    }

    public VerticalTextPanel(String id, String text, int width, int height,
                             Font font) {
        this(id, new VerticalTextResource(text, width, height, font));
    }

    public VerticalTextPanel(String id, String text, int width, int height) {
        this(id, new VerticalTextResource(text, width, height));
    }

    public VerticalTextPanel(String id, String text) {
        this(id, new VerticalTextResource(text));
    }

    public VerticalTextPanel(String id, String text, Font font) {
        this(id, new VerticalTextResource(text, font));
    }

}
