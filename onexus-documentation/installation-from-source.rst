Onexus installation
++++++++++++++++++++++++++++

Follow above instructions to install Onexus from source code.

Requirements
************

#. We will install everything in the same folder::

   # mkdir onexus-workspace
   # cd onexus-workspace

#. Install Java::

   Download Java JDK 6 from http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u31-download-1501634.html
   and install it::

   # chmod +x jdk-6u31-linux-x64.bin
   # ./jdk-6u31-linux-x64.bin
   # export JAVA_HOME=`pwd`/jdk1.6.0_31/jre/

#. Install Maven::

   # wget http://apache.rediris.es/maven/binaries/apache-maven-3.0.4-bin.tar.gz
   # tar xvzf apache-maven-3.0.4-bin.tar.gz
   # export PATH=$PATH:`pwd`/apache-maven-3.0.4/bin

#. Install Git::

   # sudo apt-get install git

Compile and package Onexus
**************************

#. Clone Onexus git repository::

   # git clone https://github.com/onexus/onexus.git

#. Compile and package Onexus::

   # cd onexus
   # mvn clean package install

#. Install Onexus from your local maven repository::

   # cd ..
   # mkdir server
   # cd server
   # tar xvzf ~/.m2/repository/org/onexus/onexus-server/0.2-SNAPSHOT/onexus-server-0.2-SNAPSHOT.tar.gz

Run on debug mode
*****************

#. Run it in debug mode and attach remote debuging to port 5005::

   # ./onexus-server-0.2-SNAPSHOT/bin/onexus debug
   Browse: http://localhost:8181/onexus


#. Watch all SNAPSHOT bundles on local Maven repository

   karaf@onexus-root> dev:watch *

   Now when you run "mvn clean install" on any Onexus bundle code it will be updated automatically on the running Onexus server.

