New widget development
++++++++++++++++++++++

Create widget from archetype
****************************

Creating a new widget using the basic widget Maven archetype::

    # mvn archetype:generate \
       -DarchetypeGroupId=org.onexus \
       -DarchetypeArtifactId=onexus-archetype-widget \
       -DarchetypeVersion=0.2-SNAPSHOT \
       -DgroupId=my.groupid \
       -DartifactId=helloworld \
       -Dversion=1.0-SNAPSHOT

You need to change goupdId "my.groupid" and artifactId "helloworld" to deseried ones.

Compile, package and install
****************************

Go into the widget folder::

   # cd helloworld
   # mvn clean install

Start Onexus and in the console execute::

  karaf@onexus-root> install -s mvn:my.groupid/helloworld/1.0-SNAPSHOT

Now your widget is installed and you can use it inside any website.onx config file like this::

   <widget-hello>
      <id>hello-world</id>
      <message>This is my first Onexus widget!</message>
   </widget-hello>

