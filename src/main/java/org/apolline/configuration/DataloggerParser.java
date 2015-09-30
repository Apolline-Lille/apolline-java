package org.apolline.configuration;

import org.apache.commons.cli.*;

/**
 * This class parse parameters set when start the application
 */
public class DataloggerParser{

	/**
	 * This field contains parameters parsed
	 */
	private CommandLine cmd;

	/**
	 * The parameters parser
	 */
	private CommandLineParser parser;

	/**
	 * This class parse parameters set when start the application
	 */
	public DataloggerParser(){
		//Initialize the parser
		parser = new DefaultParser();
	}

	/**
	 * This method parse parameters set when start the application
	 * @param args List of parameters
	 */
	public void parse(String args[]){
		try {
			//Parse parameters
			cmd = parser.parse( createOptions(), args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method create all application parameter
	 * @return The list with all application parameter
	 */
	protected Options createOptions(){
		//Intialize application parameter
		Options options = new Options();

		//Add an option for set the property file
		options.addOption("p","properties",true,"Properties file");

		//Add an option for get the application version
		options.addOption("v","version",false,"version");

		//Return all application parameters
		return options;
	}

	/**
	 * Get a value associat to a parameter
	 * @param arg Parameter name
	 * @return Value associat to the parameter
	 */
	public String getValueOf(String arg){

		//Return the value associat to the parameter
		return cmd.getOptionValue(arg);
	}
}
