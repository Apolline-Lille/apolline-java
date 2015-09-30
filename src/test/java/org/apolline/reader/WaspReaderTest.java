package org.apolline.reader;

import org.apolline.ApisenseSender;
import org.apolline.configuration.SensorConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApisenseSender.class,WaspReader.class})
public class WaspReaderTest {

    protected WaspReader reader;
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

        reader = new WaspReader(is,"url","token","moduleId","condId","2.3","4.5",12,sensors,1000);
    }
/*
    @Test
    public void testSensorsInformationAreCorrect(){
        Set<Integer> list=new HashSet<Integer>();
        list.add(5);
        list.add(7);
        list.add(11);
        when(sensors.keySet()).thenReturn(list);

        assertTrue(reader.sensorsInformationAreCorrect());
    }

    @Test
    public void testSensorsInformationAreCorrect2(){
        WaspReader reader= new WaspReader(is,"moduleId",7,sensors,1000);

        Set<Integer> list=new HashSet<Integer>();
        list.add(5);
        list.add(7);
        list.add(12);
        when(sensors.keySet()).thenReturn(list);

        assertTrue(reader.sensorsInformationAreCorrect());
    }

    @Test
    public void testSensorsInformationAreNotCorrectWithLessThan5(){
        Set<Integer> list=new HashSet<Integer>();
        list.add(4);
        when(sensors.keySet()).thenReturn(list);

        assertFalse(reader.sensorsInformationAreCorrect());
    }

    @Test
    public void testSensorsInformationAreNotCorrectWithGreaterThanTheNumberOfValue(){
        Set<Integer> list=new HashSet<Integer>();
        list.add(12);
        when(sensors.keySet()).thenReturn(list);

        assertFalse(reader.sensorsInformationAreCorrect());
    }

    @Test
    public void testSensorsInformationAreNotCorrectWithGreaterThanTheNumberOfValue2(){
        WaspReader reader= new WaspReader(is,"moduleId",7,sensors,1000);

        Set<Integer> list=new HashSet<Integer>();
        list.add(13);
        when(sensors.keySet()).thenReturn(list);

        assertFalse(reader.sensorsInformationAreCorrect());
    }*/

    @Test
    public void testInitData() throws Exception {
        GregorianCalendar calendar=new GregorianCalendar();

        whenNew(GregorianCalendar.class).withArguments(TimeZone.getTimeZone("UTC")).thenReturn(calendar);

        reader.values[5]="";
        reader.nbStore=3;
        reader.date=date.getTime();
        reader.initData();

        assertEquals(calendar.getTime(), reader.date);
        assertEquals(0, reader.nbStore);
        assertArrayEquals(new String[12], reader.values);

        verifyNew(GregorianCalendar.class).withArguments(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void testStoreData(){
        String data[]={"","","2.3","3.4"};

        reader.storeData(data, 5);

        assertEquals("3.4", reader.values[4]);
        assertEquals(1, reader.nbStore);
    }

    @Test
    public void testStore2Data(){
        String data[]={"","","","2.3"};
        String data2[]={"","","","3.4"};

        reader.storeData(data,5);
        reader.storeData(data2,8);

        assertEquals("2.3", reader.values[4]);
        assertEquals("3.4", reader.values[7]);
        assertEquals(2,reader.nbStore);
    }

    @Test
    public void testStoreNotData(){
        String data[]={""};

        reader.storeData(data, 5);

        assertEquals("", reader.values[4]);
    }

    @Test
    public void testPushDataNotWorkWhenNbStoreNotEqualToNbValue(){
        reader.pushData();

        verify(sender,never()).send(anyString(),anyString(),anyString(),anyString(), any(Map.class), any(Date.class), any(String[].class),anyInt());
    }

    @Test
    public void testPushDataNotWorkWhenCanSendReturnFalse(){
        WaspReader reader=mock(WaspReader.class);

        reader.date=date.getTime();
        reader.nbStore=0;
        reader.nbValue=0;
        reader.index=2;
        when(reader.canSend(date.getTime())).thenReturn(false);
        doCallRealMethod().when(reader).pushData();

        reader.pushData();

        verify(sender, never()).send(anyString(),anyString(),anyString(),anyString(), any(Map.class), any(Date.class), any(String[].class),eq(2));
        verify(reader).canSend(date.getTime());
    }

    @Test
    public void testPushData(){
        WaspReader reader=mock(WaspReader.class);

        reader.date=date.getTime();
        reader.nbStore=0;
        reader.nbValue=0;
        reader.index=2;
        reader.moduleId="moduleId";
        reader.conditionId="condId";
        reader.lat="2.3";
        reader.lon="4.5";
        reader.sensors=sensors;
        reader.values=new String[12];
        reader.sender=sender;
        when(reader.canSend(date.getTime())).thenReturn(true);
        doCallRealMethod().when(reader).pushData();
        doNothing().when(sender).send("moduleId","condId","2.3","4.5", sensors, date.getTime(), new String[12],2);

        reader.pushData();

        verify(sender).send("moduleId","condId","2.3","4.5",sensors,date.getTime(),new String[12],2);
        verify(reader).canSend(date.getTime());
    }

    @Test
    public void testAnalizeDataWithShorterLine(){
        WaspReader reader=mock(WaspReader.class);

        doCallRealMethod().when(reader).analizeSensorData("1234");

        reader.analizeSensorData("1234");

        verify(reader,never()).initData();
        verify(reader,never()).storeData(any(String[].class), anyInt());
        verify(reader,never()).pushData();
    }

    @Test
    public void testAnalizeDataWithIndexEqualsTo1(){
        WaspReader reader=mock(WaspReader.class);
        String data[]={"3","2","1","0"};

        doCallRealMethod().when(reader).analizeSensorData("3|2|1|0");
        doNothing().when(reader).initData();
        doNothing().when(reader).storeData(data, 1);

        reader.analizeSensorData("3|2|1|0");

        verify(reader).initData();
        verify(reader).storeData(data, 1);
        verify(reader,never()).pushData();
    }

    @Test
    public void testAnalizeDataWithIndexEqualsToNbValue(){
        WaspReader reader=mock(WaspReader.class);
        String data[]={"1","2","12","4"};

        reader.nbValue=12;
        doCallRealMethod().when(reader).analizeSensorData("1|2|12|4");
        doNothing().when(reader).pushData();
        doNothing().when(reader).storeData(data, 12);

        reader.analizeSensorData("1|2|12|4");

        verify(reader,never()).initData();
        verify(reader).storeData(data, 12);
        verify(reader).pushData();
    }

    @Test
    public void testAnalizeData(){
        WaspReader reader=mock(WaspReader.class);
        String data[]={"1","2","5","4"};

        doCallRealMethod().when(reader).analizeSensorData("1|2|5|4");
        doNothing().when(reader).storeData(data, 5);

        reader.analizeSensorData("1|2|5|4");

        verify(reader,never()).initData();
        verify(reader).storeData(data, 5);
        verify(reader,never()).pushData();
    }

    @Test
    public void testReadInitData() throws IOException {
        WaspReader reader=mock(WaspReader.class);
        when(is.readLine()).thenReturn("line");

        reader.is=is;
        reader.init=true;
        doCallRealMethod().when(reader).readData();

        reader.readData();
        assertTrue(reader.init);

        verify(reader,never()).analizeSensorData(anyString());
    }

    @Test
    public void testReadData() throws IOException {
        WaspReader reader=mock(WaspReader.class);
        when(is.readLine()).thenReturn("1|2|3|4");

        reader.is=is;
        reader.init=true;
        doCallRealMethod().when(reader).readData();
        doNothing().when(reader).analizeSensorData("1|2|3|4");

        reader.readData();
        assertFalse(reader.init);

        verify(reader).analizeSensorData("1|2|3|4");
    }
}
