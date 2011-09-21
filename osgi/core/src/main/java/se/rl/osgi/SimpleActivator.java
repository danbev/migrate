package se.rl.osgi;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SimpleActivator implements BundleActivator
{
    private Logger log = Logger.getLogger(SimpleActivator.class);
    
    public void start(final BundleContext context) throws Exception
    {
        log.info("SimpleActivator start");
    }

    public void stop(final BundleContext context) throws Exception
    {
        log.info("SimpleActivator stop");
    }

}
