## Migrate to AS7
This project contains a sample application with various issues that you can run into when migrating an 
existing application to JBoss AS 7. 
The sample application, an ear, is deployed to AS7 and issues will crop up which will be fixed step by step
until finally the application can be deployed an run. 

# Overview of the migrate application
The application is an enterprice application archive and contains a ejb and a war. There is also a "normal" jar that 
contains code that is used from the ejb jar. 

# Building
To build the application execute the following command from the root of the project:

    gradle ear
    
The artifact produced will be located in _target/libs_.

# Deploying
There are various ways to deploy to JBoss AS7, CLI, Web Console, API, file system. For this example we will be using
the file system deploy method. 
First, start JBoss AS 7:

    ./standalone.sh --server-config=standalone-preview.xml
    
Next, deploy the migrate.ear:

    cp target/lib/migrate.ear /path/to/as7/standalone/deployments
    
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
This can be accomplished by setting a manifest header. Open _ejb/build.gradle_ and uncomment:

	attributes 'Dependencies': 'org.apache.log4j'
Now, rebuild the ear and redploy it. It should now deploy successfully.

# Running the application
After successfully deploying migrate.ear as explained in the previous section we are now ready to run the app. 
Open a web browser and open the following url; http://localhost:8080/war

The page presented is a very simple jsp page with a input form. The idea is that you enter your name and hit the send button.
In the background the name you entered will be sent to a JMS Queue named _GreetingQueue_. A Message Driven Bean is listening
to this queue and will be triggered.
Try this out and you'll see that another issue will be exposed:

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
To understand this issue we need to take a look at the [GreetingBean](migrate/blob/master/ejb/src/main/java/se/rl/migrate/ejb/GreeterBean.java)
Notice how the GreetingBean uses _se.rl.migrate.Version_. This class is packages in a separate jar file and can be found 
in the root of the migrate.ear. Since this jar is not located in the ear's lib director, which is specified using `libraray_directory`
element in _META-INF/application.xml AS7 has no knowledge of this class. 
To make AS7, and other containers for that matter, aware of this class we need to update the ejb.jar's MANIFEST.

Again, open ejb/build.gradle and uncomment:

	attributes 'Class-Path': "version.jar"
Now, rebuild and deploy the application. Then retry entering a name and pressing the send button. 
If all goes well you should see somethings similar to the following in the server console:

    INFO [se.rl.migrate.ejb.GreeterBean] (Thread-4 (group:HornetQ-client-global-threads-780135981)) [1.0] Hello Daniel


    

    

