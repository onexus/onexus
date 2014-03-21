package org.onexus.collection.store.elasticsearch.mocks;

import org.onexus.data.api.IDataManager;
import org.onexus.data.api.IDataStreams;
import org.onexus.data.api.utils.SingleDataStreams;
import org.onexus.resource.api.ORI;

public class MockDataManager implements IDataManager {

    private Class rootClass;

    public MockDataManager(Class rootClass) {
        this.rootClass = rootClass;
    }

    @Override
    public IDataStreams load(ORI dataOri) {
        return new SingleDataStreams(null, rootClass.getResourceAsStream(dataOri.getPath()));
    }

    @Override
    public long size(ORI dataOri) {
        return -1;
    }

    @Override
    public String getMount() {
        return "ds";
    }
}
