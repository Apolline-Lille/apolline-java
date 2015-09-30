package org.apolline.reader;

import org.apolline.configuration.SensorConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * This class read data received by a waspmote
 */
public class WaspReader extends SerialReader {

    protected String[] values;

    protected Date date;

    protected int nbStore;

    /**
     * Flag indicate if the waspmote is in an initialization phase
     */
    protected boolean init;

    /**
     * This class read data received by a waspmote
     * @param is Input stream for read data
     * @param moduleId Module id on which the waspmote is connect
     * @param nbValue Number of values received
     * @param sensors Sensors informations
     */
    public WaspReader(BufferedReader is,String collectUrl,String dataToken,String moduleId,String conditionId,String lat, String lon, int nbValue, Map<String,List<SensorConfig>> sensors, int timeFilter) {
        super(is,collectUrl,dataToken,moduleId,conditionId,lat,lon, nbValue, sensors,timeFilter);

        //Init the list of data received
        values=new String[nbValue];

        //The the flag tu an initialization phase
        init=true;

        nbStore=0;

        verifySensorsInformation();
    }

    /**
     * Verify if sensors informations are correct
     * @return Return if sensors information are correct, else false
     */
    protected boolean sensorsInformationAreCorrect(){
        //For each sensor information
        /*for(int i: sensors.keySet()){

            //Index is greater or equals to the number of data received
            if(i<5 || i>=nbValue) {
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
            //Read one line
            String line=is.readLine();

            //If is in an initialization phase
            if(init){

                //Line contain "|" stop the initialization phase
                if(line.contains("|")){
                    init=false;
                }

            }

            if(!init){

                //analize sensors data
                analizeSensorData(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store data received and send their when all are received
     * @param line Line received
     */
    protected void analizeSensorData(String line){
        //if the ligne contains more than 5 character
        if(line.length()>5){
            //get data
            String data[]=line.split("\\|");

            //Get data index
            int index=Integer.parseInt(data[2]);

            //if index is equal to 1, init the list of data
            if(index==1){
                initData();
            }

            storeData(data,index);
            //If all data are received
            if (index == nbValue) {
                pushData();
            }
        }
    }

    /**
     * Store the data received to the array
     * @param data Data received
     * @param index Index in the array where store the data
     */
    protected void storeData(String[] data,int index){
        if (data.length == 4) {
            values[index-1]=data[3];
        } else {
            values[index-1]="";
        }
        nbStore++;
    }

    /**
     * Find the current date and init the array of value received
     */
    protected void initData(){
        //Get the current date
        GregorianCalendar calendar=new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        date=calendar.getTime();

        //Init the array of value received
        values=new String[nbValue];

        //Init number of data received
        nbStore=0;
    }

    /**
     * Push data to apisense
     */
    protected void pushData(){
        //If have received the good number of data and can send data
        if(nbStore==nbValue && canSend(date)) {
                //Send data to apisense
                sender.send(moduleId,conditionId,lat,lon, sensors, date, values,index++);
        }
    }
}
