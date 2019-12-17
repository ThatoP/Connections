package jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import utils.Logging;
import utils.Props;
import utils.Utilities;

public class ReadFromQueue 
{
	private final Props props;
    private final Logging logging;

    private static final String JMS_FACTORY = "java:/ConnectionFactory";
    private Context initialContext;
    private QueueConnectionFactory queueConnectionFactory;
    private Queue queue;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private MessageConsumer messageConsumer;
    private QueueBrowser queueBrowser;
    private QueueReceiver queueReceiver;
    private TextMessage textMessage;

    public ReadFromQueue(Props props, Logging logging) {
        this.props = props;
        this.logging = logging;
    }

    public HashMap readfromQueue(String queueName, String correlationID) {
        String queueMessage = "";
        HashMap dataMap = new HashMap();
        try {
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            properties.put(Context.URL_PKG_PREFIXES, " org.jboss.naming:org.jnp.interfaces");
            properties.put(Context.PROVIDER_URL, props.getPROVIDER_URL());
//            properties.put(Context.SECURITY_PRINCIPAL, props.getSECURITY_PRINCIPAL());
//            properties.put(Context.SECURITY_CREDENTIALS, props.getSECURITY_CREDENTIALS());

            initialContext = new InitialContext();

            logging.applicationLog(Utilities.logPreString() + "Acquired Initial Context", "", "7");

            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(JMS_FACTORY);
            logging.applicationLog(Utilities.logPreString() + "Initialized Connection Factory.....", "", "7");

            queue = (Queue) initialContext.lookup(queueName);
            logging.applicationLog(Utilities.logPreString() + "Acquired Message Queue:- " + queueName, "", "7");

            queueConnection = queueConnectionFactory.createQueueConnection();
            logging.applicationLog(Utilities.logPreString() + "Acquired connection for request Queue:- " + queueName, "", "7");

            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            logging.applicationLog(Utilities.logPreString() + "Acquired Session for Queue.", "", "7");

            queueBrowser = queueSession.createBrowser(queue, "JMSCorrelationID = '" + correlationID + "'");
            logging.applicationLog(Utilities.logPreString() + "Created MessageBrowser for Queue:- " + queueName, "", "7");

            Enumeration enumeration = queueBrowser.getEnumeration();
            logging.applicationLog(Utilities.logPreString() + "HAS ENUMERATION " + enumeration.hasMoreElements(), "", "7");

            while (enumeration.hasMoreElements()) {
                Message message = (Message) enumeration.nextElement();
                queueReceiver = queueSession.createReceiver(queue, "JMSCorrelationID = '" + correlationID + "'");
                ObjectMessage objectMessage = (ObjectMessage) message;
                dataMap = (HashMap) objectMessage.getObject();
                queueMessage = dataMap.toString();

                logging.applicationLog(Utilities.logPreString() + "Message From Queue:- " + queueMessage, "", "7");
                queueReceiver.receiveNoWait();
            }

            queueBrowser.close();
            dataMap.remove("CorrelationID");
            return dataMap;
        } catch (JMSException jMSException) {
            logging.applicationLog(Utilities.logPreString() + "JMSException Occurred: " + jMSException.getMessage(), "", "7");
            return dataMap;
        } catch (NamingException namingException) {
            logging.applicationLog(Utilities.logPreString() + "NamingException Occurred: " + namingException.getMessage(), "", "7");
            return dataMap;
        } finally {
            releaseResources();
        }
    }

    public void releaseResources() {
        try {
            queueSession.close();
            queueConnection.close();
            queue = null;
            queueConnectionFactory = null;
            initialContext.close();
        } catch (JMSException ex) {
            logging.applicationLog(Utilities.logPreString() + "JMSException:- Failed to release Resources: " + ex.getMessage(), "", "7");
        } catch (NamingException ex) {
            logging.applicationLog(Utilities.logPreString() + "NamingException:- Failed to release Resources: " + ex.getMessage(), "", "7");
        }

    }
}
