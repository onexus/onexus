# Onexus installation

## Note

Onexus is under development and we still haven't released any final
version, so you first need to [compile](compilation.html) the source
code.

## Requirements

You need to install Karaf server from [http://karaf.apache.org](http://karaf.apache.org):

It's possible to install Karaf on any
operating system that has installed Java SDK 6 (previous versions of
Java do not work). It is important that you install both the SDK and the
JRE version to be able to run Java in server mode. You can download and
install **Java SE Development Kit 6** from [Oracle website](http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javase6-419409.html).

Once you java Java installed to install  Karaf you only need to extract the
Karaf distribution on a folder.

## Installation

1. Start Karaf console:

        ./bin/karaf

2. Install the Onexus features repository:

        karaf@root> features:addurl mvn:org.onexus/onexus-features/${project.version}/xml/features

3. Now you can choose between H2 version and MySQL version. The H2
   version uses an embedded database so you won't need to do any extra
   configuration, it's a good choice to test Onexus. The MySQL is the
   recommended version if you plan to use big datasets or want to
   support many concurrent users.

   The H2 version:

        karaf@root> features:install -v onexus-h2

   The MySQL version (first configure the database, check the above configuration section):

        karaf@root> features:install -v onexus-mysql

4. Point your browser to the Onexus web interface on
   [http://localhost:8181/ws](http://localhost:8181/ws).
   If you are using the default Karaf user configuration you'll be able to login
   with username "karaf" and password "karaf" (See users configuration section if you want to add,
   remove or change users and passwords).

5. You can stop Onexus closing the Karaf console by pressing Ctrl + D.

## Configuration

### Config MySQL collection store

Follow this steps to install [MySQL](http://www.mysql.com) as default
data store.

1. Install MySQL

2. Create an empty database:

        mysql -u root -p
        mysql> CREATE DATABASE onexus;

3. Create a user:

        mysql> GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,INDEX ON onexus.*
               TO 'onexus'@'localhost' IDENTIFIED BY 'onexus';

4. Stop Onexus with Ctrl + D on Karaf console

5. Create **org.onexus.collection.store.mysql.cfg** file into **/etc** folder
   and add this lines with the correct values:

        server = localhost
        port = 3306
        database = onexus
        username = onexus
        password = onexus

6. Start Karaf.

### Users authentication system

The default configuration stores the users, passwords and roles into
**/etc/users.properties** file. If you want to use other systems (like
external SQL database, LDAP...) check the security section of Karaf
documentation at
[http://karaf.apache.org/manual/latest-2.2.x/users-guide/security.html](http://karaf.apache.org/manual/latest-2.2.x/users-guide/security.html).

The important role to get access to the Onexus web interface is **admin**.
