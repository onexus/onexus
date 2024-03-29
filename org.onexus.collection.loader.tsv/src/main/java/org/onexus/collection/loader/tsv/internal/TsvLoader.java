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
package org.onexus.collection.loader.tsv.internal;

import org.onexus.collection.api.Collection;
import org.onexus.collection.api.ICollectionLoader;
import org.onexus.collection.api.IEntitySet;
import org.onexus.data.api.IDataManager;
import org.onexus.resource.api.Plugin;
import org.onexus.resource.api.Progress;

import java.util.concurrent.Callable;

public class TsvLoader implements ICollectionLoader {

    private IDataManager dataManager;

    public TsvLoader() {
        super();
    }

    public IDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public Callable<IEntitySet> newCallable(Progress progress, Plugin plugin, Collection collection) {
        return new TsvCallable(progress, dataManager, collection);
    }
}
