package org.apolline;

import gnu.io.*;
import org.apolline.configuration.Configuration;
import org.apolline.configuration.DataloggerParser;
import org.apolline.configuration.InstallNativeLibrary;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Datalogger.class,CommPortIdentifier.class,RXTXCommDriver.class})
@SuppressStaticInitializationFor({"gnu.io.RXTXCommDriver","gnu.io.CommPortIdentifier"})
public class DataloggerTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private Datalogger datalogger;

    @Before
    public void setUp() throws Exception {
        datalogger=new Datalogger();
    }

    @Test
    public void testNoPortFound(){
        Enumeration<CommPortIdentifier> enumPort= mock(FakeEnum.class);

        mockStatic(CommPortIdentifier.class);
        PowerMockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(enumPort);
        when(enumPort.hasMoreElements()).thenReturn(false);

        assertNull(datalogger.findPort("test"));

        verify(enumPort).hasMoreElements();
    }

    @Test
    public void testPortFoundButNotHaveTheGoodType(){
        Enumeration<CommPortIdentifier> enumPort= mock(FakeEnum.class);
        CommPortIdentifier port=mock(CommPortIdentifier.class);

        mockStatic(CommPortIdentifier.class);
        PowerMockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(enumPort);
        when(enumPort.hasMoreElements()).thenReturn(true, false);
        when(enumPort.nextElement()).thenReturn(port);
        when(port.getPortType()).thenReturn(CommPortIdentifier.PORT_PARALLEL);

        assertNull(datalogger.findPort("test"));

        verify(enumPort, times(2)).hasMoreElements();
        verify(enumPort).nextElement();
        verify(port).getPortType();
    }

    @Test
    public void testFindPortButNotHaveTheGoodName(){
        Enumeration<CommPortIdentifier> enumPort= mock(FakeEnum.class);
        CommPortIdentifier port=mock(CommPortIdentifier.class);

        mockStatic(CommPortIdentifier.class);
        PowerMockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(enumPort);
        when(enumPort.hasMoreElements()).thenReturn(true, false);
        when(enumPort.nextElement()).thenReturn(port);
        when(port.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);
        when(port.getName()).thenReturn("/dev/ttyUSB0");

        assertNull(datalogger.findPort("test"));

        verify(enumPort, times(2)).hasMoreElements();
        verify(enumPort).nextElement();
        verify(port).getPortType();
        verify(port).getName();
    }

    @Test
    public void testFindPort(){
        Enumeration<CommPortIdentifier> enumPort= mock(FakeEnum.class);
        CommPortIdentifier port=mock(CommPortIdentifier.class);

        mockStatic(CommPortIdentifier.class);
        PowerMockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(enumPort);
        when(enumPort.hasMoreElements()).thenReturn(true, false);
        when(enumPort.nextElement()).thenReturn(port);
        when(port.getPortType()).thenReturn(CommPortIdentifier.PORT_SERIAL);
        when(port.getName()).thenReturn("/dev/ttyUSB0");

        assertEquals(datalogger.findPort("/dev/ttyUSB0"), port);

        verify(enumPort).hasMoreElements();
        verify(enumPort).nextElement();
        verify(port).getPortType();
        verify(port).getName();
    }

    @Test
    public void testConnectPort() throws PortInUseException, UnsupportedCommOperationException {
        CommPortIdentifier pid=mock(CommPortIdentifier.class);
        SerialPort serial=mock(SerialPort.class);

        when(pid.open("datalogger",10)).thenReturn(serial);
        doNothing().when(serial).setSerialPortParams(9600, 8, 1, 0);

        SerialPort res=datalogger.connectPort(pid, 10, 9600, 8, 1, 0);

        assertEquals(res, serial);

        verify(pid).open("datalogger", 10);
        verify(serial).setSerialPortParams(9600, 8, 1, 0);
    }

    @Test
    public void main() throws Exception {
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);
        DataloggerParser parser=mock(DataloggerParser.class);
        Datalogger datalogger =mock(Datalogger.class);
        Configuration config=mock(Configuration.class);
        DataloggerFacade facade=mock(DataloggerFacade.class);
        String args[]={"-p","file.properties"};

        whenNew(InstallNativeLibrary.class).withNoArguments().thenReturn(install);
        doNothing().when(install).install();
        doNothing().when(install).loadLibrary();
        whenNew(Datalogger.class).withNoArguments().thenReturn(datalogger);
        whenNew(DataloggerParser.class).withNoArguments().thenReturn(parser);
        doNothing().when(parser).parse(args);
        when(parser.getValueOf("p")).thenReturn("file.properties");
        whenNew(Configuration.class).withArguments("file.properties").thenReturn(config);
        when(config.getValueOf("type")).thenReturn("ADC");
        whenNew(DataloggerFacade.class).withArguments(datalogger, config).thenReturn(facade);
        doNothing().when(facade).start_ADCDatalogger();

        Datalogger.main(args);

        verifyNew(InstallNativeLibrary.class).withNoArguments();
        verify(install).install();
        verify(install).loadLibrary();
        verifyNew(Datalogger.class).withNoArguments();
        verifyNew(DataloggerParser.class).withNoArguments();
        verify(parser).parse(args);
        verify(parser).getValueOf("p");
        verifyNew(Configuration.class).withArguments("file.properties");
        verify(config).getValueOf("type");
        verifyNew(DataloggerFacade.class).withArguments(datalogger, config);
        verify(facade).start_ADCDatalogger();
    }

    @Test
    public void testMainWithErrorWhenInstallLibrary() throws Exception {
        exit.expectSystemExitWithStatus(51);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);
        String args[]={"-p","file.properties"};

        doNothing().when(err).println("Error during install the java rxtx library");
        whenNew(InstallNativeLibrary.class).withNoArguments().thenReturn(install);
        Mockito.doThrow(IOException.class).when(install).install();

        Datalogger.main(args);

        verifyNew(InstallNativeLibrary.class).withNoArguments();
        verify(install).install();
        verify(err).println("Error during install the java rxtx library");
    }

    @Test
    public void testMainWithErrorWhenLoadLibrary() throws Exception {
        exit.expectSystemExitWithStatus(52);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);
        InstallNativeLibrary install=mock(InstallNativeLibrary.class);
        String args[]={"-p","file.properties"};

        doNothing().when(err).println("Error during load rxtx library");
        whenNew(InstallNativeLibrary.class).withNoArguments().thenReturn(install);
        doNothing().when(install).install();
        Mockito.doThrow(IOException.class).when(install).loadLibrary();

        Datalogger.main(args);

        verifyNew(InstallNativeLibrary.class).withNoArguments();
        verify(install).install();
        verify(install).loadLibrary();
        verify(err).println("Error during load rxtx library");
    }

    @Test
    public void testMainWithErrorWhenReadFileProperties() throws Exception {
        exit.expectSystemExitWithStatus(53);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);

        InstallNativeLibrary install=mock(InstallNativeLibrary.class);
        DataloggerParser parser=mock(DataloggerParser.class);
        Datalogger datalogger =mock(Datalogger.class);
        String args[]={"-p","file.properties"};

        whenNew(InstallNativeLibrary.class).withNoArguments().thenReturn(install);
        doNothing().when(install).install();
        doNothing().when(install).loadLibrary();
        whenNew(Datalogger.class).withNoArguments().thenReturn(datalogger);
        whenNew(DataloggerParser.class).withNoArguments().thenReturn(parser);
        doNothing().when(parser).parse(args);
        when(parser.getValueOf("p")).thenReturn("file.properties");
        whenNew(Configuration.class).withArguments("file.properties").thenThrow(IOException.class);

        Datalogger.main(args);

        verifyNew(InstallNativeLibrary.class).withNoArguments();
        verify(install).install();
        verify(install).loadLibrary();
        verifyNew(Datalogger.class).withNoArguments();
        verifyNew(DataloggerParser.class).withNoArguments();
        verify(parser).parse(args);
        verify(parser).getValueOf("p");
        verifyNew(Configuration.class).withArguments("file.properties");
        verify(err).println("Error when read the properties file file.properties");
    }

    @Test
    public void testMainWithErrorWhenCallAnUndefinedDatalogger() throws Exception {
        exit.expectSystemExitWithStatus(41);

        PrintStream err=mock(PrintStream.class);
        System.setErr(err);

        InstallNativeLibrary install=mock(InstallNativeLibrary.class);
        DataloggerParser parser=mock(DataloggerParser.class);
        Datalogger datalogger =mock(Datalogger.class);
        Configuration config=mock(Configuration.class);
        DataloggerFacade facade=mock(DataloggerFacade.class);
        String args[]={"-p","file.properties"};

        whenNew(InstallNativeLibrary.class).withNoArguments().thenReturn(install);
        doNothing().when(install).install();
        doNothing().when(install).loadLibrary();
        whenNew(Datalogger.class).withNoArguments().thenReturn(datalogger);
        whenNew(DataloggerParser.class).withNoArguments().thenReturn(parser);
        doNothing().when(parser).parse(args);
        when(parser.getValueOf("p")).thenReturn("file.properties");
        whenNew(Configuration.class).withArguments("file.properties").thenReturn(config);
        when(config.getValueOf("type")).thenReturn("ADC");
        whenNew(DataloggerFacade.class).withArguments(datalogger, config).thenReturn(facade);
        doThrow(Exception.class).when(facade).start_ADCDatalogger();

        Datalogger.main(args);

        verifyNew(InstallNativeLibrary.class).withNoArguments();
        verify(install).install();
        verify(install).loadLibrary();
        verifyNew(Datalogger.class).withNoArguments();
        verifyNew(DataloggerParser.class).withNoArguments();
        verify(parser).parse(args);
        verify(parser).getValueOf("p");
        verifyNew(Configuration.class).withArguments("file.properties");
        verify(config).getValueOf("type");
        verifyNew(DataloggerFacade.class).withArguments(datalogger, config);
        verify(facade).start_ADCDatalogger();
        verify(err).println("Datalogger ADC not exist");
    }

    protected class FakeEnum implements Enumeration<CommPortIdentifier> {

        public boolean hasMoreElements() {
            return false;
        }

        public CommPortIdentifier nextElement() {
            return null;
        }
    }
}