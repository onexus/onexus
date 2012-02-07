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
