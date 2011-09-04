package se.rl.jmx;

import org.apache.log4j.Logger;

public class TestService implements TestServiceMBean {
    
    private Logger log = Logger.getLogger(TestService.class);
    
    public TestService() {
        System.out.println("created TestBean");
    }
    
    public void start() {
        log.info("start");
    }
    
    public void stop() {
        log.info("stop");
    }

}
