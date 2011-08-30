# JBoss Modular Service Container ServiceActivator
This project contains a JBoss MSC ServiceActivator that registers a simple MSC service. The intention
of this is to demonstrate the ability to add a custom service to JBoss AS7. The use case could be that 
a similar service was used with an older version of JBoss AS and needs to be migrated. 

## What to look for in this example

### META-INF/services/org.jboss.msc.service.ServiceActivator
This is the file that AS7 will scan for and call the activate method of the fully qualified class name
that it finds in this file.

### se.rl.msc.StartupActivator
This is the implementation of _org.jboss.msc.service.ServiceActivator_ which installs a service into the
service controller. 

### se.rl.msc.SampleService
Sample Service is a MSC service which could either implement the functionality 
of delegate to a pre-existing class when the start and stop methods are called.

## Building

    gradle build
    
## Deploying
Use any of the management interfaces for AS7 to deploy the jar which is available in _target/libs_

## Undeploying
Using CLI:

    [standalone@localhost:9999 /] /deployment=service-activator.jar:undeploy
	{"outcome" => "success"}
    
