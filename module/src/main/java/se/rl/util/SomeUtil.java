package se.rl.util;

import org.apache.log4j.Logger;

public class SomeUtil {
    
    private static final Logger log = Logger.getLogger(SomeUtil.class);
    
    private SomeUtil() {
        
    }
    
    public static void logMessage(final String msg) {
        log.info("Message : " + msg);
    }

}
