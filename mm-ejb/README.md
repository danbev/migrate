# mm-ejb project
The solve purpose of this project is to try to reproduce an issue with JBoss AS 7.1.0.Final.

# Issue description
The use case is a remote EJB client that upon calling an EJB gets an ClassNotFoundException and after this both the server
and the client hang.

# Building

    gradle build 

# Installing to AS7
Copy target/lib/mm-ejb.jar to $AS7/standalone/deployments

# Running the client
To run the client manually from Eclipse simply run:
se.rl.migrate.ejb.CustomerBeanClient.jar

This should be successful. To simulate the error, right click the 'mm_ejb' project and remove the 'domain' project as 
a project dependency. 
Now, rerun the client and you should get the ClassNotFoundException.

Please note that this does currently not reproduce the hanged client and server problem as this is still something that 
is being look at. 



