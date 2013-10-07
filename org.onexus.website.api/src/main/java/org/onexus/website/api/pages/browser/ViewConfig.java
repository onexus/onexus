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
package org.onexus.website.api.pages.browser;


import org.onexus.website.api.pages.PageConfig;
import org.onexus.website.api.utils.authorization.Authorization;
import org.onexus.website.api.utils.visible.IVisible;
import org.onexus.website.api.widgets.WidgetConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewConfig implements Serializable, IVisible {

    private String title;

    private String main;
    private String left;
    private String top;
    private String topRight;

    private String visible;

    public ViewConfig() {
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    @Override
    public String getVisible() {
        return visible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTopRight() {
        return topRight;
    }

    public void setTopRight(String topRight) {
        this.topRight = topRight;
    }

    public static List<WidgetConfig> getSelectedWidgetConfigs(PageConfig pageConfig, String... selectedWidgets) {

        List<WidgetConfig> widgets = new ArrayList<WidgetConfig>();

        if (selectedWidgets != null) {
            for (String selectedWidget : selectedWidgets) {
                if (selectedWidget != null) {
                    for (String widget : selectedWidget.split(",")) {
                        WidgetConfig widgetConfig = pageConfig.getWidget(widget.trim());
                        if (widgetConfig != null && Authorization.authorize(widgetConfig)) {
                            widgets.add(widgetConfig);
                        }
                    }
                }
            }
        }

        return widgets;
    }

    /*
    public static List<WidgetStatus> getSelectedWidgetStatuses(PageStatus pageStatus, String... selectedWidgets) {

        List<WidgetStatus> widgets = new ArrayList<WidgetStatus>();

        if (selectedWidgets != null) {
            for (String selectedWidget : selectedWidgets) {
                if (selectedWidget != null) {
                    for (String widget : selectedWidget.split(",")) {
                        WidgetStatus widgetStatus = pageStatus.getWidgetStatus(widget.trim());
                        if (widgetStatus != null) {
                            widgets.add(widgetStatus);
                        }
                    }
                }
            }
        }

        return widgets;
    } */

    @Override
    public String toString() {
        return title;
    }


}
