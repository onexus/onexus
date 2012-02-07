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
package org.onexus.ui.website.boxes;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.onexus.core.IEntity;

public class BoxFactory {

    private static IBoxCreator defaultBoxCreator = new GenericBoxCreator();

    private static Map<String, IBoxCreator> creators = new HashMap<String, IBoxCreator>();

    public static AbstractBox createBox(String collectionId,
	    IModel<IEntity> entity) {

	if (creators.containsKey(collectionId)) {
	    return creators.get(collectionId).createBox(collectionId, entity);
	}

	if (defaultBoxCreator != null) {
	    return defaultBoxCreator.createBox(collectionId, entity);
	}

	return null;

    }

    public static void registerBoxCreator(String collectionId,
	    IBoxCreator boxCreator) {
	creators.put(collectionId, boxCreator);
    }

    public static void setDefaultBoxCreator(IBoxCreator boxCreator) {
	defaultBoxCreator = boxCreator;
    }

}
