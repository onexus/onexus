package org.onexus.ui;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.injection.Injector;
import org.wicketstuff.osgi.inject.impl.OsgiFieldValueFactory;

public class OsgiApplicationInjector extends Injector implements IApplicationListener {
    
    private OsgiFieldValueFactory fieldFactory;

    public OsgiApplicationInjector() {
	this(true);
    }

    public OsgiApplicationInjector(boolean wrapInProxies) {
	fieldFactory = new OsgiFieldValueFactory(wrapInProxies);
    }


    @Override
    public void onAfterInitialized(Application application) {
	inject(application);
    }

    @Override
    public void onBeforeDestroyed(Application application) {
	
    }

    @Override
    public void inject(Object application) {
	inject(application, fieldFactory);
    }

}
