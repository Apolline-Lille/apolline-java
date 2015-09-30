package org.apolline;

import gnu.io.*;
import org.apolline.cmd.Ordonnanceur;
import org.apolline.cmd.co2.Co2Ordonnanceur;
import org.apolline.cmd.particule.ParticuleOrdonnanceur;
import org.apolline.configuration.Configuration;
import org.apolline.configuration.ExitStatus;
import org.apolline.reader.*;

import java.io.*;
import java.util.TooManyListenersException;

/**
 * This class is used for start the differents datalogger
 */
public class DataloggerFacade{

	protected Datalogger datalogger;

	/**
	 * This class contains all informations about the datalogger
	 */
	protected Configuration config;

	/**
	 * This class is used for start the differents datalogger
	 * @param datalogger The class used for connect to the serial port
	 * @param config The class contains all informations about the datalogger
	 */
	public DataloggerFacade(Datalogger datalogger,Configuration config){
		this.datalogger=datalogger;
		this.config=config;
	}

	protected SerialPort initSerial(){
		//Find the serial port identifier
		CommPortIdentifier port = datalogger.findPort(config.getValueOf(Configuration.DEVICE));

		SerialPort serial = null;
		try {
			//Connect and find the serial port
			serial = datalogger.connectPort(port,
					config.getIntValueOf(Configuration.TIMEOUT),
					config.getIntValueOf(Configuration.BAUD),
					config.getIntValueOf(Configuration.BITS),
					config.getIntValueOf(Configuration.STOP_BITS),
					config.getIntValueOf(Configuration.PARITY)
			);
		} catch (PortInUseException e1) {
			System.err.println(config.getValueOf(Configuration.DEVICE)+" is used");
			System.exit(ExitStatus.PORT_USED);
		} catch (UnsupportedCommOperationException e1) {
			System.err.println("Error in port parameter");
			System.exit(ExitStatus.PORT_PARAM);
		}catch(NullPointerException e){
			System.err.println("Serial port " + config.getValueOf(Configuration.DEVICE) + " not found");
			System.exit(ExitStatus.PORT_NOT_FOUND);
		}
		return serial;
	}

	protected void addListerner(SerialPort serial, SerialReader reader){
		//Create the serial listener
		SerialListener listener=new SerialListener(reader);
		try {
			serial.addEventListener(listener);
		} catch (TooManyListenersException e) {}

		//Start notify on data available
		serial.notifyOnDataAvailable(true);
	}

	/**
	 * Start the ADC Datalogger
	 */
	public void start_ADCDatalogger(){
		try{
			SerialPort serial=initSerial();

			//Find input stream for read from the serial port
			BufferedReader is=new BufferedReader(new InputStreamReader(serial.getInputStream()));

			//Create the data reader
			InlineReader reader=new InlineReader(is,config.getValueOf("collectUrl"),config.getValueOf("dataToken"),config.getValueOf("moduleId"),config.getValueOf("condition"),config.getValueOf("latitude"),config.getValueOf("longitude"),config.getIntValueOf("numberOfValue"),config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));

			addListerner(serial,reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start UNO Datalogger
	 */
	public void start_UNODatalogger(){
		try {
			SerialPort serial = initSerial();

			//Find input stream for read from the serial port
			BufferedReader is=new BufferedReader(new InputStreamReader(serial.getInputStream()));

			//Create the data reader
			InlineReader reader=new InlineReader(is,config.getValueOf("collectUrl"),config.getValueOf("dataToken"),config.getValueOf("moduleId"),config.getValueOf("condition"),config.getValueOf("latitude"),config.getValueOf("longitude"),config.getIntValueOf("numberOfValue"),config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));

			//Set the data separator
			reader.setSeparator(",");

			addListerner(serial,reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start the CO2 datalogger
	 */
	public void start_CO2Datalogger(){
		try {
			SerialPort serial = initSerial();

			//Find output stream for send command in the serial port
			DataOutputStream wr=new DataOutputStream(serial.getOutputStream());

			//Find input stream for read from the serial port
			BufferedReader is=new BufferedReader(new InputStreamReader(serial.getInputStream()));

			//Create the data reader
			//CO2Reader reader=new CO2Reader(wr,is,config.getValueOf("moduleId"),3,config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));
			Ordonnanceur ordonnanceur=new Co2Ordonnanceur(wr,is);

			CommandeReader reader=new CommandeReader(ordonnanceur,config.getValueOf("collectUrl"),config.getValueOf("dataToken"),config.getValueOf("moduleId"),config.getValueOf("condition"),config.getValueOf("latitude"),config.getValueOf("longitude"),3,config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));

			addListerner(serial, reader);

			//Send command in the serial port
			//reader.sendNextData();
			ordonnanceur.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start the wasp datalogger
	 */
	public void start_waspDatalogger(){
		try {
			SerialPort serial = initSerial();

			serial.enableReceiveTimeout(1000);
			serial.disableReceiveThreshold();

			//Find input stream for read from the serial port
			BufferedReader is=new BufferedReader(new InputStreamReader(serial.getInputStream()));

			//Create the data reader
			WaspReader reader=new WaspReader(is,config.getValueOf("collectUrl"),config.getValueOf("dataToken"),config.getValueOf("moduleId"),config.getValueOf("condition"),config.getValueOf("latitude"),config.getValueOf("longitude"),config.getIntValueOf("numberOfValue"),config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));

			addListerner(serial, reader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start UNO Datalogger
	 */
	public void start_DYLOSDatalogger(){
		try {
			SerialPort serial = initSerial();

			//Find input stream for read from the serial port
			BufferedReader is = new BufferedReader(new InputStreamReader(serial.getInputStream()));

			//Create the data reader
			InlineReader reader=new InlineReader(is,config.getValueOf("collectUrl"),config.getValueOf("dataToken"),config.getValueOf("moduleId"),config.getValueOf("condition"),config.getValueOf("latitude"),config.getValueOf("longitude"),config.getIntValueOf("numberOfValue"),config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));

			//Set the data separator
			reader.setSeparator(",");

			addListerner(serial,reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start_particuleDatalogger(){
		try {
			SerialPort serial = initSerial();

			Ordonnanceur ordonnanceur = new ParticuleOrdonnanceur(serial.getOutputStream(),serial.getInputStream());

			CommandeReader reader=new CommandeReader(ordonnanceur,config.getValueOf("collectUrl"),config.getValueOf("dataToken"),config.getValueOf("moduleId"),config.getValueOf("condition"),config.getValueOf("latitude"),config.getValueOf("longitude"),26,config.getSensors(),config.getIntValueOf(Configuration.TIME_FILTER));

			addListerner(serial, reader);

			ordonnanceur.execute();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
