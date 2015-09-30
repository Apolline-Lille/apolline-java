package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represent the command for read the first byte of the list of data send by the sensor
 */
public class BeginReadCmd extends ReadDataCmd {

    /**
     * This class represent the command for read the first byte of the list of data send by the sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public BeginReadCmd(OutputStream out, InputStream is) {
        super(out, is);
    }

    /**
     * Find bytes of the real response
     */
    protected void findData() {

    }

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public boolean isFinish() {
        return readByte[1]==(byte)0xF3;
    }

    /**
     * Parse byte of the response to a string
     */
    protected void parseResponse() {

    }
}
