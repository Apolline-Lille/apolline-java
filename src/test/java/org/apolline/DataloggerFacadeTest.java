package org.apolline;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import org.apolline.cmd.Ordonnanceur;
import org.apolline.cmd.co2.Co2Ordonnanceur;
import org.apolline.cmd.particule.ParticuleOrdonnanceur;
import org.apolline.configuration.Configuration;
import org.apolline.configuration.SensorConfig;
import org.apolline.reader.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataloggerFacade.class)
@SuppressStaticInitializationFor({"gnu.io.RXTXCommDriver","gnu.io.CommPortIdentifier"})
public class DataloggerFacadeTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    protected DataloggerFacade facade;
    protected Configuration config;
    protected Datalogger datalogger;

    @Before
    public void setUp(){
        datalogger=mock(Datalogger.class);
        config=mock(Configuration.class);
        facade=mock(DataloggerFacade.class);
        facade.config=config;
        facade.datalogger=datalogger;
    }

    @Test
    public void testADCDatalogger() throws Exception {
        SerialPort serial=mock(SerialPort.class);
        InputStream is=mock(InputStream.class);
        InlineReader reader=mock(InlineReader.class);
        Map<String,List<SensorConfig>> sensors=mock(Map.class);

        when(config.getIntValueOf("numberOfValue")).thenReturn(6);
        when(config.getValueOf("moduleId")).thenReturn("moduleId");
        when(config.getIntValueOf("timeFilter")).thenReturn(1000);
        when(config.getSensors()).thenReturn(sensors);
        when(config.getValueOf("collectUrl")).thenReturn("url");
        when(config.getValueOf("dataToken")).thenReturn("token");
        when(config.getValueOf("condition")).thenReturn("condId");
        when(config.getValueOf("latitude")).thenReturn("2.3");
        when(config.getValueOf("longitude")).thenReturn("4.5");
        when(serial.getInputStream()).thenReturn(is);
        whenNew(InlineReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000)).thenReturn(reader);
        when(facade.initSerial()).thenReturn(serial);
        doNothing().when(facade).addListerner(eq(serial), eq(reader));
        doCallRealMethod().when(facade).start_ADCDatalogger();

        facade.start_ADCDatalogger();

        verify(config).getIntValueOf("numberOfValue");
        verify(config).getValueOf("moduleId");
        verify(config).getIntValueOf("timeFilter");
        verify(config).getValueOf("collectUrl");
        verify(config).getValueOf("dataToken");
        verify(config).getValueOf("condition");
        verify(config).getValueOf("latitude");
        verify(config).getValueOf("longitude");
        verify(config).getSensors();
        verify(serial).getInputStream();
        verifyNew(InlineReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000));
        verify(facade).initSerial();
        verify(facade).addListerner(eq(serial), eq(reader));
    }

    @Test
    public void testUNODatalogger() throws Exception {
        SerialPort serial=mock(SerialPort.class);
        InputStream is=mock(InputStream.class);
        InlineReader reader=mock(InlineReader.class);
        Map<String,List<SensorConfig>> sensors=mock(Map.class);

        when(config.getIntValueOf("numberOfValue")).thenReturn(6);
        when(config.getIntValueOf("timeFilter")).thenReturn(1000);
        when(config.getValueOf("moduleId")).thenReturn("moduleId");
        when(config.getValueOf("collectUrl")).thenReturn("url");
        when(config.getValueOf("dataToken")).thenReturn("token");
        when(config.getValueOf("condition")).thenReturn("condId");
        when(config.getValueOf("latitude")).thenReturn("2.3");
        when(config.getValueOf("longitude")).thenReturn("4.5");
        when(config.getSensors()).thenReturn(sensors);
        when(serial.getInputStream()).thenReturn(is);
        whenNew(InlineReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000)).thenReturn(reader);
        doNothing().when(reader).setSeparator(",");
        when(facade.initSerial()).thenReturn(serial);
        doNothing().when(facade).addListerner(eq(serial), eq(reader));
        doCallRealMethod().when(facade).start_UNODatalogger();

        facade.start_UNODatalogger();

        verify(config).getIntValueOf("numberOfValue");
        verify(config).getIntValueOf("timeFilter");
        verify(config).getValueOf("moduleId");
        verify(config).getSensors();
        verify(config).getValueOf("collectUrl");
        verify(config).getValueOf("dataToken");
        verify(config).getValueOf("condition");
        verify(config).getValueOf("latitude");
        verify(config).getValueOf("longitude");
        verify(serial).getInputStream();
        verifyNew(InlineReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000));
        verify(reader).setSeparator(",");
        verify(facade).initSerial();
        verify(facade).addListerner(eq(serial), eq(reader));
    }

    @Test
    public void testDYLOSDatalogger() throws Exception {
        SerialPort serial=mock(SerialPort.class);
        InputStream is=mock(InputStream.class);
        InlineReader reader=mock(InlineReader.class);
        Map<String,List<SensorConfig>> sensors=mock(Map.class);

        when(config.getIntValueOf("numberOfValue")).thenReturn(6);
        when(config.getIntValueOf("timeFilter")).thenReturn(1000);
        when(config.getValueOf("moduleId")).thenReturn("moduleId");
        when(config.getValueOf("collectUrl")).thenReturn("url");
        when(config.getValueOf("dataToken")).thenReturn("token");
        when(config.getValueOf("condition")).thenReturn("condId");
        when(config.getValueOf("latitude")).thenReturn("2.3");
        when(config.getValueOf("longitude")).thenReturn("4.5");
        when(config.getSensors()).thenReturn(sensors);
        when(serial.getInputStream()).thenReturn(is);
        whenNew(InlineReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000)).thenReturn(reader);
        doNothing().when(reader).setSeparator(",");
        when(facade.initSerial()).thenReturn(serial);
        doNothing().when(facade).addListerner(eq(serial), eq(reader));
        doCallRealMethod().when(facade).start_DYLOSDatalogger();

        facade.start_DYLOSDatalogger();

        verify(config).getIntValueOf("numberOfValue");
        verify(config).getIntValueOf("timeFilter");
        verify(config).getValueOf("moduleId");
        verify(config).getSensors();
        verify(config).getValueOf("collectUrl");
        verify(config).getValueOf("dataToken");
        verify(config).getValueOf("condition");
        verify(config).getValueOf("latitude");
        verify(config).getValueOf("longitude");
        verify(serial).getInputStream();
        verifyNew(InlineReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000));
        verify(reader).setSeparator(",");
        verify(facade).initSerial();
        verify(facade).addListerner(eq(serial), eq(reader));
    }

    @Test
    public void testWaspDatalogger() throws Exception {
        SerialPort serial=mock(SerialPort.class);
        InputStream is=mock(InputStream.class);
        WaspReader reader=mock(WaspReader.class);
        Map<String,List<SensorConfig>> sensors=mock(Map.class);

        when(config.getIntValueOf("numberOfValue")).thenReturn(6);
        when(config.getIntValueOf("timeFilter")).thenReturn(1000);
        when(config.getValueOf("moduleId")).thenReturn("moduleId");
        when(config.getValueOf("collectUrl")).thenReturn("url");
        when(config.getValueOf("dataToken")).thenReturn("token");
        when(config.getValueOf("condition")).thenReturn("condId");
        when(config.getValueOf("latitude")).thenReturn("2.3");
        when(config.getValueOf("longitude")).thenReturn("4.5");
        when(config.getSensors()).thenReturn(sensors);
        when(serial.getInputStream()).thenReturn(is);
        doNothing().when(serial).enableReceiveTimeout(1000);
        doNothing().when(serial).disableReceiveThreshold();
        whenNew(WaspReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000)).thenReturn(reader);
        when(facade.initSerial()).thenReturn(serial);
        doNothing().when(facade).addListerner(eq(serial), eq(reader));
        doCallRealMethod().when(facade).start_waspDatalogger();

        facade.start_waspDatalogger();

        verify(config).getIntValueOf("numberOfValue");
        verify(config).getIntValueOf("timeFilter");
        verify(config).getValueOf("moduleId");
        verify(config).getSensors();
        verify(config).getValueOf("collectUrl");
        verify(config).getValueOf("dataToken");
        verify(config).getValueOf("condition");
        verify(config).getValueOf("latitude");
        verify(config).getValueOf("longitude");
        verify(serial).getInputStream();
        verify(serial).enableReceiveTimeout(1000);
        verify(serial).disableReceiveThreshold();
        verifyNew(WaspReader.class).withArguments(any(BufferedReader.class), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(6), eq(sensors), eq(1000));
        verify(facade).initSerial();
        verify(facade).addListerner(eq(serial), eq(reader));
    }

    @Test
    public void testCO2Datalogger() throws Exception {
        SerialPort serial=mock(SerialPort.class);
        InputStream is=mock(InputStream.class);
        CommandeReader reader=mock(CommandeReader.class);
        Co2Ordonnanceur ordonnanceur=mock(Co2Ordonnanceur.class);
        Map<String,List<SensorConfig>> sensors=mock(Map.class);

        when(config.getIntValueOf("timeFilter")).thenReturn(1000);
        when(config.getValueOf("moduleId")).thenReturn("moduleId");
        when(config.getValueOf("collectUrl")).thenReturn("url");
        when(config.getValueOf("dataToken")).thenReturn("token");
        when(config.getValueOf("condition")).thenReturn("condId");
        when(config.getValueOf("latitude")).thenReturn("2.3");
        when(config.getValueOf("longitude")).thenReturn("4.5");
        when(config.getSensors()).thenReturn(sensors);
        when(serial.getInputStream()).thenReturn(is);
        whenNew(Co2Ordonnanceur.class).withArguments(any(DataOutputStream.class), any(BufferedReader.class)).thenReturn(ordonnanceur);
        whenNew(CommandeReader.class).withArguments(eq(ordonnanceur), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(3), eq(sensors), eq(1000)).thenReturn(reader);
        doNothing().when(ordonnanceur).execute();
        when(facade.initSerial()).thenReturn(serial);
        doNothing().when(facade).addListerner(eq(serial), eq(reader));
        doCallRealMethod().when(facade).start_CO2Datalogger();

        facade.start_CO2Datalogger();

        verify(config).getIntValueOf("timeFilter");
        verify(config).getValueOf("moduleId");
        verify(config).getSensors();
        verify(config).getValueOf("collectUrl");
        verify(config).getValueOf("dataToken");
        verify(config).getValueOf("condition");
        verify(config).getValueOf("latitude");
        verify(config).getValueOf("longitude");
        verify(serial).getInputStream();
        verifyNew(Co2Ordonnanceur.class).withArguments(any(DataOutputStream.class), any(BufferedReader.class));
        verifyNew(CommandeReader.class).withArguments(eq(ordonnanceur), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(3), eq(sensors), eq(1000));
        verify(ordonnanceur).execute();
        verify(facade).initSerial();
        verify(facade).addListerner(eq(serial), eq(reader));
    }

    @Test
    public void test_dataloggerParticule() throws Exception {
        SerialPort serial=mock(SerialPort.class);
        InputStream is=mock(InputStream.class);
        CommandeReader reader=mock(CommandeReader.class);
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Map<String,List<SensorConfig>> sensors=mock(Map.class);

        when(config.getIntValueOf("timeFilter")).thenReturn(1000);
        when(config.getValueOf("moduleId")).thenReturn("moduleId");
        when(config.getValueOf("collectUrl")).thenReturn("url");
        when(config.getValueOf("dataToken")).thenReturn("token");
        when(config.getValueOf("condition")).thenReturn("condId");
        when(config.getValueOf("latitude")).thenReturn("2.3");
        when(config.getValueOf("longitude")).thenReturn("4.5");
        when(config.getSensors()).thenReturn(sensors);
        when(serial.getInputStream()).thenReturn(is);
        whenNew(ParticuleOrdonnanceur.class).withArguments(any(DataOutputStream.class), any(BufferedReader.class)).thenReturn(ordonnanceur);
        whenNew(CommandeReader.class).withArguments(eq(ordonnanceur), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(26), eq(sensors), eq(1000)).thenReturn(reader);
        doNothing().when(ordonnanceur).execute();
        when(facade.initSerial()).thenReturn(serial);
        doNothing().when(facade).addListerner(eq(serial), eq(reader));
        doCallRealMethod().when(facade).start_particuleDatalogger();

        facade.start_particuleDatalogger();

        verify(config).getIntValueOf("timeFilter");
        verify(config).getValueOf("moduleId");
        verify(config).getSensors();
        verify(config).getValueOf("collectUrl");
        verify(config).getValueOf("dataToken");
        verify(config).getValueOf("condition");
        verify(config).getValueOf("latitude");
        verify(config).getValueOf("longitude");
        verify(serial).getInputStream();
        verifyNew(ParticuleOrdonnanceur.class).withArguments(any(DataOutputStream.class), any(BufferedReader.class));
        verifyNew(CommandeReader.class).withArguments(eq(ordonnanceur), eq("url"), eq("token"), eq("moduleId"), eq("condId"), eq("2.3"), eq("4.5"), eq(26), eq(sensors), eq(1000));
        verify(ordonnanceur).execute();
        verify(facade).initSerial();
        verify(facade).addListerner(eq(serial), eq(reader));
    }

    @Test
    public void test_initSerial() throws PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier port=mock(CommPortIdentifier.class);
        SerialPort serial=mock(SerialPort.class);

        when(config.getValueOf("device")).thenReturn("/dev/ttyUSB0");
        when(config.getIntValueOf("timeout")).thenReturn(10000);
        when(config.getIntValueOf("baud")).thenReturn(9600);
        when(config.getIntValueOf("bits")).thenReturn(8);
        when(config.getIntValueOf("stopBits")).thenReturn(1);
        when(config.getIntValueOf("parity")).thenReturn(0);
        when(datalogger.findPort("/dev/ttyUSB0")).thenReturn(port);
        when(datalogger.connectPort(port, 10000, 9600, 8, 1, 0)).thenReturn(serial);
        doCallRealMethod().when(facade).initSerial();

        assertEquals(serial, facade.initSerial());

        verify(config).getValueOf("device");
        verify(config).getIntValueOf("timeout");
        verify(config).getIntValueOf("baud");
        verify(config).getIntValueOf("bits");
        verify(config).getIntValueOf("stopBits");
        verify(config).getIntValueOf("parity");
        verify(datalogger).findPort("/dev/ttyUSB0");
        verify(datalogger).connectPort(port, 10000, 9600, 8, 1, 0);
    }

    @Test
    public void test_initSerial_When_PortUsed() throws PortInUseException, UnsupportedCommOperationException {
        exit.expectSystemExitWithStatus(31);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);

        CommPortIdentifier port=mock(CommPortIdentifier.class);
        SerialPort serial=mock(SerialPort.class);

        when(config.getValueOf("device")).thenReturn("/dev/ttyUSB0");
        when(config.getIntValueOf("timeout")).thenReturn(10000);
        when(config.getIntValueOf("baud")).thenReturn(9600);
        when(config.getIntValueOf("bits")).thenReturn(8);
        when(config.getIntValueOf("stopBits")).thenReturn(1);
        when(config.getIntValueOf("parity")).thenReturn(0);
        when(datalogger.findPort("/dev/ttyUSB0")).thenReturn(port);
        when(datalogger.connectPort(port, 10000, 9600, 8, 1, 0)).thenThrow(PortInUseException.class);
        doCallRealMethod().when(facade).initSerial();

        assertEquals(serial, facade.initSerial());

        verify(config).getValueOf("device");
        verify(config).getIntValueOf("timeout");
        verify(config).getIntValueOf("baud");
        verify(config).getIntValueOf("bits");
        verify(config).getIntValueOf("stopBits");
        verify(config).getIntValueOf("parity");
        verify(datalogger).findPort("/dev/ttyUSB0");
        verify(datalogger).connectPort(port, 10000, 9600, 8, 1, 0);
        verify(err).println("/dev/ttyUSB0 is used");
    }

    @Test
    public void test_initSerial_When_PortParamIsNotValid() throws PortInUseException, UnsupportedCommOperationException {
        exit.expectSystemExitWithStatus(32);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);

        CommPortIdentifier port=mock(CommPortIdentifier.class);
        SerialPort serial=mock(SerialPort.class);

        when(config.getValueOf("device")).thenReturn("/dev/ttyUSB0");
        when(config.getIntValueOf("timeout")).thenReturn(10000);
        when(config.getIntValueOf("baud")).thenReturn(9600);
        when(config.getIntValueOf("bits")).thenReturn(8);
        when(config.getIntValueOf("stopBits")).thenReturn(1);
        when(config.getIntValueOf("parity")).thenReturn(0);
        when(datalogger.findPort("/dev/ttyUSB0")).thenReturn(port);
        when(datalogger.connectPort(port, 10000, 9600, 8, 1, 0)).thenThrow(UnsupportedCommOperationException.class);
        doCallRealMethod().when(facade).initSerial();

        assertEquals(serial, facade.initSerial());

        verify(config).getValueOf("device");
        verify(config).getIntValueOf("timeout");
        verify(config).getIntValueOf("baud");
        verify(config).getIntValueOf("bits");
        verify(config).getIntValueOf("stopBits");
        verify(config).getIntValueOf("parity");
        verify(datalogger).findPort("/dev/ttyUSB0");
        verify(datalogger).connectPort(port, 10000, 9600, 8, 1, 0);
        verify(err).println("Error in port parameter");
    }

    @Test
    public void test_initSerial_When_PortNotFound() throws PortInUseException, UnsupportedCommOperationException {
        exit.expectSystemExitWithStatus(33);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);

        CommPortIdentifier port=mock(CommPortIdentifier.class);
        SerialPort serial=mock(SerialPort.class);

        when(config.getValueOf("device")).thenReturn("/dev/ttyUSB0");
        when(config.getIntValueOf("timeout")).thenReturn(10000);
        when(config.getIntValueOf("baud")).thenReturn(9600);
        when(config.getIntValueOf("bits")).thenReturn(8);
        when(config.getIntValueOf("stopBits")).thenReturn(1);
        when(config.getIntValueOf("parity")).thenReturn(0);
        when(datalogger.findPort("/dev/ttyUSB0")).thenReturn(port);
        when(datalogger.connectPort(port, 10000, 9600, 8, 1, 0)).thenThrow(NullPointerException.class);
        doCallRealMethod().when(facade).initSerial();

        assertEquals(serial, facade.initSerial());

        verify(config).getValueOf("device");
        verify(config).getIntValueOf("timeout");
        verify(config).getIntValueOf("baud");
        verify(config).getIntValueOf("bits");
        verify(config).getIntValueOf("stopBits");
        verify(config).getIntValueOf("parity");
        verify(datalogger).findPort("/dev/ttyUSB0");
        verify(datalogger).connectPort(port, 10000, 9600, 8, 1, 0);
        verify(err).println("Serial port /dev/ttyUSB0 not found");
    }

    @Test
    public void test_addListener() throws Exception {
        SerialReader reader=mock(SerialReader.class);
        SerialPort serial=mock(SerialPort.class);
        SerialListener listener=mock(SerialListener.class);

        whenNew(SerialListener.class).withArguments(reader).thenReturn(listener);
        doNothing().when(serial).addEventListener(listener);
        doNothing().when(serial).notifyOnDataAvailable(true);
        doCallRealMethod().when(facade).addListerner(serial, reader);

        facade.addListerner(serial, reader);

        verifyNew(SerialListener.class).withArguments(reader);
        verify(serial).addEventListener(listener);
        verify(serial).notifyOnDataAvailable(true);
    }
}
