package se.rl.mdb;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JNDIClient {
    
    public static void main(final String[] args) {
        Context context = null;
        Connection connection = null;
        Queue queue = null;
        Session jmsSession = null;
        MessageProducer producer = null;
        try {
            context = new InitialContext();
            final ConnectionFactory connectionFactory =  (ConnectionFactory) context.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.start();
            queue = (Queue) context.lookup("java:/queue/GreeterQueue");
            jmsSession = connection.createSession(false, DeliveryMode.PERSISTENT);
            producer = jmsSession.createProducer(queue);
            final TextMessage textMessage = jmsSession.createTextMessage("Fletch");
            producer.send(textMessage);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            safeClose(context);
            safeClose(producer);
            safeClose(jmsSession);
            safeClose(connection);
        }
    }
    
    private static void safeClose(final MessageProducer p) {
        if (p != null) {
            try {
                p.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private static void safeClose(final Session s) {
        if (s != null) {
            try {
                s.close();
            } catch (final JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private static void safeClose(final Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (final NamingException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void safeClose(final Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (final JMSException e) {
                e.printStackTrace();
            }
        }
    }

}
