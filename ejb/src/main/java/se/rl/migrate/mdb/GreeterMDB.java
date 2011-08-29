package se.rl.migrate.mdb;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import se.rl.migrate.ejb.GreeterLocal;
import se.rl.util.SomeUtil;

@MessageDriven(name = "GreeterMDB", 
    activationConfig = { 
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/GreeterQueue")})
public class GreeterMDB implements MessageListener {
    
    //@EJB (lookup="migrate/GreeterBean/local")
    @EJB (lookup="java:module/GreeterBean!se.rl.migrate.ejb.GreeterLocal")
    private GreeterLocal greeter;
    
    @PostConstruct
    public void logConstruction() {
        SomeUtil.logMessage("Initializing GreeterMDB");
    }

    public void onMessage(final Message message) {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            try {
	            greeter.greet(textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
