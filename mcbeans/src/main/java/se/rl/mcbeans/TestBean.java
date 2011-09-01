package se.rl.mcbeans;

import org.apache.log4j.Logger;

public class TestBean {
    
    private Logger log = Logger.getLogger(TestBean.class);
    
    public TestBean() {
        System.out.println("created TestBean");
    }
    
    public void start() {
        log.info("start");
    }
    
    public void stop() {
        log.info("stop");
    }

}
