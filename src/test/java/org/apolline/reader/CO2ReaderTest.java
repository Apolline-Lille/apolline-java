package org.apolline.reader;

import org.apolline.ApisenseSender;
import org.apolline.configuration.SensorConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApisenseSender.class,CO2Reader.class})
public class CO2ReaderTest {

    protected CO2Reader reader;
    protected DataOutputStream outStream;
    protected BufferedReader is;
    protected Map<String,List<SensorConfig>> sensors;
    protected ApisenseSender sender;
    protected GregorianCalendar date;

    @Before
    public void setUp() throws Exception {
        outStream= PowerMockito.mock(DataOutputStream.class);
        is=mock(BufferedReader.class);
        sensors=mock(Map.class);
        sender=mock(ApisenseSender.class);
        date=new GregorianCalendar();
        whenNew(GregorianCalendar.class).withAnyArguments().thenReturn(date);

        mockStatic(ApisenseSender.class);
        when(ApisenseSender.getInstance("url","token")).thenReturn(sender);

        reader = new CO2Reader(outStream,is,"url","token","moduleId","condId","2.3","4.5",3,sensors,1000);
    }

    /*@Test
    public void testSensorsInformationAreCorrect(){
        Set<Integer> set=new HashSet<Integer>();
        set.add(0);
        set.add(2);
        when(sensors.keySet()).thenReturn(set);

        assertTrue(reader.sensorsInformationAreCorrect());

        verify(sensors,times(2)).keySet();
    }

    @Test
    public void testSensorsInformationAreNotCorrect(){
        Set<Integer> set=new HashSet<Integer>();
        set.add(3);
        when(sensors.keySet()).thenReturn(set);

        assertFalse(reader.sensorsInformationAreCorrect());

        verify(sensors,times(2)).keySet();
    }*/

    @Test
    public void testSendNextDataWithSendN() throws IOException {
        PowerMockito.doNothing().when(outStream).writeBytes("N\r");

        reader.sendNextData();

        verify(outStream).writeBytes("N\r");
    }

    @Test
    public void testSendNextDataWithSendT() throws IOException {
        reader.nbReceive=1;
        PowerMockito.doNothing().when(outStream).writeBytes("T\r");

        reader.sendNextData();

        verify(outStream).writeBytes("T\r");
    }

    @Test
    public void testSendNextDataWithSendV() throws IOException {
        reader.nbReceive=2;
        PowerMockito.doNothing().when(outStream).writeBytes("V\r");

        reader.sendNextData();

        verify(outStream).writeBytes("V\r");
    }

    @Test
    public void testSendNextDataWithThread() throws IOException, InterruptedException {
        reader.values[0]="2.3";
        mockStatic(Thread.class);
        PowerMockito.doNothing().when(outStream).writeBytes("N\r");
        doNothing().when(Thread.class);
        Thread.sleep(eq(1000l));

        reader.sendNextData();

        verify(outStream).writeBytes("N\r");
        verifyStatic();
        Thread.sleep(eq(1000l));
    }

    @Test
    public void testReadData() throws IOException {
        when(is.readLine()).thenReturn("2.3");
        PowerMockito.doNothing().when(outStream).writeBytes("T\r");

        reader.readData();
        assertEquals("2.3", reader.values[0]);

        verify(outStream).writeBytes("T\r");
        verify(is).readLine();
    }

    @Test
    public void testReadData2() throws IOException {
        reader.nbReceive=1;
        when(is.readLine()).thenReturn("3.4");
        PowerMockito.doNothing().when(outStream).writeBytes("V\r");

        reader.readData();
        assertEquals("3.4", reader.values[1]);

        verify(outStream).writeBytes("V\r");
        verify(is).readLine();
    }

    @Test
    public void testReadData3() throws IOException, InterruptedException {
        reader.nbReceive=2;
        reader.values= new String[]{"2.3", "3.4", null};
        reader.index=5;
        String[] values={"2.3", "3.4","4.5"};
        when(is.readLine()).thenReturn("4.5");
        PowerMockito.doNothing().when(outStream).writeBytes("N\r");
        doNothing().when(sender).send(eq("moduleId"),eq("condId"),eq("2.3"),eq("4.5"), eq(sensors), any(Date.class), eq(values),eq(5));
        mockStatic(Thread.class);
        doNothing().when(Thread.class);
        Thread.sleep(eq(1000l));

        reader.readData();
        assertEquals("4.5", reader.values[2]);
        assertEquals(0, reader.nbReceive);

        verify(outStream).writeBytes("N\r");
        verify(is).readLine();
        verify(sender).send(eq("moduleId"),eq("condId"),eq("2.3"),eq("4.5"), eq(sensors), any(Date.class), eq(values),eq(5));
        verifyStatic();
        Thread.sleep(eq(1000l));
    }
}
