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
package org.onexus.ui.authentication.persona;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class LoggedInPanel extends Panel
{

    private static final long serialVersionUID = 1L;

    public LoggedInPanel(String id)
    {
        super(id);

        BrowserId browserId = SessionHelper.getBrowserId(getSession());
        if (browserId == null)
        {
            throw new IllegalStateException("The user must be authenticated!");
        }

        add(new Label("emailLabel", new PropertyModel<String>(browserId, "email")));
        add(new AjaxLink<Void>("logoutLink")
        {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
                SessionHelper.logOut(getSession());
                onLoggedOut(target);
            }
        });
    }

    protected void onLoggedOut(AjaxRequestTarget target)
    {

    }

}
