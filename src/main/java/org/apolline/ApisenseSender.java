package org.apolline;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apolline.configuration.SensorConfig;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class is used for format data and send it to apisense
 */
public class ApisenseSender {

    //Apisense url
    private final String CROPS_URL;
    private final String DATA_TOKEN;

    //Instance of the class
    protected static ApisenseSender instance;

    protected ApisenseSender(String collectUrl,String dataToken) {
        CROPS_URL=collectUrl;
        DATA_TOKEN=dataToken;
    }

    /**
     * This method is used for get the instance of the class
     *
     * @return Return the instance of the class
     */
    public static ApisenseSender getInstance(String collectUrl,String dataToken) {
        if (instance == null) {
            instance = new ApisenseSender(collectUrl,dataToken);
        }
        return instance;
    }

    /**
     * This method format data to JSON for send it to apisense
     *
     * @param moduleId Module id
     * @param sensors  Map for associat a value with a sensor
     * @param date     Date when received data
     * @param values   List of data received
     * @return Data format to JSON
     */
    protected String createJson(String moduleId,String conditionId,String lat,String lon, Map<String, List<SensorConfig>> sensors, Date date, String values[],int index) {
        StringWriter writer = new StringWriter();

        //Create a JSON generator
        JsonGenerator generator = Json.createGenerator(writer);

        //Start write JSON
        generator = generator.writeStartObject()
                .write("timestamp", date.getTime()) //Write the timestamp
                .write("moduleId", moduleId) //Write the module id
                .write("conditionId",conditionId);
        if(lat!=null && lon!=null) {
            generator=generator.write("latitude", lat)
                    .write("longitude", lon);
        }
        generator=generator.write("index",index)
                .writeStartArray("sensors"); //Start the list of sensors

        List<SensorConfig> list;

        //For each sensors
        for (String sensorId : sensors.keySet()) {
            list = sensors.get(sensorId);

            //Write sensor information
            generator=generator.writeStartObject()
                    .write("sensorId", sensorId) //Write the sensor id
                    .writeStartArray("mesures");
            int i=1;
            for(SensorConfig info:list){
                generator=generator.writeStartObject()
                    .write("signalId", i++) //Write the signal id
                    .write("rawValue", values[info.index]) // Write the raw value
                    .write("especeId",info.espece)
                    .write("infoId",info.info)
                    .writeEnd();
            }
            generator=generator.writeEnd().writeEnd();
        }

        generator.writeEnd()
                .writeEnd()
                .close();


        //Return the JSON format
        return writer.toString();
    }

    /**
     * Send data to apisense
     * @param moduleId Module id
     * @param sensors Sensors informations
     * @param date Date when received data
     * @param values List of value received
     */
    public void send(final String moduleId, final String conditionId, final String lat, final String lon, final Map<String, List<SensorConfig>> sensors, final Date date, final String values[], final int index){
        new Thread(new Runnable() {
            public void run() {
                //Create the http client
                CloseableHttpClient httpClient = HttpClients.createDefault();
                try
                {
                    //Create the http post
                    HttpPost httpPost = new HttpPost(CROPS_URL);

                    //Prepare data to post
                    StringEntity stringEntity = new StringEntity(createJson(moduleId,conditionId,lat,lon,sensors,date,values,index), ContentType.APPLICATION_JSON);

                    //Set data to the post
                    httpPost.setEntity(stringEntity);

                    //Set header
                    httpPost.setHeader("Authorization", "dataToken " + DATA_TOKEN);

                    //Send the post
                    HttpResponse response =httpClient.execute(httpPost);

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    httpClient.close();
                }

                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}