package org.apolline.cmd.co2;

import java.io.BufferedReader;
import java.io.DataOutputStream;

/**
 * This CO2 command find the CO2 quantity
 */
public class NCmd extends Co2Cmd{

    protected static final String CMD="N\r";

    public NCmd(DataOutputStream out, BufferedReader is) {
        super(out, is);
    }

    /**
     * Find the CO2 command
     * @return A string represent the CO2 command
     */
    protected String findCmd() {
        return CMD;
    }
}
