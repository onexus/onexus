package org.onexus.data.api;

import java.util.List;

public interface IDataStore {

    public void cancel(Task task);

    public boolean isRegistered(String dataUri);

    public void register(String dataUri);

    public void deregister(String dataUri);

    public List<String> getRegistered();

    public IDataStreams load(String dataUri);

}
