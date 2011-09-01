# JBoss Microcontainer Beans
This example takes a POJO that was configured for JBoss AS 5/6 and tried to deploy it in JBoss AS7.


# Building

    ../gradlew build

# Starting AS7
    
    ./standalone.sh
    
# Deploying
   cp target/libs/mcbeans.jar /path/as7/standalone/deployments
   

# Step 1: Deployment error 

    14:57:09,897 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-3) Starting deployment of "mcbeans.jar"
    14:57:09,901 ERROR [org.jboss.msc.service.fail] (MSC service thread 1-3) MSC00001: Failed to start service jboss.deployment.unit."mcbeans.jar".PARSE: org.jboss.msc.service.StartException in service jboss.deployment.unit."mcbeans.jar".PARSE: Failed to process phase PARSE of deployment "mcbeans.jar"
        at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:121) [jboss-as-server-7.1.0.Alpha1-SNAPSHOT.jar:7.1.0.Alpha1-SNAPSHOT]
        at org.jboss.msc.service.ServiceControllerImpl$StartTask.startService(ServiceControllerImpl.java:1824) [jboss-msc-1.0.1.GA.jar:1.0.1.GA]
        at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1759) [jboss-msc-1.0.1.GA.jar:1.0.1.GA]
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886) [:1.6.0_26]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908) [:1.6.0_26]
        at java.lang.Thread.run(Thread.java:680) [:1.6.0_26]
    Caused by: org.jboss.as.server.deployment.DeploymentUnitProcessingException: Failed to parse POJO xml ["/content/mcbeans.jar/META-INF/test-jboss-beans.xml"]
        at org.jboss.as.pojo.KernelDeploymentParsingProcessor.parseDescriptor(KernelDeploymentParsingProcessor.java:131)
        at org.jboss.as.pojo.KernelDeploymentParsingProcessor.parseDescriptors(KernelDeploymentParsingProcessor.java:105)
        at org.jboss.as.pojo.KernelDeploymentParsingProcessor.deploy(KernelDeploymentParsingProcessor.java:74)
        at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:115) [jboss-as-server-7.1.0.Alpha1-SNAPSHOT.jar:7.1.0.Alpha1-SNAPSHOT]
        ... 5 more
    Caused by: javax.xml.stream.XMLStreamException: ParseError at [row,col]:[3,1]
    Message: Unexpected element '{urn:jboss:bean-deployer:2.0}deployment'
        at org.jboss.staxmapper.XMLMapperImpl.processNested(XMLMapperImpl.java:98)
        at org.jboss.staxmapper.XMLMapperImpl.parseDocument(XMLMapperImpl.java:59)
        at org.jboss.as.pojo.KernelDeploymentParsingProcessor.parseDescriptor(KernelDeploymentParsingProcessor.java:124)
        ... 8 more

    14:57:09,903 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 1) Deployment of "mcbeans.jar" was rolled back with failure message {"Failed services" => {"jboss.deployment.unit.\"mcbeans.jar\".PARSE" => "org.jboss.msc.service.StartException in service jboss.deployment.unit.\"mcbeans.jar\".PARSE: Failed to process phase PARSE of deployment \"mcbeans.jar\""}}
    14:57:09,906 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-1) Stopped deployment mcbeans.jar in 2ms
    14:57:09,908 ERROR [org.jboss.as.deployment] (DeploymentScanner-threads - 2) {"Composite operation failed and was rolled back. Steps that failed:" => {"Operation step-2" => {"Failed services" => {"jboss.deployment.unit.\"mcbeans.jar\".PARSE" => "org.jboss.msc.service.StartException in service jboss.deployment.unit.\"mcbeans.jar\".PARSE: Failed to process phase PARSE of deployment \"mcbeans.jar\""}}}}
What this says is that the element _deployment_ with the namespace _urn:jboss:bean-deployer:2.0_ in not recognized. The namespace has changed with AS7 and should 
now be _urn:jboss:mc:7.0_
Open up [test-jboss-beans.xml](mcbeans/src/main/resources/test-jboss-beans.xml) and add the correct namespace.

Now, rebuild the application and deploy once more. You should see the following in the server console:

    15:02:25,180 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-4) Starting deployment of "mcbeans.jar"
    15:02:25,215 INFO  [org.jboss.as.jpa] (MSC service thread 1-3) added javax.persistence.api dependency to mcbeans.jar
    15:02:25,279 INFO  [stdout] (MSC service thread 1-3) created TestBean
    15:02:25,287 INFO  [se.rl.mcbeans.TestBean] (MSC service thread 1-4) start
    15:02:25,292 INFO  [se.rl.mcbeans.TestBean] (MSC service thread 1-3) start
    15:02:25,354 INFO  [org.jboss.as.server.controller] (DeploymentScanner-threads - 1) Deployed "mcbeans.jar"
