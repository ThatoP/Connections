package utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import javax.naming.directory.Attributes;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLBreaker extends DefaultHandler
{
	String responseCode, tmpValue, request, requestType = "";
    HashMap<String, String> myHashMap = new HashMap();
    StringBuffer stringBuffer = new StringBuffer();
    
    boolean inerrorCode, inerrorDescription, infaultMessageList, currentElement = false;

    public XMLBreaker(String request, String requestType) {
        this.request = request;
        this.requestType = requestType;
    }
    
    public void breakXML() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(new InputSource(new StringReader(request)), this);
        } catch (SAXException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
        } 
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        stringBuffer.setLength(0);
        
        if(qName.equalsIgnoreCase("Token")){
            currentElement = true;
        }
        if(qName.equalsIgnoreCase("errorCode")){
            inerrorCode = true;
        }
        if(qName.equalsIgnoreCase("errorDescription")){
            inerrorDescription = true;
        }
        if(qName.equalsIgnoreCase("faultMessageList")){
            infaultMessageList = true;
        }
    }
  
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //tmpValue = new String(ch, start, length);
        if (currentElement) {
            stringBuffer.append(new String(ch, start, length));
        } else {
            tmpValue = new String(ch, start, length);
        }
    }
    
     @Override
    public void endElement(String uri, String localName, String qName) {        
        if (requestType.equalsIgnoreCase("login")) {
            if(currentElement){
                tmpValue = stringBuffer.toString();
            }
            myHashMap.put(qName, tmpValue);
            currentElement = false;
        } else if (requestType.equalsIgnoreCase("recharge")) {
            if (inerrorCode) {
                myHashMap.put("messageCode", tmpValue);
                inerrorCode = false;
            }
            if (inerrorDescription) {
                myHashMap.put("message", tmpValue);
                inerrorDescription = false;
            }
        } else if(requestType.equalsIgnoreCase("fail")){
            if(infaultMessageList){
                myHashMap.put(qName, tmpValue);
                
                if(qName.equalsIgnoreCase("faultMessageList")){
                    infaultMessageList = false;
                }
            }
        }
    }

    public HashMap<String, String> getResponse() {
        return myHashMap;
    } 
}
