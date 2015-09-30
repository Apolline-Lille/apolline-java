package org.apolline.reader;

import org.apolline.configuration.SensorConfig;

import java.io.*;
import java.util.*;

/**
 * This class read data received in inline format
 */
public class InlineReader extends SerialReader{

	/**
	 * Separator used for separate each sensor data
	 */
	protected String separator;

	/**
	 * This class read data received in inline format
	 * @param is Input stream for read data
	 * @param moduleId Module id on which sensors are connect
	 * @param nbValue Number of values received
	 * @param sensors Sensors informations
	 */
	public InlineReader(BufferedReader is,String collectUrl,String dataToken,String moduleId,String conditionId,String lat, String lon,int nbValue,Map<String,List<SensorConfig>> sensors,int timeFilter){
		super(is,collectUrl,dataToken,moduleId,conditionId,lat,lon,nbValue,sensors,timeFilter);

		//Set the default separator
		separator=";";
	}

	/**
	 * Verify if sensors informations are correct
	 * @return Return if sensors information are correct, else false
	 */
	protected boolean sensorsInformationAreCorrect(){
		//For each sensor information
		/*for(int index:sensors.keySet()){

			//Verify if the index is valid
			if(index<decalValue || index>=(decalValue+nbSensor)) {
				return false;
			}
		}*/
		return true;
	}

	/**
	 * Set the separator used for separate each sensor data
	 * @param separator The separator
	 */
	public void setSeparator(String separator){
		this.separator=separator;
	}

	/**
	 * Read data received
	 */
	public void readData(){

		try {
			//Read one line
			String line = is.readLine();

			verifySensorsInformation();

			//Find the current date
			GregorianCalendar calendar=new GregorianCalendar(TimeZone.getTimeZone("UTC"));

			//Separate data received for the line
			String[] value=line.split(separator);

			//If received the good number of value
			if(value.length==nbValue){
				if(canSend(calendar.getTime())) {
					//Send data to apisense
					sender.send(moduleId,conditionId,lat,lon, sensors, calendar.getTime(), value,index++);
					lastSend=calendar.getTime();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
