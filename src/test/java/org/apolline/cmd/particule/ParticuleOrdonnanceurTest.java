package org.apolline.cmd.particule;


import org.apolline.cmd.Cmd;
import org.apolline.cmd.co2.Co2Ordonnanceur;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ParticuleOrdonnanceur.class)
public class ParticuleOrdonnanceurTest {

    protected OutputStream out;
    protected InputStream is;
    protected ParticuleDataOrdonnanceur particuleCmd;
    protected StartFanCmd startFanCmd;
    protected StopFanCmd stopFanCmd;
    protected ParticuleOrdonnanceur ordonnanceur;

    @Before
    public void setUp() throws Exception {
        out=mock(OutputStream.class);
        is=mock(InputStream.class);
        particuleCmd=mock(ParticuleDataOrdonnanceur.class);
        startFanCmd=mock(StartFanCmd.class);
        stopFanCmd=mock(StopFanCmd.class);

        whenNew(ParticuleDataOrdonnanceur.class).withArguments(out,is).thenReturn(particuleCmd);
        whenNew(StartFanCmd.class).withArguments(out,is).thenReturn(startFanCmd);
        whenNew(StopFanCmd.class).withArguments(out,is).thenReturn(stopFanCmd);

        ordonnanceur=new ParticuleOrdonnanceur(out,is);
    }

    @Test
    public void test_constructor() throws Exception {
        assertEquals(out,ordonnanceur.out);
        assertEquals(is, ordonnanceur.is);

        verifyNew(ParticuleDataOrdonnanceur.class).withArguments(out, is);
        verifyNew(StartFanCmd.class).withArguments(out, is);
        verifyNew(StopFanCmd.class).withArguments(out,is);
    }

    @Test
    public void test_execute_with_setSpiMode() throws IOException {
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Cmd cmd=mock(Cmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=0;

        doCallRealMethod().when(ordonnanceur).execute();
        doNothing().when(ordonnanceur).setSpiMode();
        doNothing().when(cmd).execute();

        ordonnanceur.execute();

        verify(ordonnanceur).setSpiMode();
        verify(ordonnanceur,never()).checkStatus();
        verify(ordonnanceur,never()).startFan();
        verify(ordonnanceur, never()).readData();
        verify(ordonnanceur, never()).stopFan();
        verify(cmd).execute();
    }

    @Test
    public void test_execute_with_checkStatus() throws IOException {
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Cmd cmd=mock(Cmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=1;

        doCallRealMethod().when(ordonnanceur).execute();
        doNothing().when(ordonnanceur).checkStatus();
        doNothing().when(cmd).execute();

        ordonnanceur.execute();

        verify(ordonnanceur,never()).setSpiMode();
        verify(ordonnanceur).checkStatus();
        verify(ordonnanceur,never()).startFan();
        verify(ordonnanceur,never()).readData();
        verify(ordonnanceur,never()).stopFan();
        verify(cmd).execute();
    }

    @Test
    public void test_execute_with_startFan() throws IOException {
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Cmd cmd=mock(Cmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=2;

        doCallRealMethod().when(ordonnanceur).execute();
        doNothing().when(ordonnanceur).startFan();
        doNothing().when(cmd).execute();

        ordonnanceur.execute();

        verify(ordonnanceur,never()).setSpiMode();
        verify(ordonnanceur,never()).checkStatus();
        verify(ordonnanceur).startFan();
        verify(ordonnanceur,never()).readData();
        verify(ordonnanceur,never()).stopFan();
        verify(cmd).execute();
    }

    @Test
    public void test_execute_with_readFirstData() throws IOException {
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Cmd cmd=mock(Cmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=3;

        doCallRealMethod().when(ordonnanceur).execute();
        doNothing().when(ordonnanceur).readData();
        doNothing().when(cmd).execute();

        ordonnanceur.execute();

        verify(ordonnanceur,never()).setSpiMode();
        verify(ordonnanceur,never()).checkStatus();
        verify(ordonnanceur,never()).startFan();
        verify(ordonnanceur).readData();
        verify(ordonnanceur,never()).stopFan();
        verify(cmd).execute();
    }

    @Test
    public void test_execute_with_readData() throws IOException {
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Cmd cmd=mock(Cmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=4;

        doCallRealMethod().when(ordonnanceur).execute();
        doNothing().when(ordonnanceur).readData();
        doNothing().when(cmd).execute();

        ordonnanceur.execute();

        verify(ordonnanceur,never()).setSpiMode();
        verify(ordonnanceur,never()).checkStatus();
        verify(ordonnanceur,never()).startFan();
        verify(ordonnanceur).readData();
        verify(ordonnanceur,never()).stopFan();
        verify(cmd).execute();
    }

    @Test
    public void test_execute_with_stopFan() throws IOException {
        ParticuleOrdonnanceur ordonnanceur=mock(ParticuleOrdonnanceur.class);
        Cmd cmd=mock(Cmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=5;

        doCallRealMethod().when(ordonnanceur).execute();
        doNothing().when(ordonnanceur).stopFan();
        doNothing().when(cmd).execute();

        ordonnanceur.execute();

        verify(ordonnanceur,never()).setSpiMode();
        verify(ordonnanceur,never()).checkStatus();
        verify(ordonnanceur,never()).startFan();
        verify(ordonnanceur,never()).readData();
        verify(ordonnanceur).stopFan();
        verify(cmd).execute();
    }

    @Test
    public void test_setSpiMode() throws Exception {
        SetSpiModeCmd spi=mock(SetSpiModeCmd.class);
        whenNew(SetSpiModeCmd.class).withArguments(out, is).thenReturn(spi);

        ordonnanceur.setSpiMode();

        assertEquals(spi, ordonnanceur.current);
    }

    @Test
    public void test_checkStatus() throws Exception {
        CheckStatusCmd status=mock(CheckStatusCmd.class);
        whenNew(CheckStatusCmd.class).withArguments(out,is).thenReturn(status);

        ordonnanceur.checkStatus();

        assertEquals(status,ordonnanceur.current);
    }

    @Test
    public void test_startFan() throws Exception {
        ordonnanceur.startFan();

        assertEquals(startFanCmd,ordonnanceur.current);
    }

    @Test
    public void test_stopFan() throws Exception {
        ordonnanceur.stopFan();

        assertEquals(stopFanCmd,ordonnanceur.current);
    }

    @Test
    public void test_readData() throws Exception {
        ordonnanceur.readData();

        assertEquals(particuleCmd,ordonnanceur.current);
    }

    @Test
    public void test_haveValues_returnFalse(){
        ordonnanceur.values=null;

        assertFalse(ordonnanceur.haveValues());
    }

    @Test
    public void test_haveValues_returnTrue(){
        String[] vals={"val1","val2"};
        ordonnanceur.values=vals;

        assertTrue(ordonnanceur.haveValues());
    }

    @Test
    public void test_getValues(){
        String[] vals={"val1","val2"};
        ordonnanceur.values=vals;

        assertArrayEquals(vals, ordonnanceur.getValues());
        assertNull(ordonnanceur.values);
    }

    @Test
    public void test_read_withCmdReadDataFinish(){
        String[] vals={"val1","val2"};
        ordonnanceur.cmd=4;
        ordonnanceur.current=particuleCmd;

        doNothing().when(particuleCmd).read();
        when(particuleCmd.isFinish()).thenReturn(true);
        when(particuleCmd.getValues()).thenReturn(vals);

        ordonnanceur.read();

        assertArrayEquals(vals, ordonnanceur.values);
        assertEquals(4, ordonnanceur.cmd);

        verify(particuleCmd).read();
        verify(particuleCmd).isFinish();
        verify(particuleCmd).getValues();
    }

    @Test
    public void test_read_withCmdReadFirstDataFinish(){
        String[] vals={"val1","val2"};
        ordonnanceur.cmd=3;
        ordonnanceur.current=particuleCmd;

        doNothing().when(particuleCmd).read();
        when(particuleCmd.isFinish()).thenReturn(true);
        when(particuleCmd.getResponse()).thenReturn("vals");

        ordonnanceur.read();

        assertNull(ordonnanceur.values);
        assertEquals(4, ordonnanceur.cmd);

        verify(particuleCmd).read();
        verify(particuleCmd).isFinish();
        verify(particuleCmd).getResponse();
    }
}