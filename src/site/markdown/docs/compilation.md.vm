# Compilation

Follow above instructions to compile Onexus source code.

## Requirements

If you have Java JDK 6, Maven 3 and Git installed you can skip these
steps.

1. We will install everything in the same folder:

        mkdir onexus-workspace
        cd onexus-workspace

2. Install Java:
    Download Java JDK 6 from [Oracle website](http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u31-download-1501634.html)
    and install it:

        chmod +x jdk-6u31-linux-x64.bin
        ./jdk-6u31-linux-x64.bin
        export JAVA\_HOME=\`pwd\`/jdk1.6.0\_31/jre/

3. Install Maven:

        wget http://apache.rediris.es/maven/binaries/apache-maven-3.0.4-bin.tar.gz
        tar xvzf apache-maven-3.0.4-bin.tar.gz
        export PATH=$PATH:\`pwd\`/apache-maven-3.0.4/bin

4. Install Git:

        sudo apt-get install git

## Compile and package Onexus

1. Clone Onexus git repository:

        git clone git://github.com/onexus/onexus.git

2. Compile and package Onexus:

        cd onexus
        mvn clean install

3. Now you can proceed to the normal [installation](installation.html)
   using the SNAPSHOT version.

