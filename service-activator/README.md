# JBoss Modular Service Container ServiceActivator
This project contains a JBoss MSC ServiceActivator that registers a simple MSC service. The intention
of this is to demonstrate the ability to add a custom service to JBoss AS7. The use case could be that 
a similar service was used with an older version of JBoss AS and needs to be migrated. 

This example also shows how you can customize logging. See the Custom Logging section for details.

## What to look for in this example

### META-INF/services/org.jboss.msc.service.ServiceActivator
This is the [file](service-activator/src/main/resources/META-INF/services/org.jboss.msc.service.ServiceActivator) that AS7 will scan for and call the activate method of the fully qualified class name
that it finds in this file.

### se.rl.msc.StartupActivator
[StartupActivator](service-activator/src/main/java/se/rl/msc/StartupActivator.java) is the implementation of _org.jboss.msc.service.ServiceActivator_ which installs a service into the
service controller. 

### se.rl.msc.SampleService
[SampleService](service-activator/src/main/java/se/rl/msc/SampleService.java)
is a MSC service which could either implement the functionality 
of delegate to a pre-existing class when the start and stop methods are called.

## Building

    gradle build
    
## Deploying
Use any of the management interfaces for AS7 to deploy the jar which is available in _target/libs_

## Undeploying
Using CLI:

    [standalone@localhost:9999 /] /deployment=service-activator.jar:undeploy
	{"outcome" => "success"}
    
# Service using JEE
A service is also added to this example that uses JEE annotations and hence portable. 
[EEService](service-activator/src/main/java/se/rl/ee/EEService.java) shows the usage of a singleton service that will
have its method annotated with @PostContruct called upon deployment. An upon undeployment the method annotated with
@PreDestroy will be called.

# Custom logging
This example ships with a custom [log4j configuration](service-activator/src/main/resources/logging/log4j.xml). To make logging work we need to add deployment module for log4j so that our service-activator module is separated from 
the log4j module that ships with AS7. The reason for this is that we need log4j to be initialized with our custom log4j.xml
and if we specify a dependency to the module 'org.apache.log4j' it will have been initialized before our module is deployed
and our custom configuration will not be considered.

## Adding a log4 module to the deployment
For this we need to add a [jboss-deployment-structure.xml](service-activator/src/main/resources/META-INF/jboss-deployment-structure.xml):

    <jboss-deployment-structure>

        <module name="deployment.org.apache.log4j" >
            <resources>
                <resource-root path="log4j-1.2.16.jar" />
                <resource-root path="logging"/>
            </resources>
            <dependencies>
               <module name="javaee.api"/>
            </dependencies>
        </module>
  
    </jboss-deployment-structure>
Above, we are adding the log4j jar and also the path to our log4j.xml which is in the logging directory of the service-activator.jar.
We have also added a dependency to _javaee.api_ since log4j has dependencies to a few packages which are included in that module, for example _org/w3c/dom/Node_

Finally, we need to specify that our module has a dependency to _deployment.org.apache.log4j_. We do this by adding a _Class-Path_ manifest header:

    Class-Path: 'deployment.org.apache.log4j'
This is done in build.gradle.
    
Build and deploy the application and you should see the following in the console:

    10:30:16,704 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-2) Starting deployment of "service-activator.jar"
    10:30:16,755 INFO  [org.jboss.as.jpa] (MSC service thread 1-3) added javax.persistence.api dependency to service-activator.jar
    10:30:16,769 INFO  [org.jboss.as.ejb3.deployment.processors.EjbJndiBindingsDeploymentUnitProcessor] (MSC service thread 1-4) JNDI bindings for session bean named EEService in deployment unit deployment "service-activator.jar" are as follows:

        java:global/service-activator/EEService!se.rl.ee.EEService
        java:app/service-activator/EEService!se.rl.ee.EEService
        java:module/EEService!se.rl.ee.EEService
        java:global/service-activator/EEService
        java:app/service-activator/EEService
        java:module/EEService

    10:30:16,833 INFO  [stdout] (MSC service thread 1-1) [logging/log4j.xml]: se.rl.msc.SampleService start 
    10:30:16,835 INFO  [stdout] (MSC service thread 1-3) [logging/log4j.xml]: se.rl.ee.EEService start  
    
  



