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
package org.onexus.ui;

import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.apache.wicket.injection.Injector;
import org.wicketstuff.osgi.inject.impl.OsgiFieldValueFactory;

public class OsgiSessionInjector extends Injector implements ISessionListener {

    private OsgiFieldValueFactory fieldFactory;

    public OsgiSessionInjector() {
        this(true);
    }

    public OsgiSessionInjector(boolean wrapInProxies) {
        fieldFactory = new OsgiFieldValueFactory(wrapInProxies);
    }

    @Override
    public void onCreated(Session session) {
        inject(session);
    }

    @Override
    public void inject(Object object) {
        inject(object, fieldFactory);
    }

}
