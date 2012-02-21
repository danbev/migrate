# Migrate to AS7
This project contains a sample application with various issues that you can run into when migrating an 
existing application to JBoss AS 7. 
The sample application, an ear, is deployed to AS7 and issues will crop up which will be fixed step by step,
until the application can be deployed and run. 

## Overview of the migrate application
The application is an enterprise application archive and contains an EJB, an MDB and a WAR. 

The entry point to the application is via a jsp presented which contains a very simple input form. 
The idea is that you enter your name and hit the send button. In the background the name you entered will be sent to a 
JMS Queue named _GreetingQueue_. An MDB is listening to this queue and will be triggered. The MDB will in turn invoke
the EJB with the content of the JMS Message. 


## Building
To build the application execute the following command from the root of the project:

    ./gradlew ear
or on windows:

    gradlew.bat ear
    
    
The artifact produced will be located in _target/libs_.

## Starting AS7
This example was tested using [JBoss AS 7.1.0.Final](http://download.jboss.org/jbossas/7.1/jboss-as-7.1.0.Final/jboss-as-7.1.0.Final.zip).

    ./standalone.sh -c standalone-full.xml
    

## Deploying
There are various ways to deploy to JBoss AS7: 

* Command Line Interface (CLI) 
* Web Console (HTTP API) 
* Java API 
* File system deployment scanner


### Deploying the ear
Deploying using the file system:

    cp target/libs/migrate.ear /path/to/as7/standalone/deployments
    
Deploying using CLI:

    [standalone@localhost:9999 /] deploy --force /path/to/migrate/target/libs/migrate.ear
The _--force_ option is specified to redeploy the application if it was already deployed.

As an alternative method of deploying you can add another directory that the deployment scanner will scan:

    [standalone@localhost:9999 /] /subsystem=deployment-scanner/scanner=user:add(path=/path/to/migrate/target/libs/)  
With this in place the application will be deployed automatically after building. 
If you find this annoying when playing with the app just remove the added scanner:

    [standalone@localhost:9999 /] /subsystem=deployment-scanner/scanner=user:remove                                                              
    
Now you'll get an error upon deployment which is expected as the point of the application is to show different
issues that crop up when migrating. Follow the steps below to take care of the issues as the appear.

# Step 1: JMS destination deployment error
When you deploy the application the first time you'll get the following error in server console:

    09:19:18,534 ERROR [org.jboss.msc.service.fail] (MSC service thread 1-2) MSC00001: Failed to start service jboss.deployment.subunit."migrate.ear"."ejb.jar".PARSE: org.jboss.msc.service.StartException in service jboss.deployment.subunit."migrate.ear"."ejb.jar".PARSE: Failed to process phase PARSE of subdeployment "ejb.jar" of deployment "migrate.ear"
        at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:119) [jboss-as-server-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.msc.service.ServiceControllerImpl$StartTask.startService(ServiceControllerImpl.java:1811) [jboss-msc-1.0.2.GA.jar:1.0.2.GA]
        at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1746) [jboss-msc-1.0.2.GA.jar:1.0.2.GA]
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886) [classes.jar:1.6.0_29]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908) [classes.jar:1.6.0_29]
        at java.lang.Thread.run(Thread.java:680) [classes.jar:1.6.0_29]
    Caused by: org.jboss.as.server.deployment.DeploymentUnitProcessingException: JBAS011666: Could not parse file /Users/danbev/work/jboss/as/bundles/jboss-as-7.1.0.Final/standalone/tmp/vfs/deployment9c32baaf26eee6e9/ejb.jar-9e14ada594ca5798/contents/META-INF/hornetq-jms.xml
        at org.jboss.as.messaging.deployment.MessagingXmlParsingDeploymentUnitProcessor.deploy(MessagingXmlParsingDeploymentUnitProcessor.java:76)
        at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:113) [jboss-as-server-7.1.0.Final.jar:7.1.0.Final]
        ... 5 more
    Caused by: org.jboss.as.server.deployment.DeploymentUnitProcessingException: JBAS011666: Could not parse file /Users/danbev/work/jboss/as/bundles/jboss-as-7.1.0.Final/standalone/tmp/vfs/deployment9c32baaf26eee6e9/ejb.jar-9e14ada594ca5798/contents/META-INF/hornetq-jms.xml
        at org.jboss.as.messaging.deployment.MessagingXmlParsingDeploymentUnitProcessor.deploy(MessagingXmlParsingDeploymentUnitProcessor.java:73)
        ... 6 more
    Caused by: javax.xml.stream.XMLStreamException: ParseError at [row,col]:[2,1]
    Message: Unexpected element '{urn:hornetq}configuration'
        at org.jboss.staxmapper.XMLMapperImpl.processNested(XMLMapperImpl.java:108)
        at org.jboss.staxmapper.XMLMapperImpl.parseDocument(XMLMapperImpl.java:69)
        at org.jboss.as.messaging.deployment.MessagingXmlParsingDeploymentUnitProcessor.deploy(MessagingXmlParsingDeploymentUnitProcessor.java:67)
        ... 6 more

From the above error message we can that AS7 cannot parse the _hornetq-jms.xml_ file. The syntax for jms deployments has been modified and needs to be updated
to work with AS7. 

Open up [hornetq-jms.xml](migrate/blob/master/ejb/src/main/resources/META-INF/hornetq-jms.xml) and comment out the old version and uncomment the new configuration.


# Step 2: Dependency upon pre-installed module 
After successfully deploying migrate.ear we are now ready to run the app. 
Open a web browser and open the following url: [http://localhost:8080/war](http://localhost:8080/war)

The page presented is a very simple jsp page with a input form. The idea is that you enter your name and hit the send button.
In the background the name you entered will be sent to a JMS Queue named _GreetingQueue_. A Message Driven Bean is listening
to this queue and will be triggered.
Now deploy the application and you'll see the following error in the server console:

    10:00:29,868 ERROR [org.jboss.ejb3.invocation] (Thread-8 (HornetQ-client-global-threads-2416231)) JBAS014134: EJB Invocation failed on component GreeterMDB for method public abstract void javax.jms.MessageListener.onMessage(javax.jms.Message): javax.ejb.EJBTransactionRolledbackException: Unexpected Error
        at org.jboss.as.ejb3.tx.CMTTxInterceptor.handleInCallerTx(CMTTxInterceptor.java:133) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.as.ejb3.tx.CMTTxInterceptor.invokeInCallerTx(CMTTxInterceptor.java:204) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.as.ejb3.tx.CMTTxInterceptor.required(CMTTxInterceptor.java:306) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.as.ejb3.tx.CMTTxInterceptor.processInvocation(CMTTxInterceptor.java:190) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ejb3.component.interceptors.CurrentInvocationContextInterceptor.processInvocation(CurrentInvocationContextInterceptor.java:41) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ejb3.component.interceptors.LoggingInterceptor.processInvocation(LoggingInterceptor.java:59) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ee.component.NamespaceContextInterceptor.processInvocation(NamespaceContextInterceptor.java:50) [jboss-as-ee-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ejb3.component.interceptors.AdditionalSetupInterceptor.processInvocation(AdditionalSetupInterceptor.java:43) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ejb3.component.messagedriven.MessageDrivenComponentDescription$4$1.processInvocation(MessageDrivenComponentDescription.java:177) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ee.component.TCCLInterceptor.processInvocation(TCCLInterceptor.java:45) [jboss-as-ee-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.invocation.ChainedInterceptor.processInvocation(ChainedInterceptor.java:61) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ee.component.ViewService$View.invoke(ViewService.java:165) [jboss-as-ee-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.as.ee.component.ViewDescription$1.processInvocation(ViewDescription.java:173) [jboss-as-ee-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:288) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.invocation.ChainedInterceptor.processInvocation(ChainedInterceptor.java:61) [jboss-invocation-1.1.1.Final.jar:1.1.1.Final]
        at org.jboss.as.ee.component.ProxyInvocationHandler.invoke(ProxyInvocationHandler.java:72) [jboss-as-ee-7.1.0.Final.jar:7.1.0.Final]
        at javax.jms.MessageListener$$$view1.onMessage(Unknown Source) [jboss-jms-api_1.1_spec-1.0.0.Final.jar:1.0.0.Final]
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [classes.jar:1.6.0_29]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39) [classes.jar:1.6.0_29]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25) [classes.jar:1.6.0_29]
        at java.lang.reflect.Method.invoke(Method.java:597) [classes.jar:1.6.0_29]
        at org.jboss.as.ejb3.inflow.MessageEndpointInvocationHandler.doInvoke(MessageEndpointInvocationHandler.java:140) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at org.jboss.as.ejb3.inflow.AbstractInvocationHandler.invoke(AbstractInvocationHandler.java:73) [jboss-as-ejb3-7.1.0.Final.jar:7.1.0.Final]
        at $Proxy32.onMessage(Unknown Source)   at org.hornetq.ra.inflow.HornetQMessageHandler.onMessage(HornetQMessageHandler.java:278)
        at org.hornetq.core.client.impl.ClientConsumerImpl.callOnMessage(ClientConsumerImpl.java:983)
        at org.hornetq.core.client.impl.ClientConsumerImpl.access$400(ClientConsumerImpl.java:48)
        at org.hornetq.core.client.impl.ClientConsumerImpl$Runner.run(ClientConsumerImpl.java:1113)
        at org.hornetq.utils.OrderedExecutorFactory$OrderedExecutor$1.run(OrderedExecutorFactory.java:100)
        at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886) [classes.jar:1.6.0_29]
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908) [classes.jar:1.6.0_29]
        at java.lang.Thread.run(Thread.java:680) [classes.jar:1.6.0_29]    
    Caused by: java.lang.NoClassDefFoundError: se/rl/util/SomeUtil
        at se.rl.migrate.mdb.GreeterMDB.logConstruction(GreeterMDB.java:27) [ejb.jar:]
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [classes.jar:1.6.0_29]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39) [classes.jar:1.6.0_29]
    Caused by: java.lang.ClassNotFoundException: se.rl.util.SomeUtil from [Module "deployment.migrate.ear.ejb.jar:main" from Service Module Loader]
        at org.jboss.modules.ModuleClassLoader.findClass(ModuleClassLoader.java:190)
        at org.jboss.modules.ConcurrentClassLoader.performLoadClassUnchecked(ConcurrentClassLoader.java:468)
        at org.jboss.modules.ConcurrentClassLoader.performLoadClassChecked(ConcurrentClassLoader.java:456)
        at org.jboss.modules.ConcurrentClassLoader.performLoadClassChecked(ConcurrentClassLoader.java:423)
        at org.jboss.modules.ConcurrentClassLoader.performLoadClass(ConcurrentClassLoader.java:398)
        at org.jboss.modules.ConcurrentClassLoader.loadClass(ConcurrentClassLoader.java:120)
        ... 72 more
        
From the stacktrace we can see that the error is being thrown from [GreeterMDB](migrate/blob/master/ejb/src/main/java/se/rl/migrate/mdb/GreeterMDB.java)'s
logConstruction method. This method is using a class named [SomeUtil](migrate/blob/master/module/src/main/java/se/rl/util/SomeUtil.java) which is not packaged in the jar file. 

The use case here is that we have a utility jar that multiple applications can use, not only our migrate.ear. So lets install a custom
module.

First we need to build the _module_ project:

    ../gradlew mod
This command will produce a directory named _user-modules_ in module/_target_ directory. Copy this directory to your servers home directory:

    cp -r user-module /path/to/as7
The last thing to do is to make AS7 aware of this custom modules directory. You may have noticed that there is directory named _modules_ in the servers home directory. This is where all the pre-installed 
modules that are shipped with the server are stored. To avoid mixing our custom modules and make upgrading easier we will use a different directory. To accomplish this we need to 
update the standalone.sh or standalone.bat file:

     -mp \"$JBOSS_HOME/modules\":\"$JBOSS_HOME/user-modules\" \
Notice that _standalone.sh_ contains two entries for the modules path argument (mp). It is the first one that is used in this example and the updated section should look like this:

     # Execute the JVM in the foreground
      eval \"$JAVA\" $JAVA_OPTS \
         \"-Dorg.jboss.boot.log.file=$JBOSS_HOME/standalone/log/boot.log\" \
         \"-Dlogging.configuration=file:$JBOSS_HOME/standalone/configuration/logging.properties\" \
         -jar \"$JBOSS_HOME/jboss-modules.jar\" \
         -mp \"$JBOSS_HOME/modules:$JBOSS_HOME/user-modules\" \
         -logmodule "org.jboss.logmanager" \
         -jaxpmodule javax.xml.jaxp-provider \
         org.jboss.as.standalone \
         -Djboss.home.dir=\"$JBOSS_HOME\" \
         "$@"
         
Now we only need to add this dependency to our ejb project. Open _ejb/build.gradle_ and 'se.rl.util:1.0' as a dependency:

    attributes 'Dependencies': 'se.rl.util:1.0'
    
Creating a custom module as explained above is great if you have multiple applications that use the same module. The downside to this
is that you have to maintain this directory structure and the modules have to be available of all installations if you are running in a cluster.

## Step 2b: Alternatively adding a module as a deployment 
With AS7 you also have the option to configure a module with a deployment. You can package a [META-INF/jboss-deployment-structure.xml](migrate/blob/master/module/src/main/resources/META-INF/jboss-deployment-structure.xml) 
with your deployment or as a separate deployment. 

Notice how the name of such a module is prefixed with _deployment_ which means
that you'll have to update you dependencies manifest headers.
To try this out we need to revert the change to _standalone.sh_ and remove the _user-modules_ directory that we added. It should now looks like it did from the start:

    -mp \"$JBOSS_HOME/modules\" \
Next, we need to deploy our module. The jar file that the _module_ project produces is a valid module deployment so simply deploying 
to the server is enough to enable this module:

    cp module/target/libs/util-1.0-SNAPSHOT.jar /path/to/as7/standalone/deployments/
    
Next, we have to update the dependency manifest header in _ejb/build.gradle_ to depend on a deployable module:

    attributes 'Dependencies': 'deployment.se.rl.util:1.0'
    
Now redeploy migrate.ear and re-run the application. 

    
# Step 3. Dependency on jar in deployment archive 
Re-build and deploy migrate.ear and re-run the application again. The following error will be displayed:

    Caused by: java.lang.NoClassDefFoundError: se/rl/migrate/Version
        at se.rl.migrate.ejb.GreeterBean.greet(GreeterBean.java:14)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.6.0_26]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39) [:1.6.0_26]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25) [:1.6.0_26]
        at java.lang.reflect.Method.invoke(Method.java:597) [:1.6.0_26]
        at org.jboss.as.ee.component.ManagedReferenceMethodInterceptor.processInvocation(ManagedReferenceMethodInterceptor.java:51)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.invocation.InterceptorContext$Invocation.proceed(InterceptorContext.java:370)
        at org.jboss.as.weld.ejb.Jsr299BindingsInterceptor.doMethodInterception(Jsr299BindingsInterceptor.java:114)
        at org.jboss.as.weld.ejb.Jsr299BindingsInterceptor.processInvocation(Jsr299BindingsInterceptor.java:122)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.invocation.WeavedInterceptor.processInvocation(WeavedInterceptor.java:53)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.as.jpa.interceptor.SBInvocationInterceptor.processInvocation(SBInvocationInterceptor.java:45)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.as.ee.component.NamespaceContextInterceptor.processInvocation(NamespaceContextInterceptor.java:44)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.as.ee.component.TCCLInterceptor.processInvocation(TCCLInterceptor.java:45)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.invocation.InitialInterceptor.processInvocation(InitialInterceptor.java:21)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.invocation.ChainedInterceptor.processInvocation(ChainedInterceptor.java:61)
        at org.jboss.as.ee.component.ViewDescription$ComponentDispatcherInterceptor.processInvocation(ViewDescription.java:202)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.as.ejb3.component.pool.PooledInstanceInterceptor.processInvocation(PooledInstanceInterceptor.java:44)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.as.ejb3.component.session.SessionInvocationContextInterceptor$CustomSessionInvocationContext.proceed(SessionInvocationContextInterceptor.java:126)
        at org.jboss.ejb3.tx2.impl.CMTTxInterceptor.invokeInCallerTx(CMTTxInterceptor.java:233)
        ... 55 more
To understand this issue we need to take a look at the [GreeterBean](migrate/blob/master/ejb/src/main/java/se/rl/migrate/ejb/GreeterBean.java).
Notice how the GreetingBean uses _se.rl.migrate.Version_. This class is packaged in a separate jar file which is located in the root of  
of migrate.ear. Since this jar is not in the ear's lib director, which is specified using _library\_directory_
element in [META-INF/application.xml](migrate/blob/master/src/main/application/META-INF/application.xml), AS7 has no knowledge of this class. 
To make AS7, and other containers for that matter, aware of this class we need to update the ejb.jar's MANIFEST.

Again, open _ejb/build.gradle_ and uncomment:

	attributes 'Class-Path': "version.jar"
Now, rebuild and deploy the application. Then retry entering a name and pressing the send button. 
If all goes well you should see somethings similar to the following in the server console:

    INFO [se.rl.migrate.ejb.GreeterBean] (Thread-4 (group:HornetQ-client-global-threads-780135981)) [1.0] Hello Daniel

# service-activator project
The [service-activator](migrate/blob/master/service-activator) project contains an example of a JBoss Modular Service Container ServiceActivator,
and an example of a portable alternative using JEE. This project also contains an example of customizing logging using log4j.
See the project readme for for information.

# mcbeans project
The [mcbeans](migrate/blob/master/mcbeans) project contains an example of a JBoss Micro container bean and deploying it to AS7.

# jboss-service project
The [jboss-service](migrate/blob/master/jboss-service) project contains an example of taking a jboss-service.xml and deploying it to AS7.
    
# osgi/core
The [osgi-core](migrate/blob/master/osgi/core) project contains a very simple example of an OSGI BundleActivator.

# osgi/blueprint
The [osgi-blueprint](migrate/blob/master/osgi/blueprint) project contains a very simple example using OSGI blueprint specification.
    

