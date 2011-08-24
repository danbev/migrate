package se.rl.migrate.ejb;

import javax.ejb.Stateless;
import org.apache.log4j.*;

import se.rl.migrate.Version;

@Stateless
public class GreeterBean implements GreeterLocal, GreeterRemote {
    
    private Logger log = Logger.getLogger(GreeterBean.class);

    public void greet(String name) {
        log.info("[" + Version.getVersion() + "] Hello " + name);
    }
}
