package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class represent the command for read the value of sample flow rate, temperature/pressure and sample period in the list of data send by the sensor
 */
public class ReadUnsignedInt32Cmd extends ReadDataCmd{

    /**
     * Byte array for the real response
     */
    protected byte[] data;

    /**
     * This class represent the command for read the value of sample flow rate, temperature/pressure and sample period in the list of data send by the sensor
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public ReadUnsignedInt32Cmd(OutputStream out, InputStream is) {
        super(out, is);
        data=new byte[4];
    }

    /**
     * Find bytes of the real response
     */
    protected void findData() {
        //Set the byte of the real response to the array at the good index and increments the number of response
        data[nbResponse++]=readByte[1];
    }

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    public boolean isFinish() {
        return nbResponse==4;
    }

    /**
     * Parse bytes of the response to a string
     */
    protected void parseResponse() {
        //Transform the response represent in little endian to an integer
        resp=Integer.toString(ByteBuffer.wrap(data, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
    }
}
