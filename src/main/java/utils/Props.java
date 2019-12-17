package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Props 
{
	private transient Properties props;

    /**
     * A list of any errors that occurred while loading the properties.
     */
    private transient List<String> loadErrors;

    private transient final String error1 = "ERROR: %s is <= 0 or may not have been set";
    private transient final String error2 = "ERROR: %s may not have been set";
    private static final String PROPS_FILE = "Desktop\test.properties";

    private transient String databaseContextURL;
    private transient String logsPath;

    private transient String TYLERSOFT_ESB_MDB_REQUEST_QUEUE;
    private transient String TYLERSOFT_ESB_MDB_RESPONSE_QUEUE;

    private transient String TYLERSOFT_SMS_REQUEST_QUEUE;
    private transient String TYLERSOFT_SERVLET_RESPONSE_QUEUE;
    private transient String PROVIDER_URL;
    private transient String REMOTE_PROVIDER_URL;

    private transient String SECURITY_PRINCIPAL;
    private transient String SECURITY_CREDENTIALS;

    private transient String SALT;
    private transient String SERVLET_RESPONSE_TIMEOUT;
    private transient String SERVLET_ACCESS_KEY;
    private transient String CYBER_URL;
    /**
     * Instantiates a new Props.
     */
    public Props() {
        loadProperties(PROPS_FILE);
    }

    /**
     * Load properties.
     *
     * @param propsFileName the props file name
     */
    public void loadProperties(final String propsFileName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propsFileName);

            props = new Properties();

            props.load(inputStream);

            databaseContextURL = readString("DATABASE_CONTEXT_URL");

            logsPath = readString("LOGS_PATH");

            TYLERSOFT_ESB_MDB_REQUEST_QUEUE = readString("TYLERSOFT_ESB_MDB_REQUEST_QUEUE");
            TYLERSOFT_ESB_MDB_RESPONSE_QUEUE = readString("TYLERSOFT_ESB_MDB_RESPONSE_QUEUE");

            TYLERSOFT_SERVLET_RESPONSE_QUEUE = readString("TYLERSOFT_SERVLET_RESPONSE_QUEUE");
            PROVIDER_URL = readString("PROVIDER_URL");
            REMOTE_PROVIDER_URL = readString("REMOTE_PROVIDER_URL");
            CYBER_URL = readString("CYBER_URL");
            SECURITY_PRINCIPAL = readString("SECURITY_PRINCIPAL");
            SECURITY_CREDENTIALS = readString("SECURITY_CREDENTIALS");
            
            TYLERSOFT_SMS_REQUEST_QUEUE = readString("TYLERSOFT_SMS_REQUEST_QUEUE");
            SALT = readString("SALT");
            SERVLET_RESPONSE_TIMEOUT = readString("SERVLET_RESPONSE_TIMEOUT");
            SERVLET_ACCESS_KEY = readString("SERVLET_ACCESS_KEY");

        } catch (Exception ex) {
            System.err.print("ERROR: Failed to load properties file.\nCause: " + ex.getMessage());
            Logger.getLogger(Props.class.getName()).log(Level.SEVERE, "ERROR: Failed to load properties file.\nCause: \n", ex);

        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Props.class.getName()).log(Level.SEVERE, "ERROR: Failed to load properties file.\nCause: \n", ex);
            }
        }
    }
    
     /**
     * Read string string. - This function reads a String from the properties
     * file
     *
     * @param propertyName the property name
     * @return the string
     */
    public String readString(String propertyName) {
        String property = props.getProperty(propertyName);
        if (property.isEmpty()) {
            getLoadErrors().add(String.format(error2, propertyName));
        }
        return property;
    }

    /**
     * Gets load errors.
     *
     * @return the load errors
     */
    public List<String> getLoadErrors() {
        return loadErrors;
    }

    /**
     * Get Database Context URL
     *
     * @return the database context URL
     */
    public String getDatabaseContextURL() {
        return databaseContextURL;
    }

    /**
     * Gets logs path.
     *
     * @return the logs path
     */
    public String getLogsPath() {
        return logsPath;
    }
     public String getCYBER_URL() {
        return CYBER_URL;
    }

    public String getESB_MDB_REQUEST_QUEUE() {
        return TYLERSOFT_ESB_MDB_REQUEST_QUEUE;
    }

    public void setESB_MDB_REQUEST_QUEUE(String ESB_MDB_REQUEST_QUEUE) {
        this.TYLERSOFT_ESB_MDB_REQUEST_QUEUE = TYLERSOFT_ESB_MDB_REQUEST_QUEUE;
    }

    public String getESB_MDB_RESPONSE_QUEUE() {
        return TYLERSOFT_ESB_MDB_RESPONSE_QUEUE;
    }

    public void setESB_MDB_RESPONSE_QUEUE(String ESB_MDB_RESPONSE_QUEUE) {
        this.TYLERSOFT_ESB_MDB_RESPONSE_QUEUE = TYLERSOFT_ESB_MDB_RESPONSE_QUEUE;
    }

    public String getPROVIDER_URL() {
        return PROVIDER_URL;
    }

    public String getREMOTE_PROVIDER_URL() {
        return REMOTE_PROVIDER_URL;
    }

    public String getSECURITY_PRINCIPAL() {
        return SECURITY_PRINCIPAL;
    }

    public String getSECURITY_CREDENTIALS() {
        return SECURITY_CREDENTIALS;
    }

    public String getSERVLET_RESPONSE_QUEUE() {
        return TYLERSOFT_SERVLET_RESPONSE_QUEUE;
    }

    public void setSERVLET_RESPONSE_QUEUE(String SERVLET_RESPONSE_QUEUE) {
        this.TYLERSOFT_SERVLET_RESPONSE_QUEUE = TYLERSOFT_SERVLET_RESPONSE_QUEUE;
    }
    
    public String getSMS_REQUEST_QUEUE() {
        return TYLERSOFT_SMS_REQUEST_QUEUE;
    }

    public void setSMS_REQUEST_QUEUE(String SMS_REQUEST_QUEUE) {
        this.TYLERSOFT_SMS_REQUEST_QUEUE = TYLERSOFT_SMS_REQUEST_QUEUE;
    }

    public void setSALT(String SALT) {
        this.SALT = SALT;
    }

    public long getSERVLET_RESPONSE_TIMEOUT() {
        return Long.parseLong(SERVLET_RESPONSE_TIMEOUT);
    }

    public void setSERVLET_RESPONSE_TIMEOUT(String SERVLET_RESPONSE_TIMEOUT) {
        this.SERVLET_RESPONSE_TIMEOUT = SERVLET_RESPONSE_TIMEOUT;
    }

    public String getSERVLET_ACCESS_KEY() {
        return SERVLET_ACCESS_KEY;
    }

    public void setSERVLET_ACCESS_KEY(String SERVLET_ACCESS_KEY) {
        this.SERVLET_ACCESS_KEY = SERVLET_ACCESS_KEY;
    }
}
