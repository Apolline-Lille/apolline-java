package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class reprensent the command for connect USB with the SPI of the sensor
 */
public class SetSpiModeCmd extends ParticuleCmd{

    /**
     * A byte array reprensent the command for connect USB with the SPI of the sensor
     */
    protected static final byte[] CMD={(byte)0x5A,(byte)0x02,(byte)0x92,(byte)0x0B};

    /**
     * This class represent the command for connect USB with the SPI of the sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public SetSpiModeCmd(OutputStream out, InputStream is) {
        super(out, is);
        readByte=new byte[4];
    }

    /**
     * Find bytes of the real response
     */
    protected void findData() {

    }

    /**
     * Find the command
     * @return A byte array represent the command
     */
    protected byte[] findCmd() {
        return CMD;
    }

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public boolean isFinish() {
        return readByte[0]==(byte)0xFF;
    }

    /**
     * Parse byte of the response to a string
     */
    protected void parseResponse() {

    }
}
