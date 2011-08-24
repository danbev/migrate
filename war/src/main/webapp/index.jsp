<%@ page import="javax.annotation.*, javax.naming.*, javax.jms.*" %>
<%!
@Resource(name = "ConnectionFactory")
ConnectionFactory connectionFactory;

@Resource(name = "java:/queue/GreeterQueue")
Queue queue;

Connection connection;
Session jmsSession;
MessageProducer producer;

@PostConstruct
public void jspInit()
{
    try
    {
	    Context context = new InitialContext();
	    connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
	    connection = connectionFactory.createConnection();
	    connection.start();
	    queue = (Queue) context.lookup("java:/queue/GreeterQueue");
	    jmsSession = connection.createSession(false, DeliveryMode.PERSISTENT);
	    producer = jmsSession.createProducer(queue);
	}
	catch (Exception e) 
	{
	   e.printStackTrace();
    }
}

@PreDestroy
public void jspDestroy() 
{
    try 
    {
        producer.close();
        jmsSession.close();
        connection.close();
    }
    catch (Exception e)
    {
       e.printStackTrace();
    }
}
%>
<% 
    if(request.getParameter("input") != null) {
        producer.send(jmsSession.createTextMessage(request.getParameter("input")));
    %>
        Sent '<%= request.getParameter("input") %>' to <%= queue.getQueueName() %>
    <%
    }
%>

    <FORM NAME="form1" METHOD="POST">
        <INPUT TYPE="HIDDEN" NAME="sendMsg">
        <INPUT TYPE="TEXT" NAME="input">
        <INPUT TYPE="BUTTON" VALUE="Send Message" ONCLICK="button1()">
    </FORM>

    <SCRIPT LANGUAGE="JavaScript">
        <!--
        function button1()
        {
            form1.submit()
        }    
        // --> 
    </SCRIPT>
