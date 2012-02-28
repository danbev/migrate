package se.rl.msc;

import org.apache.log4j.Logger;
import org.jboss.as.clustering.singleton.SingletonService;
import org.jboss.as.clustering.singleton.election.NamePreference;
import org.jboss.as.clustering.singleton.election.PreferredSingletonElectionPolicy;
import org.jboss.as.clustering.singleton.election.SimpleSingletonElectionPolicy;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.as.server.ServerEnvironment;
import org.jboss.as.server.ServerEnvironmentService;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;

/**
 * A MSC ServiceActivator which is enabled by the runtime when AS scans the 
 * deployment for a META-INF/services/org.jboss.msc.service.ServiceActivator.
 * </p>
 * The service that this activator activates is a {@link SingletonService} which will only be active in
 * one node in the cluster.
 * 
 * @author Daniel Bevenius
 *
 */
public class StartupActivator implements ServiceActivator {
    
    private static final ServiceName SERVICE_NAME = ServiceName.of("singleton", "sampleService");
    private Logger log = Logger.getLogger(SampleService.class);

    public void activate(final ServiceActivatorContext sac) throws ServiceRegistryException {
        if (isRegistered(sac, SERVICE_NAME)) {
            return;
        }
        
        log.info("activating " + SERVICE_NAME);
        // Create an instance of our service
        final SampleService sampleService = new SampleService();
        // Decorate our service in a SingletonService 
        final SingletonService<String> singleton = new SingletonService<String>(sampleService, SERVICE_NAME);
        // Install the singleton       
        final ServiceController<String> controller = singleton.build(CurrentServiceContainer.getServiceContainer())
            .addDependency(ServerEnvironmentService.SERVICE_NAME, ServerEnvironment.class, sampleService.getServerEnvironment())
            .install();
        controller.setMode(ServiceController.Mode.ACTIVE);
    }
    
    private boolean isRegistered(final ServiceActivatorContext sac, ServiceName serviceName) {
        return sac.getServiceRegistry().getService(SERVICE_NAME) != null;
    }
}
