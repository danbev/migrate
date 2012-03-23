package se.rl.deadlock;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;


/**
 * A remote EJB client that uses JNDI to look up the bean and invoke a method.
 * </p>
 * 
 * To run this class from within a IDE the following VM argument need to be given: 
 * -javaagent:lib/byteman.jar=script:src/test/resources/customer-bean-client.btm
 * 
 * @author Daniel Bevenius
 */
public class CustomerBeanClient {
    
    public static void main(String args[]) throws Exception {
        setEJBClientContext();
        final Customer customer = jndiLookup("ejb:/ejb-deadlock/CustomerBean!se.rl.deadlock.CustomerRemote");
        final CustomerImpl response = customer.getCustomer(10000);
        System.out.println(response);
    }
    
    private static void setEJBClientContext() {
        final Properties properties = new Properties();
        properties.put("endpoint.name", "client-endpoint");
        properties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
        properties.put("remote.connections", "default");
        properties.put("remote.connection.default.host", "localhost");
        properties.put("remote.connection.default.port", "4447");
        properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
        properties.put("remote.connection.default.username", "test");
        properties.put("remote.connection.default.password", "password");
        //properties.put("remote.connection.default.channel.options.org.jboss.remoting3.RemotingOptions.TRANSMIT_WINDOW_SIZE", "2147483647");
        //properties.put("remote.connection.default.channel.options.org.jboss.remoting3.RemotingOptions.RECEIVE_WINDOW_SIZE", "2147483647");
        final EJBClientConfiguration config = new PropertiesBasedEJBClientConfiguration(properties);
        final ConfigBasedEJBClientContextSelector selector = new ConfigBasedEJBClientContextSelector(config);
        EJBClientContext.setSelector(selector);
    }

    private static Customer jndiLookup(final String jndiName) throws Exception {
        Context context = null;
        try {
            final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            context =  new InitialContext(jndiProperties);
            return (Customer) context.lookup(jndiName);
        } finally {
            context.close();
        }
    }
    
}
