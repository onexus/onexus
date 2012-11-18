Onexus installation
++++++++++++++++++++++++++++

Note
****

Onexus is under development and we still haven't released any final version, so you first need to `compile <compilation.rst>`_ the source code.

Requirements
************

You need to install Karaf_ server from here: http://karaf.apache.org/

It's possible to install Karaf_ on any operating system that has installed Java SDK 6 (previous versions
of Java do not work). It is important that you install both the SDK and the JRE version to be able to 
run Java in server mode. You can download and install **Java SE Development Kit 6** from here: http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javase6-419409.html.

Once you java Java installed to install Karaf_ you only need to extract the Karaf_
distribution on a folder.


Installation
************

#. Start Karaf_ console::

	# ./bin/karaf

#. Install the Onexus features repository (changing [version] for the current version number) ::

	karaf@root> features:addurl mvn:org.onexus/onexus-features/[version]/xml/features
   
#. Now you can choose between H2 version and MySQL version. The H2 version uses an embedded database
   so you won't need to do any extra configuration, it's a good choice to test Onexus. The MySQL is
   the recommended version if you plan to use big datasets or want to support many concurrent users.

   The H2 version::

	karaf@root> features:install -v onexus-h2

   The MySQL version::

	karaf@root> features:install -v onexus-mysql

#. MySQL version needs to be configured, check the above configuration section.
   
#. Point your browser to the Onexus web interface on `http://localhost:8181/ws <http://localhost:8181/es>`_.
   If you are using the default Karaf_ user configuration you'll be able to login with username "karaf" and password "karaf"
   (See users configuration section if you want to add, remove or change users and passwords).

#. You can stop Onexus closing the Karaf_ console by pressing Ctrl + D.

Configuration
*************

Config MySQL collection store
-----------------------------

Follow this steps to install MySQL_ as default data store.

#. Install MySQL_

#. Create an empty database::

	# mysql -u root -p
	mysql> CREATE DATABASE onexus;
   
#. Create a user::

	mysql> GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,INDEX ON onexus.*
	       TO 'onexus'@'localhost' IDENTIFIED BY 'onexus';

#. Stop Onexus with Ctrl + D on Karaf_ console

#. Create *org.onexus.collection.store.mysql.cfg* file and add this lines with the correct values::

	server = localhost
	port = 3306
	database = onexus
	username = onexus
	password = onexus


#. Start Karaf_.


Users authentication system
---------------------------

The default configuration stores the users, passwords and roles into */etc/users.properties* file. If you want 
to use other systems (like external SQL database, LDAP...) check the security section of Karaf documentation at http://karaf.apache.org/manual/latest-2.2.x/users-guide/security.html.

The important role for the Onexus is:

onexus-admin
	To get access to the Onexus_ web interface.


.. _H2: http://www.h2database.com
.. _MySQL: http://www.mysql.com
.. _Maven: http://maven.apache.org 
.. _OSGi: http://www.osgi.org
.. _Onexus: http://www.onexus.org
.. _Karaf: http://karaf.apache.org

