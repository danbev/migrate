package se.rl.msc;

import org.apache.log4j.Logger;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * Sample Service is a MSC service which could either implement the functionality 
 * of delegate to a pre-existing class when the start and stop methods are called.
 * 
 * @author Daniel Bevenius
 *
 */
public class SampleService implements Service<String> {
    
    private final InjectedValue<ServerEnvironment> serverEnvironment = new InjectedValue<ServerEnvironment>();
    private Logger log = Logger.getLogger(SampleService.class);
    
    public InjectedValue<ServerEnvironment> getServerEnvironment() {
        return serverEnvironment;
    }

    public String getValue() throws IllegalStateException, IllegalArgumentException {
        return "some string...";
    }
    
    public void start(final StartContext context) throws StartException {
        log.info("[" + serverEnvironment.getValue().getNodeName() + "] start");
    }

    public void stop(final StopContext context) {
        log.info("[" + serverEnvironment.getValue().getNodeName() + "] stop");
    }

}
