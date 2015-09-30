package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represent the command for start fan and laser of the sensor
 */
public class StartFanCmd extends ParticuleCmd{

    /**
     * bytes array reprensent the command for start fan and laser of the sensor
     */
    protected static final byte[] CMD1={(byte)0x61,(byte)0x03};
    protected static final byte[] CMD2={(byte)0x61,(byte)0x00};

    /**
     * This class represent the command for start fan and laser of the sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public StartFanCmd(OutputStream out, InputStream is) {
        super(out, is);
        readByte=new byte[2];
    }

    /**
     * Find bytes of the real response
     */
    protected void findData() {
        //if the response of the first part of command  is not equals to 0xF3
        if(nbResponse==0 && readByte[1]!=(byte)0xF3){
            //Wait 30 milliseconds
            try {Thread.sleep(30);} catch (InterruptedException e) {}
            //Returns
            return;
        }

        //Else wait 20 milliseconds
        try {Thread.sleep(20);} catch (InterruptedException e) {}

        //Increment the number of response for pass to the second part of the command
        nbResponse++;
    }

    /**
     * Find the particul command
     * @return A byte array represent the particul command
     */
    protected byte[] findCmd() {
        //If not have response
        if(nbResponse==0){

            //Return the first part of the command
            return CMD1;
        }

        //Else return the second part of the command
        return CMD2;
    }

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public boolean isFinish() {
        return nbResponse==2;
    }

    /**
     * Parse byte of the response to a string
     */
    protected void parseResponse() {

    }
}
