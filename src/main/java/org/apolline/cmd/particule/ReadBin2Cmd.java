package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represent the command for read the value of bin1, bin3, bin5 and bin7 in the list of data send by the sensor
 */
public class ReadBin2Cmd extends ReadDataCmd{

    /**
     * This class represent the command for read the value of bin1, bin3, bin5 and bin7 in the list of data send by the sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public ReadBin2Cmd(OutputStream out, InputStream is) {
        super(out, is);
    }

    /**
     * Find bytes of the real response
     */
    protected void findData() {
        //Increments the number of reponse received
        nbResponse++;
    }

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public boolean isFinish() {
        return nbResponse==1;
    }

    /**
     * Parse byte of the response to a string
     */
    protected void parseResponse() {
        //Parse the single byte of response to an integer
        resp=Integer.toString(readByte[1]);
    }
}