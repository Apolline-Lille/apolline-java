package org.apolline;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.apolline.reader.SerialReader;

/**
 * This class listen the serial port event
 */
public class SerialListener implements SerialPortEventListener {

    /**
     * Class used for read data received on the serial port
     */
    private SerialReader reader;

    /**
     * This class listen the serial port event
     * @param reader Class used for read data received on the serial port
     */
    public SerialListener(SerialReader reader){
	    this.reader=reader;
    }

    /**
     * This method is call when an event is detect on the serial port
     * @param event
     */
    public void serialEvent(final SerialPortEvent event){
        switch(event.getEventType()) {
            //If data are received on the serial port
            case SerialPortEvent.DATA_AVAILABLE:
                //Read data
                reader.readData();
                break;
        }
    }
}
