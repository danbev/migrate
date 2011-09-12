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
This example was tested using [JBoss AS 7.0.1.Final](http://download.jboss.org/jbossas/7.0/jboss-as-7.0.1.Final/jboss-as-7.0.1.Final.zip).

    ./standalone.sh --server-config=standalone-preview.xml
    

## Deploying
There are various ways to deploy to JBoss AS7: 

* Command Line Interface (CLI) 
* Web Console (HTTP API) 
* Java API 
* File system deployment scanner


### Deploying the JMS queue
The example application uses a JMS queue which needs to added to AS7. In previous versions of JBoss AS one could package a queue definition file
with the deployment and it would be deployed with the application. With AS7 JMS destinations (queues and topics)
are configured in a central location, either in domain.xml or standalone.xml. One can add/modify a JMS destination using 
any of the administration consoles. We will demonstrate two alternatives here, CLI and Java API.  
Example using the CLI:

    [standalone@localhost:9999 /] /subsystem=messaging/jms-queue=GreeterQueue:add(entries=["queue/GreeterQueue"],durable=false)

Example using Groovy and the Java API:  
Change into the _management_ directory and run the [add-queue](migrate/blob/master/management/build.gradle) command:

    ../gradlew add-queue 

If you check the server console log you will see the following:
    
    INFO  [org.hornetq.core.server.impl.HornetQServerImpl] (MSC service thread 1-1) trying to deploy queue jms.queue.GreeterQueue
    INFO  [org.jboss.as.messaging.jms.AS7BindingRegistry] (MSC service thread 1-1) Bound messaging object to jndi name java:/queue/GreeterQueue
    
### Deploying a DataSource
The example application uses entity beans to persist data and hence requires a data source to be installed. With AS7 datasources
are configured in a central location, either in domain.xml or standalone.xml. One can add/modify a data source using any of the
administration consoles. We will demonstrate two alternatives here, CLI and Java API

Example using the CLI:

    /subsystem=datasources/data-source=MigrateDS:add(jndi-name=java:jboss/datasources/MigrateDS, pool-name=MigrateDS, driver-name=h2, connection-url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1)
    
Example using Groovy and the Java API:  
Change into the _management_ directory and run the [add-ds](migrate/blob/master/management/build.gradle) command:

    ../gradlew add-ds 
    
    
If you check the server console log you will see the following:

    INFO  [org.jboss.as.connector.subsystems.datasources] (MSC service thread 1-4) Bound data source [java:jboss/datasources/MigrateDS2]
    
    
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
    
 
# Step 1: Dependency upon pre-installed module
Now you'll get an error upon deployment which is expected as the point of the application is to show different
issues that crop up when migrating.

    16:09:06,353 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-3) Starting deployment of "migrate.ear"
    16:09:06,451 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-3) Starting deployment of "war.war"
    16:09:06,451 INFO  [org.jboss.as.server.deployment] (MSC service thread 1-4) Starting deployment of "ejb.jar"
    16:09:06,517 INFO  [org.jboss.as.jpa] (MSC service thread 1-1) added javax.persistence.api dependency to migrate.ear
    16:09:06,518 INFO  [org.jboss.as.jpa] (MSC service thread 1-3) added javax.persistence.api dependency to ejb.jar
    16:09:06,519 INFO  [org.jboss.as.jpa] (MSC service thread 1-2) added javax.persistence.api dependency to war.war
    16:09:06,537 INFO  [org.jboss.weld] (MSC service thread 1-3) Processing CDI deployment: migrate.ear
    16:09:06,557 INFO  [org.jboss.weld] (MSC service thread 1-2) Processing CDI deployment: war.war
    16:09:06,560 INFO  [org.jboss.weld] (MSC service thread 1-4) Processing CDI deployment: ejb.jar
    16:09:06,562 INFO  [org.jboss.as.ejb3.deployment.processors.EjbJndiBindingsDeploymentUnitProcessor] (MSC service thread 1-4) JNDI bindings for session bean named GreeterBean in deployment unit subdeployment "ejb.jar" of deployment "migrate.ear" are as follows:
    
            java:global/migrate/ejb/GreeterBean!se.rl.migrate.ejb.GreeterRemote
            java:app/ejb/GreeterBean!se.rl.migrate.ejb.GreeterRemote
            java:module/GreeterBean!se.rl.migrate.ejb.GreeterRemote
            java:global/migrate/ejb/GreeterBean!se.rl.migrate.ejb.GreeterLocal
            java:app/ejb/GreeterBean!se.rl.migrate.ejb.GreeterLocal
            java:module/GreeterBean!se.rl.migrate.ejb.GreeterLocal
    
    16:09:06,592 INFO  [org.jboss.weld] (MSC service thread 1-3) Starting Services for CDI deployment: migrate.ear
    16:09:06,668 INFO  [org.jboss.weld.Version] (MSC service thread 1-3) WELD-000900 1.1.2 (Final)
    16:09:06,680 INFO  [org.jboss.weld] (MSC service thread 1-3) Starting weld service
    16:09:06,845 ERROR [org.jboss.msc.service.fail] (MSC service thread 1-1) MSC00001: Failed to start service jboss.deployment.subunit."migrate.ear"."ejb.jar".INSTALL: org.jboss.msc.service.StartException in service jboss.deployment.subunit."migrate.ear"."ejb.jar".INSTALL: Failed to process phase INSTALL of subdeployment "ejb.jar" of deployment "migrate.ear"
            at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:121)
            at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1765)
            at org.jboss.msc.service.ServiceControllerImpl$ClearTCCLTask.run(ServiceControllerImpl.java:2291)
            at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886) [:1.6.0_26]
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908) [:1.6.0_26]
            at java.lang.Thread.run(Thread.java:680) [:1.6.0_26]
    Caused by: java.lang.RuntimeException: Error getting reflective information for class se.rl.migrate.ejb.GreeterBean
            at org.jboss.as.server.deployment.reflect.DeploymentReflectionIndex.getClassIndex(DeploymentReflectionIndex.java:70)
            at org.jboss.as.ee.component.EEModuleClassDescription$DefaultConfigurator.configure(EEModuleClassDescription.java:163)
            at org.jboss.as.ee.component.EEClassConfigurationProcessor$1.compute(EEClassConfigurationProcessor.java:134)
            at org.jboss.as.ee.component.EEClassConfigurationProcessor$1.compute(EEClassConfigurationProcessor.java:114)
            at org.jboss.as.ee.component.LazyValue.get(LazyValue.java:40)
            at org.jboss.as.ee.component.EEApplicationDescription.getClassConfiguration(EEApplicationDescription.java:183)
            at org.jboss.as.ejb3.component.stateless.StatelessComponentDescription.createConfiguration(StatelessComponentDescription.java:76)
            at org.jboss.as.ee.component.EEModuleConfigurationProcessor.deploy(EEModuleConfigurationProcessor.java:63)
            at org.jboss.as.server.deployment.DeploymentUnitPhaseService.start(DeploymentUnitPhaseService.java:115)
            ... 5 more
    Caused by: java.lang.NoClassDefFoundError: Lorg/apache/log4j/Logger;
            at java.lang.Class.getDeclaredFields0(Native Method) [:1.6.0_26]
            at java.lang.Class.privateGetDeclaredFields(Class.java:2291) [:1.6.0_26]
            at java.lang.Class.getDeclaredFields(Class.java:1743) [:1.6.0_26]
            at org.jboss.as.server.deployment.reflect.ClassReflectionIndex.<init>(ClassReflectionIndex.java:57)
            at org.jboss.as.server.deployment.reflect.DeploymentReflectionIndex.getClassIndex(DeploymentReflectionIndex.java:66)
            ... 13 more
    Caused by: java.lang.ClassNotFoundException: org.apache.log4j.Logger from [Module "deployment.migrate.ear.ejb.jar:main" from Service Module Loader]
            at org.jboss.modules.ModuleClassLoader.findClass(ModuleClassLoader.java:191)
            at org.jboss.modules.ConcurrentClassLoader.performLoadClassChecked(ConcurrentClassLoader.java:358)
            at org.jboss.modules.ConcurrentClassLoader.performLoadClassChecked(ConcurrentClassLoader.java:330)
            at org.jboss.modules.ConcurrentClassLoader.performLoadClass(ConcurrentClassLoader.java:307)
            at org.jboss.modules.ConcurrentClassLoader.loadClass(ConcurrentClassLoader.java:101)
            ... 18 more
        
What we should notice is this line:

    Caused by: java.lang.ClassNotFoundException: org.apache.log4j.Logger from [Module "deployment.migrate.ear.ejb.jar:main" from Service Module Loader]
What this indicates is that with AS7 and its modularity we need to explicitely state that our application uses log4j.
Since logj4 is a module that is shipped with AS7 we can be accomplished by setting a manifest header. Open _ejb/build.gradle_ and uncomment:

	attributes 'Dependencies': 'org.apache.log4j'
Now, rebuild the ear and redploy it. It should now deploy successfully.

# Step 2: Dependency upon custom module
After successfully deploying migrate.ear as explained in the previous section we are now ready to run the app. 
Open a web browser and open the following url; http://localhost:8080/war

The page presented is a very simple jsp page with a input form. The idea is that you enter your name and hit the send button.
In the background the name you entered will be sent to a JMS Queue named _GreetingQueue_. A Message Driven Bean is listening
to this queue and will be triggered.
Try this out and you'll see that another issue will be exposed:

    09:56:55,000 ERROR [org.hornetq.ra.inflow.HornetQMessageHandler] (Thread-7 (group:HornetQ-client-global-threads-479435515)) Failed to deliver message: java.lang.NoClassDefFoundError: se/rl/util/SomeUtil
        at se.rl.migrate.mdb.GreeterMDB.logConstruction(GreeterMDB.java:26)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.6.0_26]
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39) [:1.6.0_26]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25) [:1.6.0_26]
        at java.lang.reflect.Method.invoke(Method.java:597) [:1.6.0_26]
        at org.jboss.as.ee.component.ManagedReferenceLifecycleMethodInterceptor.processInvocation(ManagedReferenceLifecycleMethodInterceptor.java:69)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.invocation.WeavedInterceptor.processInvocation(WeavedInterceptor.java:53)
        at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:287)
        at org.jboss.as.weld.injection.WeldInjectionInterceptor.processInvocation(WeldInjectionInterceptor.java:73)
From the stacktrace we can see that the error is being thrown from [GreeterMDB](migrate/blob/master/ejb/src/main/java/se/rl/migrate/mdb/GreeterMDB.java)'s
logConstruction method. This method is using a class named [SomeUtil](migrate/blob/master/module/src/main/java/se/rl/util/SomeUtil.java) which is not packaged in the jar file. So, could
we fix this issue the same way as we did for the previous one. Well, it turns out we can but for this to work we need to create a module for it. This was not required by the previous task because
log4j is a pre-installed module that is shipped with JBoss AS7. Our use case here is that we have a utility jar that multiple applications can use, not only our migrate.ear. So lets install a custom
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

    attributes 'Dependencies': 'org.apache.log4j,se.rl.util:1.0'
    
Creating a custom module as explained above is great if you have multiple applications that use the same module. The downside to this
is that you have to maintain this directory structure and the modules have to be available of all installations if you are running in a cluster.

## Step 2b: Alternatively adding a module as a deployment
With AS7 you also have the option to configure a module with a deployment. You can package a [META-INF/jboss-deployment-structure.xml](migrate/blob/master/module/src/main/resources/META-INF/jboss-deployment-structure.xml) 
with your deployment or as a separate deployment. Notice how the name of such a module is prefixed with _deployment_ which means
that you'll have to update you dependencis manifest headers.
To try this out we need to revert the change to _standalone.sh_ and remove the _user-modules_ directory that we added. It should now looks like it did from the start:

    -mp \"$JBOSS_HOME/modules\" \
Next, we need to deploy our module. The jar file that the _module_ project produces is a valid module deployment so simply deploying 
to the server is enough to enable this module:

    cp module/target/libs/util-1.0-SNAPSHOT.jar /path/to/as7/standalone/deployments/
    
Next, we have to update the dependency manifest header in _ejb/build.gradle_ to depend on a deployable module:

    attributes 'Dependencies': 'org.apache.log4j, deployment.se.rl.util:1.0'
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
Notice how the GreetingBean uses _se.rl.migrate.Version_. This class is packaged in a separate jar file and can be found 
in the root of migrate.ear. Since this jar is not located in the ear's lib director, which is specified using 'library_directory'
element in _META-INF/application.xml_, AS7 has no knowledge of this class. 
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
    

    

