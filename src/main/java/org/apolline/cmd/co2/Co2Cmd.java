package org.apolline.cmd.co2;

import org.apolline.cmd.Cmd;

import java.io.*;

/**
 * This abstract class represent a command for the CO2 sensor
 */
public abstract class Co2Cmd implements Cmd {

    /**
     * Output stream of the serial port for write the command
     */
    protected DataOutputStream out;

    /**
     * Input stream of the serial port for read the response of the command
     */
    protected BufferedReader is;

    /**
     * The response of the command
     */
    protected String resp;

    /**
     * Flag for indicat if the command is finish
     */
    protected boolean finish;

    /**
     * Constructor for the class represent a command for the CO2 sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public Co2Cmd(DataOutputStream out,BufferedReader is){
        this.out=out;
        this.is=is;
        finish=false;
    }

    /**
     * Execute the command
     */
    public void execute() {
        //Indicat the command is not finish
        finish=false;

        //Write the command
        try {
            out.writeBytes(findCmd());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the response of the command
     */
    public void read() {
        try {
            //Read the response
            resp=is.readLine();

            //Indicat the command is finish
            finish=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public boolean isFinish(){
        return finish;
    }

    /**
     * Find the response of the command
     * @return The response of the command
     */
    public String getResponse(){
        return resp;
    }

    /**
     * Find the CO2 command
     * @return A string represent the CO2 command
     */
    protected abstract String findCmd();
}
