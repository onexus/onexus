package org.onexus.resource.api;

import java.util.Collection;

public interface IProgressManager {

    public Collection<Progress> getProgresses();

    public void addProgress(Progress progress);
}
