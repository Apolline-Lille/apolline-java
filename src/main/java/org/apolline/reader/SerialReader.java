package org.apolline.reader;

import org.apolline.ApisenseSender;
import org.apolline.configuration.SensorConfig;

import java.io.BufferedReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This abstract is used for read data received by sensors
 */
public abstract class SerialReader{

	/**
	 * Input stream for read sensors data
	 */
	protected BufferedReader is;

	/**
	 * Number of value data received
	 */
	protected int nbValue;

	/**
	 * Sensors informations
	 */
	protected Map<String,List<SensorConfig>> sensors;

	/**
	 * Data sender to apisense
	 */
	protected ApisenseSender sender;

	/**
	 * Module id on which sensors are connected
	 */
	protected String moduleId,conditionId,lat,lon;

	protected int timeFilter,index;

	protected Date lastSend;

	/**
	 * Abstract class used for read data received by sensors
	 * @param is Input stream for read sensors data
	 * @param moduleId Module id on which sensors are connected
	 * @param nbValue Number of values data received
	 * @param sensors Sensors informations
	 */
	public SerialReader(BufferedReader is,String collectUrl,String dataToken,String moduleId,String conditionId,String lat, String lon,int nbValue,Map<String,List<SensorConfig>> sensors,int timeFilter){
		this.is=is;
		this.nbValue=nbValue;
		this.sensors=sensors;
		this.moduleId=moduleId;
		this.timeFilter=timeFilter;
		this.conditionId=conditionId;
		this.lat=lat;
		this.lon=lon;
		this.index=0;

		//Get instance of apisense sender
		sender=ApisenseSender.getInstance(collectUrl,dataToken);
	}

	/**
	 * This method verify if sensors informations in the properties file are correct
	 */
	protected void verifySensorsInformation(){
		//if sensors informations are incorrect
		if(!sensorsInformationAreCorrect()){

			//Print an error
			System.err.println("Sensors informations are incorrect");

			//Stop the application
			System.exit(1);
		}
	}

	/**
	 * Determinate in terms of the time filter, if can send data to apisense
	 * @param d Current date
	 * @return True if can send data to apisense, else false
	 */
	protected boolean canSend(Date d){
		return (lastSend==null ||lastSend.getTime()+timeFilter<d.getTime());
	}

	/**
	 * Read data received
	 */
	public abstract void readData();

	/**
	 * Verify if sensors informations are correct
	 * @return Return if sensors information are correct, else false
	 */
	protected abstract boolean sensorsInformationAreCorrect();
}
