package jms;

import java.util.HashMap;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import utils.Logging;
import utils.Props;
import utils.Utilities;

public class WriteToQueue 
{
	private final Props props;
    private final Logging logging;
    private static final String JMS_FACTORY = "java:/ConnectionFactory";
    private Context context;
    private ConnectionFactory connectionFactory;
    private Queue queue;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private TextMessage textMessage;

    public WriteToQueue(Props props, Logging logging) {
        this.props = props;
        this.logging = logging;
    }

    public boolean writeToRequestQueue(String queueName, HashMap dataMap, String messageID) {
        boolean writeToQueue = false;
        
        try {
            Properties properties = new Properties();

            context = new InitialContext(properties);
            connectionFactory = (ConnectionFactory) context.lookup(JMS_FACTORY);
            queue = (Queue) context.lookup(queueName);
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            connection.start();

            logging.applicationLog(Utilities.logPreString() + "\n"
                    + "QUEUE CONNECTION STARTED:- " + queueName + " MessageID:- " + messageID, "", "2");
            
            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(dataMap);
            objectMessage.setJMSCorrelationID(messageID);
            
            textMessage = session.createTextMessage(dataMap.toString());
            messageProducer.send(objectMessage);
            writeToQueue = true;
            logging.applicationLog(Utilities.logPreString() + "\n"
                    + "QUEUE CONNECTION MESSAGE SENT:- " + queueName + " MessageID:- " + messageID, "", "8");

        } catch (NamingException namingException) {
            logging.applicationLog(Utilities.logPreString() + ""
                    + "\nERROR: NAMING EXCEPTION: " + namingException.getMessage() + queueName + " MessageID:- " + messageID, "", "99");
        } catch (JMSException exception) {
            logging.applicationLog(Utilities.logPreString() + ""
                    + "\nERROR: JMS EXCEPTION: " + exception.getMessage() + queueName + " MessageID:- " + messageID, "", "99");
        } catch (Exception exception) {
            logging.applicationLog(Utilities.logPreString() + ""
                    + "\nERROR: EXCEPTION: " + exception.getMessage() + queueName + " MessageID:- " + messageID, "", "99");
        }

        releaseConnections();
        return writeToQueue;
    }

    public void releaseConnections() {
        try {
            messageProducer.close();
            session.close();
            connection.close();
            queue = null;
            connectionFactory = null;
            context = null;
        } catch (JMSException exception) {
            logging.applicationLog(Utilities.logPreString() + ""
                    + "\nERROR: JMS EXCEPTION: " + exception.getMessage(), "", "99");
        }

    }
}
