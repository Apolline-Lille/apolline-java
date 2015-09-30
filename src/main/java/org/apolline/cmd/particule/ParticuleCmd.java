package org.apolline.cmd.particule;

import org.apolline.cmd.Cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This abstract class represent a command for the particul sensor
 */
public abstract class ParticuleCmd implements Cmd {
    /**
     * Output stream of the serial port for write the command
     */
    protected OutputStream out;

    /**
     * Input stream of the serial port for read the response of the command
     */
    protected InputStream is;

    /**
     * The response of the command
     */
    protected String resp;

    /**
     * Array used for read bytes received for the response
     */
    protected byte[] readByte;

    /**
     * Number of response received
     */
    protected int nbResponse;

    /**
     * Constructor for the class represent a command for the particul sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public ParticuleCmd(OutputStream out,InputStream is){
        this.out=out;
        this.is=is;
        init();
    }

    /**
     * This method initialize the number of response received
     */
    public void init(){
        nbResponse=0;
    }

    /**
     * Execute the command
     */
    public void execute() {

        //Write the command
        try {
            out.write(findCmd());
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
            is.read(readByte);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find bytes of the real response
        findData();

        //If the command is finish
        if(isFinish()) {

            //Parse byte of the response to a string
            parseResponse();
        }
    }

    /**
     * Find the response of the command
     * @return The response of the command
     */
    public String getResponse(){
        return resp;
    }

    /**
     * Find bytes of the real response
     */
    protected abstract void findData();

    /**
     * Find the particul command
     * @return A byte array represent the particul command
     */
    protected abstract byte[] findCmd();

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public abstract boolean isFinish();

    /**
     * Parse byte of the response to a string
     */
    protected abstract void parseResponse();
}
