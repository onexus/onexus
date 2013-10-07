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
