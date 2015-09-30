package org.apolline.cmd.particule;

import org.apolline.cmd.Cmd;
import org.apolline.cmd.Ordonnanceur;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ParticuleOrdonnanceur implements Ordonnanceur {

    protected int cmd;
    protected OutputStream out;
    protected InputStream is;
    protected Cmd current;
    protected ParticuleDataOrdonnanceur dataOrdonnceur;
    protected Cmd startFan;
    protected Cmd stopFan;
    protected String[] values;

    public ParticuleOrdonnanceur(OutputStream out,InputStream is){
        cmd=0;
        this.out=out;
        this.is=is;
        dataOrdonnceur=new ParticuleDataOrdonnanceur(out,is);
        startFan=new StartFanCmd(out,is);
        stopFan=new StopFanCmd(out,is);
    }

    public void execute() {
        switch (cmd){
            case 0:setSpiMode();
                break;
            case 1:checkStatus();
                break;
            case 2:startFan();
                break;
            case 3:
            case 4:readData();
                break;
            case 5:stopFan();
                break;
        }
        current.execute();
    }

    protected void setSpiMode(){
        current=new SetSpiModeCmd(out,is);
    }

    protected void checkStatus(){
        current=new CheckStatusCmd(out,is);
    }

    protected void startFan(){
        current=startFan;
    }

    protected void stopFan(){
        current=stopFan;
    }

    protected void readData(){
        current=dataOrdonnceur;
    }

    public void read(){
        current.read();
        if(current.isFinish()){
            if(cmd==4){
                values=((Ordonnanceur)current).getValues();
            }
            else if(cmd==3){
                current.getResponse();
            }
            else if(cmd==1){
                try {Thread.sleep(2000);} catch (InterruptedException e) {}
            }
            if(cmd!=4){
                cmd++;
            }
        }
        else if(cmd==0){
            System.exit(0);
        }
    }

    public boolean haveValues(){
        return values!=null;
    }

    public String[] getValues(){
        String[] vals=values;
        values=null;
        return vals;
    }
}
