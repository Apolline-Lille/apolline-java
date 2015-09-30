package org.apolline.reader;

import org.apolline.configuration.SensorConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * This class read data received by the CO2 sensor
 */
public class CO2Reader extends SerialReader{

    /**
     * List of value received
     */
    protected String values[];

    /**
     * Number of data received
     */
    protected int nbReceive;

    /**
     * Output stream for send data to the CO2 sensor
     */
    protected DataOutputStream wr;

    /**
     * List of data send to the CO2 sensor
     */
    protected String send[]={"N","T","V"};

    /**
     * This class read data received by the CO2 sensor
     * @param wr Output stream for send data to the CO2 sensor
     * @param is Input stream for read data from the CO2 sensor
     * @param moduleId Module id on which the CO2 sensor is connect
     * @param nbSensor Number of value received by the CO2 sensor
     * @param sensors Sensors informations
     */
    public CO2Reader(DataOutputStream wr,BufferedReader is,String collectUrl,String dataToken,String moduleId,String conditionId,String lat, String lon, int nbSensor, Map<String,List<SensorConfig>> sensors,int timeFilter) {
        super(is,collectUrl,dataToken,moduleId,conditionId,lat,lon, nbSensor, sensors,timeFilter);
        values=new String[nbSensor];
        nbReceive=0;
        this.wr=wr;
        verifySensorsInformation();
    }

    /**
     * Verify if sensors informations are correct
     * @return Return if sensors information are correct, else false
     */
    protected boolean sensorsInformationAreCorrect(){
        /*for(int index : sensors.keySet()){
            if(index>=nbSensor){
                return false;
            }
        }*/
        return true;
    }

    /**
     * Read data received
     */
    public void readData() {
        try {
            //Read the line
            String line=is.readLine();

            //Store the data
            values[nbReceive]=line;
            nbReceive++;

            //If the number of data received equals to the number of data wait
            if(nbReceive==nbValue){

                //Init the number of data received
                nbReceive=0;

                //Find the current date
                GregorianCalendar calendar=new GregorianCalendar(TimeZone.getTimeZone("UTC"));

                //Send data to apisense
                sender.send(moduleId,conditionId,lat,lon, sensors, calendar.getTime(), values,index++);

            }

            //Send the next command to the sensor
            sendNextData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the next command to the sensor
     * @throws IOException
     */
    public void sendNextData() throws IOException {
        //If have just data to apisense
        if(nbReceive==0 && values[0]!=null) {

            //Wait before send the next command
            try {
                Thread.sleep(timeFilter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //Send the command
        wr.writeBytes(send[nbReceive]+"\r");
    }
}
