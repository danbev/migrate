package se.rl.msc;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * Sample Service is a MSC service which could either implement the functionality 
 * of delegate to a pre-existing class when the start and stop methods are called.
 * 
 * @author Daniel Bevenius
 *
 */
public class SampleService implements Service<Void>{
    
    @Override
    public Void getValue() throws IllegalStateException, IllegalArgumentException {
        return null;
    }

    @Override
    public void start(StartContext context) throws StartException {
        System.out.println("SampleService start");
    }

    @Override
    public void stop(StopContext context) {
        System.out.println("SampleService stop");
    }

}
