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
package org.onexus.website.api.widgets.tableviewer.decorators.scale;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.Field;
import org.onexus.collection.api.IEntity;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.ColorUtils;
import org.onexus.website.api.widgets.tableviewer.decorators.scale.scales.IColorScaleHtml;
import org.onexus.website.api.widgets.tableviewer.decorators.utils.FieldDecorator;
import org.onexus.website.api.widgets.tableviewer.formaters.ITextFormater;

import java.awt.*;

public class ColorDecorator extends FieldDecorator {

    private static final Color EMPTY_COLOR = new Color(255, 255, 255);
    private static final String DEFAULT_URL_TITLE = "Click to see more details about this value";

    private boolean showValue = false;
    private Field tooltipField;
    private IColorScaleHtml colorScale;
    private String cssClass;
    private String urlLink;
    private String urlTitle;


    public ColorDecorator(Field valueField, IColorScaleHtml colorScale) {
        this(valueField, colorScale, null);
    }

    public ColorDecorator(Field valueField, IColorScaleHtml colorScale,
                          String cssClass) {
        this(valueField, valueField, colorScale, cssClass, false, null, null, DEFAULT_URL_TITLE);
    }

    public ColorDecorator(Field valueField, IColorScaleHtml colorScale,
                          String cssClass, ITextFormater textFormater) {
        this(valueField, valueField, colorScale, cssClass, false, textFormater, null, DEFAULT_URL_TITLE);
    }

    public ColorDecorator(Field valueField, IColorScaleHtml colorScale,
                          String cssClass, boolean showValue) {
        this(valueField, valueField, colorScale, cssClass, showValue, null, null, DEFAULT_URL_TITLE);
    }

    public ColorDecorator(Field valueField, Field tooltipField,
                          IColorScaleHtml colorScale, String cssClass, boolean showValue,
                          ITextFormater textFormater, String urlLink, String urlTitle) {
        super(valueField, textFormater);
        this.tooltipField = tooltipField;
        this.colorScale = colorScale;
        this.cssClass = cssClass;
        this.showValue = showValue;
        this.urlLink = urlLink;
        this.urlTitle = urlTitle;
    }

    protected String getTooltip(IEntity entity) {

        if (tooltipField == null || entity == null) {
            return "";
        }

        Object value = entity.get(tooltipField.getId());

        if (value == null) {
            return "";
        }

        return value.toString();

    }

    public boolean isShowValue() {
        return showValue;
    }

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
    }

    public String getColor(IEntity entity) {
        return ColorUtils.colorToHexHtml(getRealColor(entity));
    }

    public Color getRealColor(IEntity entity) {

        Object value = getValue(entity);

        if (value == null) {
            return EMPTY_COLOR;
        }

        return colorScale.valueColor(value);
    }


    @Override
    public void populateCell(WebMarkupContainer cellContainer,
                             String componentId, IModel<IEntity> entityModel) {

        IEntity rowEntity = entityModel.getObject();
        Color bkgColor = getRealColor(rowEntity);

        if (!showValue) {
            WebMarkupContainer empty = new WebMarkupContainer(componentId);
            empty.setVisible(false);
            cellContainer.add(empty);
        } else {
            String value = getFormatValue(rowEntity);
            boolean black = lum(bkgColor) >= 128;
            String styleCss = black ? "color: #000;" : "color: #FFF;";
            if (urlLink != null && !StringUtils.isEmpty(value)) {
                String url = fixLinkUrl(urlLink);
                url = replaceParameters(getField(), value, rowEntity, url, false);
                String link = "<a href=\"" + url + "\" rel=\"tooltip\" title=\"" + urlTitle + "\"><i class=\"icon-zoom-in" + (black ? "" : " icon-white") + "\"></i></a>&nbsp;";
                value = link + value;
            }
            Label textValue = new Label(componentId, value);
            textValue.setEscapeModelStrings(false);
            textValue.add(new AttributeModifier("style", new Model<String>(styleCss)));
            cellContainer.add(textValue);
        }

        cellContainer
                .add(new AttributeModifier("style", new Model<String>(
                        "background-color: " + ColorUtils.colorToHexHtml(bkgColor) + ";")));
        cellContainer.add(new AttributeModifier("title", new Model<String>(
                getTooltip(rowEntity))));
        if (cssClass != null) {
            cellContainer.add(new AttributeModifier("class", new Model<String>(
                    cssClass)));
        }

    }

    public static double lum(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return .299 * r + .587 * g + .114 * b;
    }


}
