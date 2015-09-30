package org.apolline.configuration;

public class SensorConfig {
    public int index;
    public String espece;
    public String info;

    public SensorConfig(int index, String espece, String info){
        this.index=index;
        this.espece=espece;
        this.info=info;
    }

    public boolean equals(Object o){
        if(o.getClass().equals(getClass())){
            return info.equals(((SensorConfig)o).info);
        }
        return false;
    }
}