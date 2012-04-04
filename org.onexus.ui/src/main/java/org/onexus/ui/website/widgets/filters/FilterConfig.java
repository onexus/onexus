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
package org.onexus.ui.website.widgets.filters;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.onexus.core.query.Filter;
import org.onexus.ui.website.utils.visible.IVisible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XStreamAlias("filter-config")
public class FilterConfig implements Serializable, IVisible {

    private String id;

    private Boolean active;

    private Boolean deletable;

    private Boolean hidden;

    private String name;

    private String visible;

    private List<Filter> rules;

    private String htmlHelp;

    public FilterConfig() {
        super();
    }

    public FilterConfig(String id, String name, Boolean active, Filter... rules) {
        this(id, name, active, "", rules);
    }

    public FilterConfig(String id, String name, Boolean active, String visible,
                        Filter... rules) {
        this(id, name, null, active, false, visible, rules);
    }

    public FilterConfig(String id, String name, String htmlHelp,
                        boolean active, Filter... rules) {
        this(id, name, htmlHelp, active, false, "", rules);
    }

    public FilterConfig(String id, String name, String htmlHelp,
                        Boolean active, Boolean hidden, String visible, Filter... rules) {
        super();
        this.id = id;
        this.name = name;
        this.active = active;
        this.visible = visible;
        this.deletable = false;
        this.hidden = hidden;
        this.rules = new ArrayList<Filter>();
        this.rules.addAll(Arrays.asList(rules));
        this.htmlHelp = htmlHelp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public List<Filter> getRules() {
        return rules;
    }

    public void setRules(List<Filter> rules) {
        this.rules = rules;
    }

    public String getHtmlHelp() {
        return htmlHelp;
    }

    public void setHtmlHelp(String htmlHelp) {
        this.htmlHelp = htmlHelp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FilterConfig other = (FilterConfig) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}