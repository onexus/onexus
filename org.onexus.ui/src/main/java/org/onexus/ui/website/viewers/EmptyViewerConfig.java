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
package org.onexus.ui.website.viewers;


public class EmptyViewerConfig extends ViewerConfig {

    private static EmptyViewerConfig INSTANCE = new EmptyViewerConfig("empty");

    private EmptyViewerConfig(String viewerId) {
        super(viewerId);
    }

    public static EmptyViewerConfig get() {
        return INSTANCE;
    }

    @Override
    public ViewerStatus createEmptyStatus() {
        return new ViewerStatus() {
        };
    }

    @Override
    public ViewerStatus getDefaultStatus() {
        return null;
    }


}
