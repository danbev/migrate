package se.rl.migrate.mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import se.rl.migrate.ejb.GreeterLocal;

@MessageDriven(name = "GreeterMDB", 
    activationConfig = { 
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/GreeterQueue")})
public class GreeterMDB implements MessageListener {
    
    @EJB 
    private GreeterLocal greeter;

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
