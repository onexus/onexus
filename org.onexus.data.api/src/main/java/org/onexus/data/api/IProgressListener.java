package org.onexus.data.api;

public interface IProgressListener {

    void onError(Progress progress);

    void onWarning(Progress progress);

    void onInfo(Progress progress);

    void onDebug(Progress progress);

}
