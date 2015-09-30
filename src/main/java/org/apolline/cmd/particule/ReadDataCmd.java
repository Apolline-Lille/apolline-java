package org.apolline.cmd.particule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This abstract class represent the particul command for find data
 */
public abstract class ReadDataCmd extends ParticuleCmd {

    /**
     * A byte array reprensent the command for find data
     */
    protected static final byte[] CMD={(byte)0x61,(byte)0x30};

    /**
     * This class represent the particul command for find data
     * @param out Output stream of the serial port for write the command
     * @param is Input stream of the serial port for read the response of the command
     */
    public ReadDataCmd(OutputStream out, InputStream is) {
        super(out, is);
        readByte=new byte[2];
    }

    /**
     * Find the particul command
     * @return A byte array represent the particul command
     */
    protected byte[] findCmd() {
        return CMD;
    }
}
