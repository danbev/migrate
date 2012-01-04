package se.rl.migrate.ejb;

import java.security.Security;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.sasl.JBossSaslProvider;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * A remote EJB client that uses JNDI to look up the bean and invoke a method.
 * 
 * Note: the jboss-ejb-client.properites file is located in src/test/resources.
 * 
 * @author Daniel Bevenius
 *
 */
public class GreeterBeanClient {
    
    @BeforeClass
    public static void setup() {
        Security.addProvider(new JBossSaslProvider());
    }

    @Test
    @Ignore
    public void invoke() throws Exception {
        final String beanName = assembleJndiName();
        final Greeter greeter = jndiLookup(beanName);
        greeter.greet("Hello Hello, Clarice");
    }
    
    private Greeter jndiLookup(final String jndiName) throws Exception {
        Context context = null;
        try {
            context = createContext();
            return (Greeter) context.lookup(jndiName);
        } finally {
            context.close();
        }
    }
    
    private String assembleJndiName() {
        final StringBuilder ejbName = new StringBuilder();
        ejbName.append("ejb:").append("migrate/").append("ejb/");
        ejbName.append("/"); //Destinct name
        ejbName.append(GreeterBean.class.getSimpleName());
        ejbName.append("!").append(GreeterRemote.class.getName());
        return ejbName.toString();
    }
    
    private Context createContext() throws NamingException {
        final Hashtable<String,String> jndiProperties = new Hashtable<String,String>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        return new InitialContext(jndiProperties);
    }

}
