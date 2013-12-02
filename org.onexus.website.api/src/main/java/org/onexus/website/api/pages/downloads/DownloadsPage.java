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
package org.onexus.website.api.pages.downloads;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.onexus.collection.api.query.IQueryParser;
import org.onexus.collection.api.query.Query;
import org.onexus.website.api.pages.Page;
import org.onexus.website.api.widgets.Widget;
import org.onexus.website.api.widgets.download.DownloadWidget;
import org.onexus.website.api.widgets.download.DownloadWidgetConfig;
import org.onexus.website.api.widgets.download.DownloadWidgetStatus;

import javax.inject.Inject;

public class DownloadsPage extends Page<DownloadsPageConfig, DownloadsPageStatus> {

    private WebMarkupContainer modal;

    @Inject
    private IQueryParser queryParser;

    public DownloadsPage(String componentId, IModel<DownloadsPageStatus> statusModel) {
        super(componentId, statusModel);

        RepeatingView downloads = new RepeatingView("downloads");
        add(downloads);

        for (Download download : getConfig().getDownloads()) {

            WebMarkupContainer item = new WebMarkupContainer(downloads.newChildId());
            downloads.add(item);

            WebMarkupContainer heading = new WebMarkupContainer("heading");
            item.add(heading);

            WebMarkupContainer toggle = new WebMarkupContainer("toggle");
            toggle.add(new Label("title", download.getTitle()));
            heading.add(toggle);


            WebMarkupContainer body = new WebMarkupContainer("body");
            item.add(body);
            body.add(new Label("description", download.getDescription()).setEscapeModelStrings(false));
            body.add(new AjaxLink<Download>("download", Model.of(download)) {
                @Override
                public void onClick(AjaxRequestTarget target) {

                    final Download download = getModelObject();

                    DownloadWidgetStatus status = new DownloadWidgetStatus();
                    status.setConfig(new DownloadWidgetConfig());

                    modal.addOrReplace(new Label("header", download.getTitle()));
                    modal.addOrReplace( new DownloadWidget("content", Model.of(status)) {
                        @Override
                        protected Query getQuery() {
                            return queryParser.parseQuery(download.getQuery());
                        }
                    });

                    target.add(modal);
                    target.appendJavaScript("$('#" + modal.getMarkupId() + "').modal('show')");
                }
            });
        }

        modal = new WebMarkupContainer("modal");
        modal.setOutputMarkupId(true);
        modal.add(new Label("header", ""));
        modal.add(new EmptyPanel("content"));

        modal.add(new AjaxLink<String>("close") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Component widget = modal.get("content");
                if (widget instanceof Widget) {
                    ((Widget) widget).onClose(target);
                }
                target.appendJavaScript("$('#" + modal.getMarkupId() + "').modal('hide')");
            }
        });

        add(modal);
    }
}
