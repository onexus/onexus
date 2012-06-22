Onexus installation
++++++++++++++++++++++++++++

Requirements
************

It's possible to install Onexus on any operating system that has installed Java SDK 6 (previous versions
of Java do not work). It is important that you install both the SDK and the JRE version to be able to 
run Java in server mode.

The default installation uses H2_ database as data store. H2_ is an
embedded pure Java database, which is perfect for projects with small datasets but not
for projects where you have large datasets. We recommend you to use MySQL_ database to achieve
better performance with big datasets.   

You can download and install **Java SE Development Kit 6** from here: http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javase6-419409.html.  
 

Quick start
***********

#. Download last Onexus release from the repository::

   http://repo.onexus.org/nexus/content/repositories/releases/org/onexus/onexus-server

#. Extract **onexus-server-[version].tar.gz** to the installation folder::

   # tar xzvf onexus-server-[version].tar.gz
   
#. Run it::

   # ./onexus-server-[vesion]/bin/onexus

   You will see the Karaf_ console where you can install/uninstall new Onexus plugins.
   
#. Point your browser to the Onexus web interface on `http://localhost:8181/onexus <http://localhost:8181/onexus>`_.
   Authenticate as *admin* with password *admin* (See users configuration section if you want to add, remove or change users and passwords).

#. You can stop Onexus closing the Karaf_ console by pressing Ctrl + D.



Configuration
*************

Onexus_ framework is deployed on top of Karaf_, a small OSGi based runtime which provides a lightweight container
onto which various components and applications can be deployed. If you want to understand better all the configuration options please check the
documentation of http://karaf.apache.org/.

Directory structure
-------------------

Karaf_ runtime folders. See the Karaf_ user guide for more details about this folders.
   
- **/bin**: startup scripts
- **/data**: OSGi_ working directory that contains all the current deployed bundles. If you remove this folder it is rebuild on the start up process.
- **/deploy**: Hot bundle deploy directory
- **/instances**: Directory containing child instances. See Karaf_ user guide.
- **/lib**: contains the bootstrap libraries
- **/system**: OSGi bundles repository, laid out as a Maven 2 repository
- **/local-repo**: Maven_ 2 library repository.
- **/etc**: configuration files

Onexus_ files.

- **/etc**: The files that start with *org.onexus...* are Onexus_ configuration files.
- **/repository**: This is the default local repository data (You can change it setting ONEXUS_REPOSITORY environment variable)
- **/workspaces**: The Onexus_ resources definitions. All the project, websites and data collections definitions. (You can change it setting ONEXUS_WORKSPACES environment variable).
- **onexus-h2-database.[h2/trace].db**: If you are using H2_ database these files contain all the data.
- **onexus-h2-tags.[h2/trace].db**: The label system always store the *tags* in this H2_ database (even if you are using MySQL as data store).
    
Use MySQL as data store
-----------------------

Follow this steps to install MySQL_ as default data store.

#. Install MySQL_

#. Create an empty database::

	# mysql -u root -p
	mysql> CREATE DATABASE onexus;
   
#. Create a user::

	mysql> GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,INDEX ON onexus.*
	       TO 'onexus'@'localhost' IDENTIFIED BY 'onexus';

#. Stop Onexus with Ctrl + D on Karaf_ console

#. Edit *org.onexus.collection.store.h2sql.cfg* and set "status = disabled".

#. Edit *org.onexus.collection.store.mysql.cfg* and set "status = enabled" and change other connection parameters if it's needed.

#. Start Onexus.

Now you can go to the Onexus_ web interface and using the wizard **Manage collections** click on the **load** collections to force the datasets loading into the database, or you can start
browsing any project and the loading will be done on the fly while your are browsing the project.  

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

