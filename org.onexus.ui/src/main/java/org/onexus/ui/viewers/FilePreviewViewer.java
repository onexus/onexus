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
package org.onexus.ui.viewers;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.onexus.core.IDataManager;
import org.onexus.core.resources.Resource;
import org.onexus.ui.editor.tabs.xmleditor.XmlEditorTab;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class FilePreviewViewer extends Panel {

    @Inject
    public IDataManager dataManager;
    
    public final static int MAXIMUM_LINES = 100;

    public FilePreviewViewer(String containerId, IModel<? extends Resource> model) {
        super(containerId, model);

        Form<Resource> form = new Form<Resource>("form", (IModel<Resource>) model);
        add(form);

        TextArea<String> textArea = new TextArea<String>("file", new PreviewModel());
        textArea.setEnabled(false);
        form.add(textArea);



    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(XmlEditorTab.CODEMIRROR_CSS));
        response.render(JavaScriptHeaderItem.forReference(XmlEditorTab.CODEMIRROR_JS));
    }


        private class PreviewModel extends LoadableDetachableModel<String> {

        @Override
        protected String load() {
            
            Resource resource = (Resource) FilePreviewViewer.this.getDefaultModelObject();
            
            if (resource == null) {
                return null;                
            }
            
            List<URL> urls = dataManager.retrieve(resource.getURI());
            
            if (urls == null && urls.isEmpty()) {
                return null;
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(urls.get(0).openStream()));

                String line = in.readLine();
                StringBuilder str = new StringBuilder();
                for (int i=0; i < MAXIMUM_LINES && line!=null; i++) {
                    str.append(line).append("\n");
                    line = in.readLine();
                }
                
                in.close();
                return str.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
