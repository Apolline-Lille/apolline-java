package org.apolline.cmd.co2;

import java.io.BufferedReader;
import java.io.DataOutputStream;

public class VCmd extends Co2Cmd {
    protected static final String CMD="V\r";

    public VCmd(DataOutputStream out, BufferedReader is) {
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
