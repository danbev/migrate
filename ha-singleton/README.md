# JBoss SingletonService example
This project contains a JBoss MSC ServiceActivator that registers a singleton MSC service. The intention
of this is to demonstrate the ability having a service that is singleton in a cluster, that is will only be
active on a single node in the cluster.

## Building

    gradle build
    
    
## Starting a JBoss AS 7 Cluster
### Start the first node:

    $./standalone.sh -c standalone-ha.xml -Djboss.node.name=one Djboss.server.data.dir=/tmp/one
    
### Start the seconde node:
    $./standalone.sh -c standalone-ha.xml -Djboss.node.name=two -Djboss.server.data.dir=/tmp/two -Djboss.socket.binding.port-offset=100
    
## Deploying
Use any of the management interfaces for AS7 to deploy the jar which is available in _target/libs_

After deploying you'll see the following in both of the servers consoles:

    11:38:04,650 INFO  [se.rl.msc.SampleService] (MSC service thread 1-1) activating service singleton.sampleService

## Testing the singleton
In one of the servers consoles you see somethings like this:

    11:38:03,764 INFO  [se.rl.msc.SampleService] (pool-28-thread-1) [one] start
    
If you kill this server, using CTRL+C, you should see the service being started on the second node:
    
    11:47:44,879 INFO  [se.rl.msc.SampleService] (OOB-19,null) [two] start
Now, you can do the same and start up the first node and then take down the second etc.

## Undeploying
Using CLI:

    [standalone@localhost:9999 /] /deployment=service-activator.jar:undeploy
	{"outcome" => "success"}
    
  



