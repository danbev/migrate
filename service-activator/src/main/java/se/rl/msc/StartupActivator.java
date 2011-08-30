package se.rl.msc;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;

/**
 * A MSC ServiceActivator which is enabled by the runtime when AS scan the 
 * deployment for a META-INF/services/org.jboss.msc.service.ServiceActivator.
 * 
 * @author Daniel Bevenius
 *
 */
public class StartupActivator implements ServiceActivator {
    
    private static final ServiceName SERVICE_NAME = ServiceName.of("sample", "sampleService");

    @Override
    public void activate(final ServiceActivatorContext sac) throws ServiceRegistryException {
        System.out.println("StartupActivator activate");
        sac.getServiceTarget().addService(SERVICE_NAME, new SampleService()).install();
    }

}
