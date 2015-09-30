package org.apolline.reader;

import org.apolline.ApisenseSender;
import org.apolline.configuration.SensorConfig;
import org.apolline.reader.InlineReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApisenseSender.class,InlineReader.class})
public class InlineReaderTest {

    protected InlineReader reader;
    protected BufferedReader is;
    protected Map<String,List<SensorConfig>> sensors;
    protected ApisenseSender sender;
    protected GregorianCalendar date;

    @Before
    public void setUp() throws Exception {
        is=mock(BufferedReader.class);
        sensors=mock(Map.class);
        sender=mock(ApisenseSender.class);
        date=new GregorianCalendar();
        whenNew(GregorianCalendar.class).withAnyArguments().thenReturn(date);

        mockStatic(ApisenseSender.class);
        when(ApisenseSender.getInstance("url","token")).thenReturn(sender);

        reader = new InlineReader(is,"url","token","moduleId","condId","2.3","4.5",6,sensors,1000);
    }

    @Test
    public void testDefaultSeparator(){
        assertEquals(reader.separator,";");
    }

    @Test
    public void testSetSeparator(){
        reader.setSeparator(",");

        assertEquals(reader.separator,",");
    }

    @Test
    public void testReadData() throws IOException {
        String values[]={"1","2","3","4","5","6"};

        when(is.readLine()).thenReturn("1;2;3;4;5;6");
        doNothing().when(sender).send("moduleId","condId","2.3","4.5", sensors, date.getTime(), values,0);

        reader.readData();

        verify(is).readLine();
        verify(sender).send("moduleId","condId","2.3","4.5", sensors, date.getTime(), values,0);
    }

    @Test
    public void testReadDataWithNotGoodNumberValue() throws IOException {
        when(is.readLine()).thenReturn("1;2;3;4;5");

        reader.readData();

        verify(is).readLine();
        verify(sender,never()).send(anyString(),anyString(),anyString(),anyString(),any(Map.class), any(Date.class), any(String[].class),anyInt());
    }

/*
    @Test
    public void testSensorsInformationAreCorrect(){
        Map<String,List<MesureInformation>> sensors=new HashMap<String,List<MesureInformation>>();
        sensors.put(3, mock(SensorValue.class));
        InlineReader reader=new InlineReader(mock(BufferedReader.class),"moduleId",5,sensors,1000);

        assertTrue(reader.sensorsInformationAreCorrect());
    }

    @Test
    public void testSensorsInformationAreNotCorrectIndexHigher(){
        Map<String,List<MesureInformation>> sensors=new HashMap<String,List<MesureInformation>>();
        sensors.put(5,mock(SensorValue.class));
        InlineReader reader=new InlineReader(mock(BufferedReader.class),"moduleId",5,sensors,1000);

        assertFalse(reader.sensorsInformationAreCorrect());
    }

    @Test
    public void testSensorsInformationAreNotCorrectIndexSmaller(){
        Map<String,List<MesureInformation>> sensors=new HashMap<String,List<MesureInformation>>();
        sensors.put(1,mock(SensorValue.class));
        InlineReader reader=new InlineReader(mock(BufferedReader.class),"moduleId",5,sensors,1000);
        reader.setDecalValue(2);

        assertFalse(reader.sensorsInformationAreCorrect());
    }*/
}