package org.apolline.configuration;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.util.*;

/**
 * This class find all datalogger properties in a file .properties
 */
public class Configuration{

    /**
     * Constant key for get the connection timeout
     */
    public static final String TIMEOUT = "timeout";

    /**
     * Constant key for get the baud of the serial port
     */
    public static final String BAUD = "baud";

    /**
     * Constant key for get the bits numbers of the serial port
     */
    public static final String BITS = "bits";

    /**
     * Constant key for get the stop bits of the serial port
     */
    public static final String STOP_BITS = "stopBits";

    /**
     * Constant key for get the parity of the serial port
     */
    public static final String PARITY = "parity";

    /**
     * Constant key for get the name of the serial port
     */
    public static final String DEVICE = "device";

    /**
     * Constant key for get the type of datalogger
     */
    public static final String TYPE = "type";

    /**
     * Constant key for get informations about sensors
     */
    public static final String SENSORS="sensors";

    /**
     * Constant key for get time for filter data received
     */
    public static final String TIME_FILTER="timeFilter";

    /**
     * Default value for the name of the serial port
     */
    private static final String DEFAULT_DEVICE="/dev/ttyUSB0";

    /**
     * Default value for the connection timeout (in milliseconds)
     */
    private static final Integer DEFAULT_TIMEOUT=10000;

    /**
     * Default value for the baud
     */
    private static final Integer DEFAULT_BAUD=9600;

    /**
     * Default value for the number of bits
     */
    private static final Integer DEFAULT_BITS=8;

    /**
     * Default value for the stop bit
     */
    private static final Integer DEFAULT_STOP_BITS=1;

    /**
     * Default value for the parity
     */
    private static final Integer DEFAULT_PARITY=0;

    /**
     * Default value for the time filter
     */
    private static final Integer DEFAULT_TIME_FILTER=1000;

    /**
     * This field contains all informations of the file .properties
     */
    protected Properties prop;

    /**
     * This class find all datalogger properties in a file .properties
     * @param propFileName Path to the file .properties
     * @throws IOException Exception throw when the properties file not found
     */
    public Configuration(String propFileName) throws IOException {
        //Open properties files
        InputStream inputStream = new FileInputStream(propFileName);

        //Prepare properties
        prop = new Properties();

        //Load properties
        prop.load(inputStream);

        findModuleInformation();
    }

    protected void findModuleInformation(){
        if(getValueOf("website")!=null) {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            //Create the http get
            HttpGet httpGet = new HttpGet(getValueOf("website") +"inventary/modules/"+ getValueOf("moduleId")+"/information");

            try {
                //Send the post
                HttpResponse response = httpClient.execute(httpGet);
                if(response.getStatusLine().getStatusCode()==200) {
                    System.out.println("ok");
                    JsonObject obj = Json.createReader(response.getEntity().getContent()).readObject();
                    prop.setProperty("dataToken",obj.getString("dataToken"));
                    prop.setProperty("collectUrl",obj.getString("collectUrl"));
                    prop.setProperty("condition",obj.getString("condition"));
                    if(obj.containsKey("latitude") && obj.containsKey("longitude")){
                        prop.setProperty("latitude",obj.getString("latitude"));
                        prop.setProperty("longitude",obj.getString("longitude"));
                    }
                }
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
        }
    }

    /**
     * Find a property
     * @param propName Name of the property
     * @return Return the property value, if the property not found return the default value or null
     */
    public String getValueOf(String propName){

        //If find the name of the serial port
        if(propName.equals(DEVICE)){

            //Return the name of the serial port or the default value
            return prop.getProperty(propName, DEFAULT_DEVICE);
        }

        //Return the property value or null if not found
        return prop.getProperty(propName);
    }

    /**
     * Find an integer property
     * @param propName Name of the property
     * @return Return the property value, if the property not found return the default value or null
     */
    public Integer getIntValueOf(String propName){

        if(propName.equals(TIMEOUT)){

            //Return the timeout or the default value
            return Integer.parseInt(prop.getProperty(propName, DEFAULT_TIMEOUT.toString()));

        }else if(propName.equals(BAUD)){

            //Return the baud or the default value
            return Integer.parseInt(prop.getProperty(propName, DEFAULT_BAUD.toString()));

        }else if(propName.equals(BITS)){

            //Return the number of bits or the default value
            return Integer.parseInt(prop.getProperty(propName, DEFAULT_BITS.toString()));

        }else if(propName.equals(STOP_BITS)){

            //Return the stop bits or the default value
            return Integer.parseInt(prop.getProperty(propName, DEFAULT_STOP_BITS.toString()));

        }else if(propName.equals(PARITY)){

            //Return the parity or the default value
            return Integer.parseInt(prop.getProperty(propName,DEFAULT_PARITY.toString()));

        }
        else if(propName.equals(TIME_FILTER)){
            return Integer.parseInt(prop.getProperty(propName,DEFAULT_TIME_FILTER.toString()));
        }

        //Get the property
        String property=prop.getProperty(propName);

        //If the property is not null
        if(property!=null) {

            //Return the property
            return Integer.parseInt(property);
        }

        //Else return null
        return null;
    }

    /**
     * Find sensors information
     * @return A map with for key the index where the sensor value is located and for value, an object with the sensor id and the signal id
     */
    public Map<String,List<SensorConfig>> getSensors(){
        //Get sensors informations
        String sensorsJson=prop.getProperty(SENSORS);

        //Parse sensors informations to JSON
        JsonReader reader=Json.createReader(new StringReader(sensorsJson));
        JsonObject obj=reader.readObject();

        JsonArray array;
        JsonObject objTmp;
        HashMap<String,List<SensorConfig>> sensors=new HashMap<String,List<SensorConfig>>();

        //For each informations, find the index
        for(String sensor : obj.keySet()){

            //Find the sensor information for the index
            array=obj.getJsonArray(sensor);

            ArrayList<SensorConfig> listInfos=new ArrayList<SensorConfig>();

            for(int i=0;i<array.size();i++){
                objTmp=array.getJsonObject(i);
                SensorConfig sensorConf=new SensorConfig(objTmp.getInt("index"),objTmp.getString("espece"),objTmp.getString("info"));
                listInfos.add(sensorConf);
            }

            //Put data into the map with the signal id
            sensors.put(sensor, listInfos);
        }

        //Returns sensors informations
        return sensors;
    }
}