## OSGi Blueprint Example
This project contains a very basic example of an application that uses the OSGi Blueprint specification.

The main point of this example is that the implemenation does not contain any code related to OSGi. Instead the [blueprint
xml configuration file](osgi/blueprint/src/main/resources/OSGI-INF/blueprint/pojo.xml) is read by the OSGi runtime and it 
will take care of creating and wiring the [POJO](osgi/blueprint/src/main/java/se/rl/blueprint/Pojo.java) instance.

# Building

    ../../gradlew build
    
# Deploying
Deploy _target/libs/osgi-blueprint.jar_ to your OSGi Container.

# Output
When deploying the bundle you should see the following output (AS7.0.2):
    
    10:51:12,475 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-1) Starting deployment of "osgi-blueprint.jar"
	10:51:12,485 INFO  [org.jboss.osgi.framework.internal.BundleManager] (MSC service thread 1-2) Install bundle: migrate.osgi/blueprint:0.0.0.unspecified
	10:51:12,496 INFO  [org.jboss.osgi.framework.internal.HostBundleState] (MSC service thread 1-2) Bundle started: migrate.osgi/blueprint:0.0.0.unspecified
	10:51:12,523 INFO  [stdout] (pool-9-thread-3) Pojo start
	10:51:12,533 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 2) Deployed "osgi-blueprint.jar"

# Notes about AS 7.0.2
The blueprint bundle is configured to start at startlevel 3, and the default startlevel for the OSGi subsystem on AS7
is 1. You can change this by updating the startlevel in the _osgi_ subsystem element in standalone.xml or domain.xml:

	<property name="org.osgi.framework.startlevel.beginning">3</property>
	
	