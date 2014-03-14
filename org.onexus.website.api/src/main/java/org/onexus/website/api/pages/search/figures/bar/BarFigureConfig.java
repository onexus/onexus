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
package org.onexus.website.api.pages.search.figures.bar;

import org.onexus.resource.api.annotations.ResourceAlias;
import org.onexus.website.api.pages.search.FigureConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ResourceAlias("figure-bar")
public class BarFigureConfig extends FigureConfig {

    @NotNull @Valid
    private CollectionField value;

    @NotNull @Valid
    @ResourceAlias("x-axis")
    private CollectionField xAxis;

    @NotNull @Valid
    @ResourceAlias("y-axis")
    private CollectionField yAxis;

    private String init;

    public BarFigureConfig() {
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public CollectionField getValue() {
        return value;
    }

    public void setValue(CollectionField value) {
        this.value = value;
    }

    public CollectionField getxAxis() {
        return xAxis;
    }

    public void setxAxis(CollectionField xAxis) {
        this.xAxis = xAxis;
    }

    public CollectionField getyAxis() {
        return yAxis;
    }

    public void setyAxis(CollectionField yAxis) {
        this.yAxis = yAxis;
    }
}
