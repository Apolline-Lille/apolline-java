package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represent the particul command for check the status of the sensor
 */
public class CheckStatusCmd extends ParticuleCmd{

    /**
     * A byte array reprensent the command for check the status of the sensor
     */
    protected static final byte[] CMD={(byte)0x61,(byte)0xCF};

    /**
     * This class represent the particul command for check the status of the sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public CheckStatusCmd(OutputStream out, InputStream is) {
        super(out, is);
        readByte=new byte[2];
    }

    /**
     * Find bytes of the real response
     */
    protected void findData() {

    }

    /**
     * Find the particul command
     * @return A byte array represent the particul command
     */
    protected byte[] findCmd() {
        return CMD;
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
