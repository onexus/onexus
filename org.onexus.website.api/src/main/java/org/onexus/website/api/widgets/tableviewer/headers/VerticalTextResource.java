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

import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.caching.IResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VerticalTextResource extends DynamicImageResource {

    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 12);
    private static final IResourceCachingStrategy CACHING_STRATEGY = new NoOpResourceCachingStrategy();

    private String text;
    private int width;
    private int height;
    private Font font;

    public VerticalTextResource(String text) {
        this(text, DEFAULT_FONT);


    }

    public VerticalTextResource(String text, Font font) {
        this(text, font.getSize() + 3, (int) (font.getSize() * 0.75 * text
                .length()), font);
    }

    public VerticalTextResource(String text, int width, int height) {
        this(text, width, height, DEFAULT_FONT);
    }

    public VerticalTextResource(String text, int width, int height, Font font) {
        super();
        this.text = text;
        this.width = width;
        this.height = height;
        this.font = font;
    }

    @Override
    protected byte[] getImageData(Attributes attributes) {
        final BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, 500, 500);
        g2.setFont(font);
        g2.setColor(Color.BLACK);
        g2.translate((width + font.getSize()) / 2, height - 2);
        g2.rotate(-Math.PI / 2.0);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(text, 0, 0);
        g2.dispose();

        return toImageData(image);
    }

    @Override
    protected IResourceCachingStrategy getCachingStrategy() {
        return CACHING_STRATEGY;
    }


}
