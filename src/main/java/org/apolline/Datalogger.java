package org.apolline;

import gnu.io.*;
import org.apolline.configuration.Configuration;
import org.apolline.configuration.DataloggerParser;
import org.apolline.configuration.ExitStatus;
import org.apolline.configuration.InstallNativeLibrary;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class start the datalogger, find the serial port and connect it
 */
public class Datalogger{

	/**
	 * Find the serial for read sensors data
	 * @param portWanted Name of the port wanted
	 * @return An identifier to the serial port
	 */
	public CommPortIdentifier findPort(String portWanted){

		//Find the list of port available
		Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

		//For each port
		while (portIdentifiers.hasMoreElements()){
			CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers.nextElement();

			//If is a serial port and is the port wanted
			if(pid.getPortType() == CommPortIdentifier.PORT_SERIAL && pid.getName().equals(portWanted)) {

				//Return the identifier to the serial port
				return pid;

			}
		}
		return null;
	}

	/**
	 * Start a connection to the serial port
	 * @param pid Indentifier to a serial port
	 * @param timeout Connection timeout
	 * @param baud Number of pulse per seconds
	 * @param dataBits Number data bits
	 * @param stopBits Stop bits
	 * @param parity Parity
	 * @return A connection to the serial port
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 */
	public SerialPort connectPort(CommPortIdentifier pid,int timeout,int baud,int dataBits,int stopBits,int parity) throws PortInUseException, UnsupportedCommOperationException {
		//Open the serial port
		SerialPort port = (SerialPort) pid.open("datalogger",timeout);

		//Set serial port param
		port.setSerialPortParams(baud,dataBits,stopBits,parity);

		//Return the serial port connection
		return port;
	}

	public static void main(String args[]){

		//Install rxtx library
		InstallNativeLibrary install=new InstallNativeLibrary();
		try {
			install.install();
		} catch (IOException e) {
			System.err.println("Error during install the java rxtx library");
			System.exit(ExitStatus.INSTALL_LIBRARY);
		}
		try {
			install.loadLibrary();
		} catch (Exception e){
			System.err.println("Error during load rxtx library");
			System.exit(ExitStatus.LOAD_LIBRARY);
		}

		Configuration config=null;

		//Find the datalogger
		Datalogger datalogger=new Datalogger();

		//Parse arguments received pass to the application
		DataloggerParser parser=new DataloggerParser();
		parser.parse(args);
		try {
			//Find the configuration
			config=new Configuration(parser.getValueOf("p"));
		} catch (IOException e) {
			System.err.println("Error when read the properties file "+parser.getValueOf("p"));
			System.exit(ExitStatus.READ_PROPERTIES);
		}

		//Find the datalogger facade
		DataloggerFacade facade=new DataloggerFacade(datalogger,config);

		try {
			//Find the method to call in function of the datalogger type
			Method m = facade.getClass().getMethod("start_" + config.getValueOf(Configuration.TYPE) + "Datalogger");

			//Call the method
			m.invoke(facade);
		}catch (Exception e){
			System.err.println("Datalogger "+config.getValueOf(Configuration.TYPE)+" not exist");
			System.exit(ExitStatus.FIND_DATALOGGER);
		}
	}
}
