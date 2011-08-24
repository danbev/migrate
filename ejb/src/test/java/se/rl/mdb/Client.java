package se.rl.mdb;

import org.hornetq.api.core.Message;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ServerLocatorImpl;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;


public final class Client {
    
    private Client() {
    }
    
    public static void main(final String[] ignored) throws Exception {
        final TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName());
        transportConfiguration.getParams().put("port", 5445);
        final ServerLocatorImpl serverLocator = new ServerLocatorImpl(false, transportConfiguration);
        final ClientSessionFactory sessionFactory = serverLocator.createSessionFactory();
        final ClientSession session = sessionFactory.createSession();
        session.start();
        final ClientProducer producer = session.createProducer("jms.queue.GreeterQueue");
        final ClientMessage message = session.createMessage(Message.TEXT_TYPE, false);
        message.getBodyBuffer().writeString("Fletch");
        producer.send(message);
        
        System.out.println("Sent message [" + message + "]");
        Thread.sleep(2000);
        
        producer.close();
        session.close();
        sessionFactory.close();
    }
}
