package se.rl.ee;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;

@Singleton
@Startup
public class EEService {
    
    private Logger log = Logger.getLogger(EEService.class);
    
    @PostConstruct
    public void start() {
        log.info("start");
    }
    
    @PreDestroy
    public void stop() {
        log.info("stop");
    }

}
