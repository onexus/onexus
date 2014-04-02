package org.onexus.data.api;

import org.onexus.data.api.utils.SingleDataStreams;
import org.onexus.resource.api.ORI;

public class PackageDataManager implements IDataManager {

    private Class rootClass;

    public PackageDataManager(Class rootClass) {
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
