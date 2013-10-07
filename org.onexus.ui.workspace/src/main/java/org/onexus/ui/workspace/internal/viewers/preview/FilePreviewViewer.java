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
package org.onexus.ui.workspace.internal.viewers.preview;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.onexus.data.api.IDataManager;
import org.onexus.resource.api.Resource;
import org.onexus.ui.workspace.internal.viewers.utils.PrettifyBehavior;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class FilePreviewViewer extends Panel {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePreviewViewer.class);
    @PaxWicketBean(name = "dataManager")
    private IDataManager dataManager;

    public static final int MAXIMUM_LINES = 100;

    public FilePreviewViewer(String containerId, IModel<? extends Resource> model) {
        super(containerId, model);

        add(new PrettifyBehavior());
        add(new Label("file", new PreviewModel()));

    }

    private class PreviewModel extends LoadableDetachableModel<String> {

        @Override
        protected String load() {

            Resource resource = (Resource) FilePreviewViewer.this.getDefaultModelObject();

            if (resource == null) {
                return null;
            }

            Iterator<InputStream> inIterator = dataManager.load(resource.getORI()).iterator();

            if (!inIterator.hasNext()) {
                return null;
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(inIterator.next()));

                String line = in.readLine();
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < MAXIMUM_LINES && line != null; i++) {
                    str.append(line).append("\n");
                    line = in.readLine();
                }

                in.close();
                return str.toString();

            } catch (IOException e) {
                LOGGER.error("Reading input", e);
            }

            return null;
        }
    }
}
