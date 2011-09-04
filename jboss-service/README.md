# JBoss Service
This example takes a jboss-service.xml that was configured for JBoss AS 5/6 and tries to deploy it in JBoss AS7.

# Building

    ../gradlew build

# Starting AS7
    
    ./standalone.sh
    
# Deploying
   cp target/libs/jmx-beans.jar /path/as7/standalone/deployments
   

# Step 1: Deployment error 
    18:04:47,037 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-1) Starting deployment of "jmx-beans.jar"
    18:04:47,042 ERROR [org.jboss.msc.service.fail] (MSC service thread 1-4) MSC00001: Failed to start service jboss.deployment.unit."jmx-beans.jar".PARSE: org.jboss.msc.service.StartException in service jboss.deployment.unit."jmx-beans.jar".PARSE: Failed to process phase PARSE of deployment "jmx-beans.jar"
        at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:121) [jboss-as-server-7.1.0.Alpha1-SNAPSHOT.jar:7.1.0.Alpha1-SNAPSHOT]
        at org.jboss.msc.service.ServiceControllerImpl$StartTask.startService(ServiceControllerImpl.java:1824) [jboss-msc-1.0.1.GA.jar:1.0.1.GA]
        at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1759) [jboss-msc-1.0.1.GA.jar:1.0.1.GA]
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886) [:1.6.0_26]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908) [:1.6.0_26]
        at java.lang.Thread.run(Thread.java:680) [:1.6.0_26]
    Caused by: org.jboss.as.server.deployment.DeploymentUnitProcessingException: Failed to parse service xml ["/content/jmx-beans.jar/META-INF/jboss-service.xml"]
        at org.jboss.as.service.ServiceDeploymentParsingProcessor.deploy(ServiceDeploymentParsingProcessor.java:94)
        at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:115) [jboss-as-server-7.1.0.Alpha1-SNAPSHOT.jar:7.1.0.Alpha1-SNAPSHOT]
        ... 5 more
    Caused by: javax.xml.stream.XMLStreamException: ParseError at [row,col]:[1,1]
    Message: Unexpected element 'server'
        at org.jboss.staxmapper.XMLMapperImpl.processNested(XMLMapperImpl.java:98) [staxmapper-1.0.0.Final.jar:1.0.0.Final]
        at org.jboss.staxmapper.XMLMapperImpl.parseDocument(XMLMapperImpl.java:59) [staxmapper-1.0.0.Final.jar:1.0.0.Final]
        at org.jboss.as.service.ServiceDeploymentParsingProcessor.deploy(ServiceDeploymentParsingProcessor.java:87)
        ... 6 more
If you open [jboss-service.xml](jboss-service/src/main/resources/META-INF/jboss-service.xml) you'll see that the _server_ element does not have a namespace. This work fine
with previous version of JBoss AS but AS7 requires a namespace. Add the correct namespace.
Now, rebuild the application and deploy once more. You should see the following in the server console:

    18:01:01,611 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-3) Starting deployment of "jmx-beans.jar"
    18:01:01,629 INFO  [org.jboss.as.jpa] (MSC service thread 1-1) added javax.persistence.api dependency to jmx-beans.jar
    18:01:01,636 INFO  [stdout] (MSC service thread 1-1) created TestBean
    18:01:01,639 INFO  [se.rl.jmx.TestService] (MSC service thread 1-4) start
    18:01:01,751 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 1) Deployed "jmx-beans.jar"
