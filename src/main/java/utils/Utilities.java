package utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utilities 
{
	 private final Logging logger;

	    public Utilities(Logging logger) {
	        this.logger = logger;
	    }

	    /**
	     * Generate Random UUID using Java UUID Utility
	     *
	     * @return - UUID Generated
	     */
	    public static String generateUUID() {
	        return UUID.randomUUID().toString();
	    }

	    /**
	     * Utility methods to handle date
	     */
	    public static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
	        @Override
	        protected SimpleDateFormat initialValue() {
	            return new SimpleDateFormat("YY-MM-dd");
	        }
	    };

	    public static final ThreadLocal<SimpleDateFormat> monthDayFormat = new ThreadLocal<SimpleDateFormat>() {
	        @Override
	        protected SimpleDateFormat initialValue() {
	            return new SimpleDateFormat("MMDD");
	        }
	    };

	    public static final ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>() {
	        @Override
	        protected SimpleDateFormat initialValue() {
	            return new SimpleDateFormat("HHmmss");
	        }
	    };
	    public static final ThreadLocal<SimpleDateFormat> dateTimeFormat = new ThreadLocal<SimpleDateFormat>() {
	        @Override
	        protected SimpleDateFormat initialValue() {
	            return new SimpleDateFormat("MMddHHmmss");
	        }
	    };
	    
	     /**
	     * This function gets a SOAP message and converts it into a String, this
	     * enables easy logging of the message and usage in other dependant
	     * functions.
	     *
	     * @param soapMessage - The soap message prepared by the daemon or received
	     * by daemon from API
	     * @return - A string instance of the SOAP payload which maybe logged or
	     * transformed into a DOM Document to get node elements from it
	     * @throws Exception - Exception is thrown incase the function is unable to
	     * Stringify the SOAP message
	     */
	    public static String toStringSOAPMessage(SOAPMessage soapMessage) throws Exception {
	        Source source = soapMessage.getSOAPPart().getContent();
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();

	        StringWriter stringWriter = new StringWriter();
	        StreamResult streamResult = new StreamResult(stringWriter);
	        transformer.transform(source, streamResult);

	        return stringWriter.toString();
	    }
	    
	     /**
	     * This function gets a String SOAP message and converts it back into a SOAP
	     * message
	     *
	     * @param xml - the String XML payload
	     * @return - the processed SOAP message
	     * @throws SOAPException - SOAP exception is thrown if it fails to create
	     * the SOAP message
	     * @throws IOException - IO Exception thrown if it cannot get the XML string
	     * to process
	     */
	    public static SOAPMessage getSoapMessageFromString(String xml) throws SOAPException, IOException {
	        MessageFactory factory = MessageFactory.newInstance();
	        SOAPMessage message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
	        return message;
	    }
	    
	    /**
	     * Convert nodes from xml.
	     *
	     * This will receive the xml as a String and convert it to a bytStream and
	     * then into an XML file. Then it passes it to the createMap function that
	     * will iterate and get the nodes and their values
	     *
	     * @param xml the xml from the server
	     * @return the object - a Map of node and value
	     * @throws Exception the exception
	     */
	    public static Map convertNodesFromXml(String xml) throws Exception {

	        InputStream is = new ByteArrayInputStream(xml.getBytes());
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document document = db.parse(is);
	        return (Map) createMap(document.getDocumentElement());
	    }
	    
	    /**
	     * Create map.
	     *
	     * This will create a Map Object from the XML Doc that's been created and
	     * Unmarshall it into a Map. This is done by reading the XML node elements
	     * and their values to make a Map of Key(node) and value(element).
	     *
	     * @param node the node <node>nodeData</node>
	     * @return the object
	     */
	    public static Object createMap(Node node) {
	        Map<String, Object> map = new HashMap<String, Object>();
	        NodeList nodeList = node.getChildNodes();
	        for (int i = 0; i < nodeList.getLength(); i++) {
	            Node currentNode = nodeList.item(i);
	            String name = currentNode.getNodeName();
	            Object value = null;
	            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	                value = createMap(currentNode);
	            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
	                return currentNode.getTextContent();
	            }
	            if (map.containsKey(name)) {
	                Object object = map.get(name);
	                if (object instanceof List) {
	                    ((List<Object>) object).add(value);
	                } else {
	                    List<Object> objectList = new LinkedList<Object>();
	                    objectList.add(object);
	                    objectList.add(value);
	                    map.put(name, objectList);
	                }
	            } else {
	                map.put(name, value);
	            }
	        }
	        return map;
	    }
	    
	    /**
	     * This sends an echo test to the Server to ensure that we have a connection
	     * before we can make a service request on the server.
	     *
	     * @return 0 for failure and 1 for success
	     */
	    public static int pingAPIURL(String url, int timeout) {
	        // Otherwise an exception may be thrown on invalid SSL certificates:
	        url = url.replaceFirst("^https", "http");

	        int responseCode = 0;

	        try {
	            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	            connection.setConnectTimeout(timeout);
	            connection.setReadTimeout(timeout);
	            connection.setRequestMethod("HEAD");
	            responseCode = connection.getResponseCode();
	            return responseCode;
	        } catch (IOException exception) {
	            return responseCode;
	        }
	    }
	    
	    /**
	     * this function receives a string URL query and converts it into a hashmap
	     *
	     * @param query - The URL query string
	     * @return - HashMap of key and object
	     */
	    public static Map<String, String> convertStringQueryToMap(String query) throws Exception {
	        Map<String, String> result = new HashMap<String, String>();
	        for (String param : query.split("&")) {
	             String pair[] = param.split("=");
	            if (pair.length > 1) {
	                result.put(pair[0], pair[1]);
	            } else {
	                result.put(pair[0], "");
	            }
	        }
	        return result;
	    }
	    
	     public static Document createDOMDocument(String xml) throws Exception {
	        InputStream is = new ByteArrayInputStream(xml.getBytes());
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document document = db.parse(is);

	        return document;
	    }
	     
	     /**
	     * To string xml message string.
	     *
	     * This gets an XML DOM Document and converts it into a String variable
	     *
	     * @param xmlPayload the xml payload
	     * @return the string
	     * @throws Exception the exception
	     */
	    public static String toStringXMLMessage(Document xmlPayload) throws Exception {
	        DOMSource source = new DOMSource(xmlPayload);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        StringWriter stringWriter = new StringWriter();
	        StreamResult streamResult = new StreamResult(stringWriter);
	        transformer.transform(source, streamResult);

	        return stringWriter.toString().replace("\n", "");
	    }
	    
	    /**
	     * Convert nodes to map map.
	     *
	     * @param dataXML the data xml
	     * @return the map
	     */
	    public static Map<String, String> convertNodesToMap(String xmlAlias, String dataXML) {
	       // XStream xStream = new XStream();
	        //xStream.registerConverter(new MapEntryConverter());
	        //xStream.alias(xmlAlias, Map.class);

	        return null;
	    }
	    
	    /**
	     * Log pre string.
	     *
	     * @return the string
	     */
	    public static String logPreString() {
	        return "Generic Servlet | " + Thread.currentThread().getStackTrace()[2].getClassName() + " | "
	                + Thread.currentThread().getStackTrace()[2].getLineNumber() + " | "
	                + Thread.currentThread().getStackTrace()[2].getMethodName() + "() | ";
	    }
	    
	    /**
	     * Prepare xml payload string.
	     *
	     * This creates an XML String from the HashMap key value pair to be posted
	     * to API
	     *
	     * @param methodName the method name for the API eg. Airtime, BalanceEnquiry
	     * @param payloadMap the payload map to create XML with
	     * @return the string
	     */
	    public String prepareXMLPayload(String methodName, Map<String, String> payloadMap) {
	        String payload = "";
	        logger.applicationLog(logPreString() + "prepareXMLPayload() | Preparing "
	                + "payload for methodName " + methodName + " DATA MAP:- " + payloadMap, "", "6");
	        try {
	            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	            Document document = documentBuilder.newDocument();

	            Element rootElement = document.createElement(methodName);
	            document.appendChild(rootElement);

	            for (String key : payloadMap.keySet()) {
	                Element innerElement = document.createElement(key);
	                innerElement.appendChild(document.createTextNode("" + payloadMap.get(key)));
	                rootElement.appendChild(innerElement);
	            }

	            payload = toStringXMLMessage(document);

	            logger.applicationLog(logPreString() + "prepareXMLPayload() | XML DOM "
	                    + "Payload Created Successfully..\n" + payload, "", "6");

	            Map<String, String> resultMap = Utilities.convertNodesToMap("response", payload);

	            logger.applicationLog(logPreString() + "prepareXMLPayload() | RESULT MAP "
	                    + ": \n" + resultMap.toString(), "6", "6");
	        } catch (Exception ex) {
	            logger.applicationLog(logPreString() + "Exception occurred during XML build,"
	                    + "ERROR:- " + ex.getMessage(), "", "2");
	            ex.printStackTrace();
	        }
	        return payload;
	    }
	    
	    //format the linked accounts,to remove the blank spaces
	    public static String formatLinkedAccounts(String accounts, String mwalletAccount) {
	        String response = "";
	        String[] accs = accounts.replace("|", "<@@>").split("<@@>");

	        for (String acc : accs) {
	            if (acc.length() >= 10) {
	                response += acc + "|";
	            }
	        }
	        if (response.length() < 10) {
	            response = mwalletAccount;
	        }
	        return response;
	    }
	    
	    public static String getField48Information(String field39Status) {
//	        String statusDescription = ResponseCodes.responseCodesMap.get(field39Status);
//	        return "{status=" + field39Status + ",statusDescription=" + statusDescription + "}";
	    return "";
	    }

	    /**
	     * The type Map entry converter.
	     */
	    public static class MapEntryConverter   {

	        public boolean canConvert(Class clazz) {
	            return AbstractMap.class.isAssignableFrom(clazz);
	        }
	    }
	    
	     public static String generateCustomerPIN() throws Exception {
	        Random rand = new Random();

	        int max = 10000;
	        int min = 1000;
	        int randomNum = rand.nextInt((max - min) + 1) + min;
	        String random = String.valueOf(randomNum);
	        
	        return random;
	    }
}
