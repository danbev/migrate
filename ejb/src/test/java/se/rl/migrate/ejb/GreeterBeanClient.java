package se.rl.migrate.ejb;

import java.security.Security;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.sasl.JBossSaslProvider;

/**
 * A remote EJB client that uses JNDI to look up the bean and invoke a method.
 * 
 * Note: the jboss-ejb-client.properites file is located in src/test/resources.
 * 
 * @author Daniel Bevenius
 *
 */
public class GreeterBeanClient {
    
    public static void main(final String[] args) throws Exception {
        Security.addProvider(new JBossSaslProvider());
        final String beanName = assembleJndiName();
        System.out.println("Lookup name: " + beanName);
        final Greeter greeter = jndiLookup(beanName);
        final String response = greeter.greet("Fletch");
        System.out.println(response);
    }
    
    private static Greeter jndiLookup(final String jndiName) throws Exception {
        Context context = null;
        try {
            final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            context =  new InitialContext(jndiProperties);
            return (Greeter) context.lookup(jndiName);
        } finally {
            context.close();
        }
    }
    
    private static String assembleJndiName() {
        final StringBuilder ejbName = new StringBuilder();
        ejbName.append("ejb:").append("migrate/").append("ejb/");
        ejbName.append("/"); //Destinct name
        ejbName.append(GreeterBean.class.getSimpleName());
        ejbName.append("!").append(GreeterRemote.class.getName());
        return ejbName.toString();
    }
    
}
