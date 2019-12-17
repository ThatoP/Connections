package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

public class Logging 
{
	 private final String LOGS_PATH;
	    
	    public Logging(String LOGS_PATH){
	        this.LOGS_PATH = LOGS_PATH;
	    }
	    
	    public void applicationLog(String details, String uniqueId, String logLevel){
	        Date now = new Date();
	        
	        String typeOfLog = "";
	        
	        if (logLevel.equals("1")){
	            typeOfLog = "LoginRequest";
	        } else if(logLevel.equals("2")){
	            typeOfLog = "Application";
	        } else if(logLevel.equals("3")){
	            typeOfLog = "Database";
	        } else if(logLevel.equals("4")){
	            typeOfLog = "FromApi";
	        } else if(logLevel.equals("5")){
	            typeOfLog = "ToApi";
	        } else if(logLevel.equals("6")){
	            typeOfLog = "Data";
	        } else if (logLevel.equals("7")){
	            typeOfLog = "ReadMsgQueue";
	        } else if (logLevel.equals("8")){
	            typeOfLog = "WriteMsgQueue";
	        } else if (logLevel.equals("99")){
	            typeOfLog = "Exceptions";
	        } else {
	            typeOfLog = "Others";
	        }
	        
	       Date today  = Calendar.getInstance().getTime();
	       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	       String logDate = formatter.format(today);
	       SimpleDateFormat logTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	       String logTime = logTimeFormatter.format(today);
	       
	       File dir = new File(LOGS_PATH+"/"+typeOfLog+"/"+logDate);
	       BufferedWriter writer = null;
	       
	       if (dir.exists()){
	           dir.setWritable(true);
	       } else {
	           dir.mkdirs();
	           dir.setWritable(true);
	       }
	       
	       String number = "";
	       int max = 100000;
	       
	       try{
	           if(uniqueId.equals("")){
	               String minimum = "5";
	                number = minimum + (int) (Math.random() * max);
	                uniqueId = number;

	           }else {
	               number = "10" + (int) (Math.random() * max);
	           }
	           
	           //add more code using the Utils class
	           String fileName = "";

	            File[] fileList = dir.listFiles();

	            if (fileList.length > 0) {
	                for (int i = 0; i < fileList.length; i++) {
	                    if (fileList[i].length() < 25000000) {
	                        fileName = "/" + fileList[i].getName();
	                    } else {
	                        fileName = "/" + Utilities.dateFormat.get().format(now) + "-" + uniqueId + ".log";
	                    }
	                }
	            } else {
	                fileName = "/" + Utilities.dateFormat.get().format(now) + "-" + number + ".log";
	            }


	            writer = new BufferedWriter(new FileWriter(dir + fileName, true));
	            writer.write("--------------------");
	            writer.newLine();
	            writer.write(logTime + " ~ " + details);
	            writer.newLine();
	       }catch(Exception e){
	           java.util.logging.Logger.getLogger(Logging.class.getSimpleName()).log(Level.SEVERE, "Exception Occurred:- "+e.getMessage(), e);
	       }
	       finally{
	           try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (IOException ex) {
	                java.util.logging.Logger.getLogger(Logging.class.getSimpleName()).log(Level.SEVERE, "IOException Occurred:- "+ex.getMessage(), ex);
	            }
	       }
	    }
}
