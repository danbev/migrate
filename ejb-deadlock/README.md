# ejb-deadlock
The sole purpose of this project is to reproduce a deadlock when calling an EJB running on JBoss AS 7.1.1.Final from 
a remote standalone client.

# Issue description
The use case is a remote EJB client that upon calling an EJB gets an ClassNotFoundException when deserializing the object graph
returned from the server. When this occurs the client "freezes" but can be interrupted by CTRL+C, but the server will 
deadlock and will not respond and is not able to be shutdown with out killing the server process. 

# Thread dump
Can be found in 'thread-dump.txt'

# Building

    gradle build 

# Installing to AS7
Copy target/lib/ejb-deadlock.jar to $AS7/standalone/deployments

# Running the client
To run the client manually from Eclipse simply run:

    gradle exec

This will run se.rl.deadlock.CustomerBeanClient which can also be called from within and IDE but will
require following VM argument need to be given: 

    -javaagent:lib/byteman.jar=script:src/test/resources/customer-bean-client.btm
    
# Generateing a thread dump

    jstack -l 61276 > thread-dump.txt



