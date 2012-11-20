package org.onexus.resource.manger.internal;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class FileObserver {

    public static void main(String[] args) throws Exception {

        File file = new File("/home/jdeu/.onexus");

        FileAlterationObserver fileObserver = new FileAlterationObserver(file, FileFilterUtils.nameFileFilter("projects.ini"));

        fileObserver.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onFileChange(File file) {
                System.out.println("FileChange: " + file.toString());
            }

        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(2000);
        monitor.addObserver(fileObserver);
        monitor.start();

        System.in.read();

        monitor.stop();
    }
}
