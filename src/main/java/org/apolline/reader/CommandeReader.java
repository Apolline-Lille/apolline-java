package org.apolline.reader;

import org.apolline.cmd.Ordonnanceur;
import org.apolline.configuration.SensorConfig;

import java.io.BufferedReader;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CommandeReader extends SerialReader {

    protected Ordonnanceur ordonnanceur;

    /**
     * Abstract class used for read data received by sensors
     *
     * @param moduleId   Module id on which sensors are connected
     * @param nbValue   Number of nbValue data received
     * @param sensors    Sensors informations
     * @param timeFilter
     */
    public CommandeReader(Ordonnanceur ordonnaceur, String collectUrl,String dataToken,String moduleId,String conditionId,String lat, String lon, int nbValue, Map<String, List<SensorConfig>> sensors, int timeFilter) {
        super(null,collectUrl,dataToken,moduleId,conditionId,lat,lon, nbValue, sensors, timeFilter);
        this.ordonnanceur=ordonnaceur;
    }

    @Override
    public void readData() {
        ordonnanceur.read();
        if(ordonnanceur.haveValues()){
            //Find the current date
            GregorianCalendar calendar=new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            sender.send(moduleId,conditionId,lat,lon,sensors,calendar.getTime(),ordonnanceur.getValues(),index++);

            //Wait before send the next command
            try {
                Thread.sleep(timeFilter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ordonnanceur.execute();
    }

    @Override
    protected boolean sensorsInformationAreCorrect() {
        return true;
    }
}
