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
package ${package};

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.website.api.widgets.WidgetConfig;

import java.lang.String;

@XStreamAlias("widget-hello")
public class HelloWidgetConfig extends WidgetConfig {

    private HelloWidgetStatus defaultStatus;
    private String message;

    public HelloWidgetConfig() {
        super();
    }

    public HelloWidgetConfig(String id, String message) {
        super(id);

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public HelloWidgetStatus createEmptyStatus() {
        return new HelloWidgetStatus(getId());
    }

    public HelloWidgetStatus getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(HelloWidgetStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
