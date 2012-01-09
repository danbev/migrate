package se.rl.migrate.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.*;

import se.rl.migrate.Version;

@Stateless 
public class GreeterBean implements GreeterLocal, GreeterRemote {
    
    private Logger log = Logger.getLogger(GreeterBean.class);
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void logVersion() {
        log.info("Greeter bean " + Version.getVersion() + " constructed");
        
    }

    public String greet(final String name) {
        final GreeterEntity greeter = em.find(GreeterEntity.class, name);
        String response;
        if (greeter == null) {
	        response = "Hello " + name;
	        log.info(response);
	        em.persist(new GreeterEntity(name));
        }
        else {
            response = "We have already greeted you " + name;
	        log.info("We have already greeted you " + name);
        }
        return response;
    }
}
