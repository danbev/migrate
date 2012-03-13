package se.rl.migrate.ejb;

import java.security.Security;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.sasl.JBossSaslProvider;

import se.rl.migrate.domain.customer.CustomerImpl;

/**
 * A remote EJB client that uses JNDI to look up the bean and invoke a method.
 * 
 * Note: the jboss-ejb-client.properites file is located in src/test/resources.
 * 
 * @author Daniel Bevenius
 *
 */
public class CustomerBeanClient {
    
    public static void main(final String[] args) throws Exception {
        Security.addProvider(new JBossSaslProvider());
        final String beanName = assembleJndiName();
        System.out.println("Lookup name: " + beanName);
        final Customer customer = jndiLookup(beanName);
        final CustomerImpl response = customer.getCustomer();
        System.out.println(response);
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
    
    private static String assembleJndiName() {
        final StringBuilder ejbName = new StringBuilder();
        ejbName.append("ejb:").append("/mm-ejb");
        ejbName.append("/"); //Destinct name
        ejbName.append(CustomerBean.class.getSimpleName());
        ejbName.append("!").append(CustomerRemote.class.getName());
        return ejbName.toString();
    }
    
}
