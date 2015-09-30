package org.apolline.cmd.particule;


import org.apolline.cmd.Cmd;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ParticuleDataOrdonnanceur.class)
public class ParticuleDataOrdonnanceurTest {

    protected BeginReadCmd beginCmd;
    protected ReadBinCmd binCmd;
    protected ReadBin2Cmd bin2Cmd;
    protected ReadCRCCmd crcCmd;
    protected ReadUnsignedInt32Cmd unsignedCmd;
    protected ReadFloatCmd floatCmd;
    protected ParticuleDataOrdonnanceur ordonnanceur;

    protected OutputStream out;
    protected InputStream is;

    @Before
    public void setUp() throws Exception {
        out=mock(OutputStream.class);
        is=mock(InputStream.class);
        beginCmd=mock(BeginReadCmd.class);
        binCmd=mock(ReadBinCmd.class);
        bin2Cmd=mock(ReadBin2Cmd.class);
        crcCmd=mock(ReadCRCCmd.class);
        unsignedCmd=mock(ReadUnsignedInt32Cmd.class);
        floatCmd=mock(ReadFloatCmd.class);

        whenNew(BeginReadCmd.class).withArguments(out,is).thenReturn(beginCmd);
        whenNew(ReadBinCmd.class).withArguments(out,is).thenReturn(binCmd);
        whenNew(ReadBin2Cmd.class).withArguments(out,is).thenReturn(bin2Cmd);
        whenNew(ReadCRCCmd.class).withArguments(out,is).thenReturn(crcCmd);
        whenNew(ReadUnsignedInt32Cmd.class).withArguments(out,is).thenReturn(unsignedCmd);
        whenNew(ReadFloatCmd.class).withArguments(out,is).thenReturn(floatCmd);

        ordonnanceur=new ParticuleDataOrdonnanceur(out,is);
    }

    @Test
    public void test_constructor() throws Exception {
        verifyNew(BeginReadCmd.class).withArguments(out, is);
        verifyNew(ReadBinCmd.class).withArguments(out, is);
        verifyNew(ReadBin2Cmd.class).withArguments(out, is);
        verifyNew(ReadCRCCmd.class).withArguments(out, is);
        verifyNew(ReadUnsignedInt32Cmd.class).withArguments(out, is);
        verifyNew(ReadFloatCmd.class).withArguments(out, is);
    }

    @Test
    public void test_execute_with_readBegin() throws IOException {
        ordonnanceur.cmd=0;

        doNothing().when(beginCmd).execute();

        ordonnanceur.execute();

        assertEquals(beginCmd, ordonnanceur.current);

        verify(beginCmd).execute();
    }

    @Test
    public void test_execute_with_readBin_1() throws IOException {
        ordonnanceur.cmd=1;

        doNothing().when(binCmd).execute();

        ordonnanceur.execute();

        assertEquals(binCmd, ordonnanceur.current);

        verify(binCmd).execute();
    }

    @Test
    public void test_execute_with_readBin_2() throws IOException {
        ordonnanceur.cmd=16;

        doNothing().when(binCmd).execute();

        ordonnanceur.execute();

        assertEquals(binCmd, ordonnanceur.current);

        verify(binCmd).execute();
    }

    @Test
    public void test_execute_with_readBin2_1() throws IOException {
        ordonnanceur.cmd=17;

        doNothing().when(bin2Cmd).execute();

        ordonnanceur.execute();

        assertEquals(bin2Cmd, ordonnanceur.current);

        verify(bin2Cmd).execute();
    }

    @Test
    public void test_execute_with_readBin2_2() throws IOException {
        ordonnanceur.cmd=20;

        doNothing().when(bin2Cmd).execute();

        ordonnanceur.execute();

        assertEquals(bin2Cmd, ordonnanceur.current);

        verify(bin2Cmd).execute();
    }

    @Test
    public void test_execute_with_readUnsignedInt_1() throws IOException {
        ordonnanceur.cmd=21;

        doNothing().when(unsignedCmd).execute();

        ordonnanceur.execute();

        assertEquals(unsignedCmd, ordonnanceur.current);

        verify(unsignedCmd).execute();
    }

    @Test
    public void test_execute_with_readUnsignedInt_2() throws IOException {
        ordonnanceur.cmd=23;

        doNothing().when(unsignedCmd).execute();

        ordonnanceur.execute();

        assertEquals(unsignedCmd, ordonnanceur.current);

        verify(unsignedCmd).execute();
    }

    @Test
    public void test_execute_with_readCrc() throws IOException {
        ordonnanceur.cmd=24;

        doNothing().when(crcCmd).execute();

        ordonnanceur.execute();

        assertEquals(crcCmd, ordonnanceur.current);

        verify(crcCmd).execute();
    }

    @Test
    public void test_execute_with_readFloat() throws IOException {
        ordonnanceur.cmd=25;

        doNothing().when(floatCmd).execute();

        ordonnanceur.execute();

        assertEquals(floatCmd, ordonnanceur.current);

        verify(floatCmd).execute();
    }

    @Test
    public void test_getValues(){
        String[] vals={"val1","val2"};
        ordonnanceur.cmd=1;
        ordonnanceur.values=vals;

        assertArrayEquals(vals, ordonnanceur.getValues());
        assertEquals(0, ordonnanceur.cmd);
    }

    @Test
    public void test_haveValues_return_false(){
        ordonnanceur.cmd=27;

        assertFalse(ordonnanceur.haveValues());
    }

    @Test
    public void test_haveValues_return_false2(){
        ordonnanceur.cmd=29;

        assertFalse(ordonnanceur.haveValues());
    }

    @Test
    public void test_haveValues_return_true(){
        ordonnanceur.cmd=28;

        assertTrue(ordonnanceur.haveValues());
    }

    @Test
    public void test_read_with_not_find_the_good_begin_flag(){
        ParticuleCmd cmd=mock(ParticuleCmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=0;

        doNothing().when(cmd).read();
        when(cmd.isFinish()).thenReturn(false);

        ordonnanceur.read();

        verify(cmd).read();
        verify(cmd).isFinish();
        verify(cmd,never()).getResponse();
        verify(cmd,never()).init();
    }

    @Test
    public void test_read_with_find_the_good_begin_flag(){
        ParticuleCmd cmd=mock(ParticuleCmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=0;

        doNothing().when(cmd).read();
        when(cmd.isFinish()).thenReturn(true);
        doNothing().when(cmd).init();

        ordonnanceur.read();

        assertEquals(1, ordonnanceur.cmd);

        verify(cmd).read();
        verify(cmd,times(2)).isFinish();
        verify(cmd,never()).getResponse();
        verify(cmd).init();
    }

    @Test
    public void test_read(){
        ParticuleCmd cmd=mock(ParticuleCmd.class);
        ordonnanceur.current=cmd;
        ordonnanceur.cmd=2;

        doNothing().when(cmd).read();
        when(cmd.isFinish()).thenReturn(true);
        doNothing().when(cmd).init();
        when(cmd.getResponse()).thenReturn("val");

        ordonnanceur.read();

        assertEquals(3, ordonnanceur.cmd);
        assertEquals("val",ordonnanceur.values[1]);

        verify(cmd).read();
        verify(cmd).isFinish();
        verify(cmd).getResponse();
        verify(cmd).init();
    }

    @Test
    public void test_getResponse(){
        String vals[]={"v0","v1","v2","v3","v4","v5","v6","v7","v8","v9",
                "v10","v11","v12","v13","v14","v15","v16","v17","v18","v19",
                "v20","v21","v22","v23","v24","v25","v26"
        };
        ordonnanceur.cmd=28;
        ordonnanceur.values=vals;
        String resp="v0;v1;v2;v3;v4;v5;v6;v7;v8;v9;v10;v11;v12;v13;v14;v15;v16;v17;v18;v19;v20;v21;v22;v23;v24;v25;v26";

        assertEquals(resp, ordonnanceur.getResponse());
        assertEquals(0, ordonnanceur.cmd);
    }

    @Test
    public void test_isFinish_return_false(){
        ordonnanceur.cmd=27;

        assertFalse(ordonnanceur.isFinish());
    }

    @Test
    public void test_isFinish_return_false2(){
        ordonnanceur.cmd=29;

        assertFalse(ordonnanceur.isFinish());
    }

    @Test
    public void test_isFinish_return_true(){
        ordonnanceur.cmd=28;

        assertTrue(ordonnanceur.isFinish());
    }
}