package org.apolline.cmd.particule;

import org.apolline.cmd.Cmd;
import org.apolline.cmd.Ordonnanceur;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ParticuleDataOrdonnanceur implements Cmd, Ordonnanceur{
    protected final Cmd begin;
    protected final Cmd bin;
    protected final Cmd bin2;
    protected final Cmd crc;
    protected final Cmd unsignedInt32;
    protected final Cmd floatCmd;
    protected int cmd;
    protected Cmd current;
    protected String[] values;

    public ParticuleDataOrdonnanceur(OutputStream out,InputStream is){
        begin=new BeginReadCmd(out,is);
        bin=new ReadBinCmd(out,is);
        bin2=new ReadBin2Cmd(out,is);
        crc=new ReadCRCCmd(out,is);
        unsignedInt32=new ReadUnsignedInt32Cmd(out,is);
        floatCmd=new ReadFloatCmd(out,is);
        cmd=0;
        values=new String[27];
    }

    public void execute() {
        if(cmd==0){
            current=begin;
        }else if(cmd<17){
            current=bin;
        }
        else if(cmd<21){
            current=bin2;
        }
        else if(cmd<24){
            current=unsignedInt32;
        }
        else if(cmd==24){
            current=crc;
        }else{
            current=floatCmd;
        }
        current.execute();
    }

    public String[] getValues() {
        cmd=0;
        return values;
    }

    public boolean haveValues() {
        return cmd==28;
    }

    public void read() {
        current.read();
        if(cmd==0 && !current.isFinish())
            return;
        if(current.isFinish()) {
            if(cmd>0) {
                values[cmd - 1] = current.getResponse();
            }
            cmd++;
            ((ParticuleCmd)current).init();
        }
    }

    public String getResponse() {
        String str=values[0];
        for(int i=1;i<27;i++){
            str+=";"+values[i];
        }
        cmd=0;
        return str;
    }

    public boolean isFinish() {
        return cmd==28;
    }
}
