package org.apolline.cmd.co2;

import org.apolline.cmd.Cmd;
import org.apolline.cmd.Ordonnanceur;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;


public class Co2Ordonnanceur implements Ordonnanceur {

    protected Cmd[] cmd;
    protected int cmdNumber;
    protected String values[];

    public Co2Ordonnanceur(DataOutputStream out, BufferedReader is){
        cmd=new Cmd[3];
        cmd[0]=new NCmd(out,is);
        cmd[1]=new TCmd(out,is);
        cmd[2]=new VCmd(out,is);
        cmdNumber=0;
        values=new String[3];
    }

    public void execute() {
        cmd[cmdNumber].execute();
    }

    public String[] getValues() {
        String val[]=values;
        values=null;
        return val;
    }

    public boolean haveValues() {
        return values!=null;
    }

    public void read() {
        Cmd current=cmd[cmdNumber];
        current.read();
        values[cmdNumber]=current.getResponse();
        cmdNumber=(cmdNumber+1)%3;
    }
}
