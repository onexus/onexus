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
package org.onexus.ui.website.decorators;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.onexus.core.resources.Collection;
import org.onexus.core.resources.Field;
import org.onexus.ui.website.decorators.color.BinaryColorScale;
import org.onexus.ui.website.decorators.color.IColorScaleHtml;
import org.onexus.ui.website.decorators.color.LinearColorScale;
import org.onexus.ui.website.decorators.color.PValueColorScale;
import org.onexus.ui.website.formaters.DoubleFormater;
import org.onexus.ui.website.formaters.PValueFormater;

/**
 * Application > Project > Browser > User preferences
 * 
 * @author jordi
 * 
 */
public class DecoratorFactory {

    private final static String annotationKey = "BROWSER_DECORATOR";
    private final static String cssClass = "st";
    private final static IColorScaleHtml binaryScale = new BinaryColorScale(
	    0.0, 1.0, 1.0, Color.LIGHT_GRAY, Color.RED, Color.WHITE);
    private final static IColorScaleHtml pvalueScale = new PValueColorScale();
    private final static IColorScaleHtml rankScale = new LinearColorScale(0.0,
	    1.0, new Color(255, 255, 255), new Color(0, 255, 0));
    private final static IColorScaleHtml qualityScale = new LinearColorScale(
	    0.0, 2500.0, new Color(255, 255, 255), new Color(0, 255, 0));
    private final static IColorScaleHtml depthScale = new LinearColorScale(
	    10.0, 100.0, new Color(255, 255, 255), new Color(0, 255, 0));

    private final static Map<String, IDecoratorCreator> creators = new HashMap<String, IDecoratorCreator>();

    public static IDecorator getDecorator(Collection collection, Field field) {
	return getDecorator(null, collection, field);
    }

    public static IDecorator getDecorator(String decoratorId,
	    Collection collection, Field field) {

	if (decoratorId != null && creators.containsKey(decoratorId)) {
	    IDecoratorCreator creator = creators.get(decoratorId);
	    if (creator != null) {
		return creator.createDecorator(collection, field);
	    }
	}

	if (decoratorId == null && field != null) {
	    decoratorId = field.getProperty(annotationKey);
	}

	if (decoratorId == null && field != null || decoratorId != null
		&& decoratorId.equals("") && field != null) {

	    if (field.getDataType().equals(Double.class)) {
		return new FieldDecorator(field, new DoubleFormater(3),
			cssClass);
	    }

	    if (field.getDataType().equals(Integer.class)) {
		return new FieldDecorator(field, cssClass);
	    }
	    if (field.getDataType().equals(Long.class)) {
		return new FieldDecorator(field, cssClass);
	    }

	    if (field.getDataType().equals(String.class)) {
		return new FieldDecorator(field, cssClass);
	    }

	} else if (decoratorId != null && field != null) {

	    if (decoratorId.equalsIgnoreCase("LINK")) {
		return new LinkDecorator(collection.getURI(), field);
	    }

	    if (decoratorId.equalsIgnoreCase("LINK-GOTO")) {
		return new LinkGotoDecorator(collection.getURI(), field);
	    }

	    if (decoratorId.equalsIgnoreCase("PVALUE")) {
		return new ColorDecorator(field, pvalueScale, cssClass, PValueFormater.INSTANCE);
	    }
	    
	    if (decoratorId.equalsIgnoreCase("PVALUE2")) {
		return new ColorDecorator(field, field, pvalueScale, cssClass, true, PValueFormater.INSTANCE);
	    }

	    if (decoratorId.equalsIgnoreCase("RANK")) {
		return new ColorDecorator(field, rankScale, cssClass, true);
	    }

	    if (decoratorId.equalsIgnoreCase("BINARY")) {
		return new ColorDecorator(field, binaryScale, cssClass);
	    }

	    if (decoratorId.equalsIgnoreCase("CT")) {
		return new CTDecorator(field, cssClass);
	    }

	    if (decoratorId.equalsIgnoreCase("QUALITY")) {
		return new ColorDecorator(field, qualityScale, cssClass, true);
	    }

	    if (decoratorId.equalsIgnoreCase("DEPTH")) {
		return new ColorDecorator(field, depthScale, cssClass, true);
	    }

	}

	if (field != null) {
	    return new FieldDecorator(field);
	}

	return null;
    }

    public static void registerDecorator(String decoratorId,
	    IDecoratorCreator creator) {
	creators.put(decoratorId, creator);
    }

}
