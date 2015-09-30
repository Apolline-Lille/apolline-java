package org.apolline.cmd.particule;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReadBin2CmdTest {
    protected OutputStream out;
    protected InputStream is;
    protected byte[] val_cmd={(byte)0x61,(byte)0x30};

    protected ReadBin2Cmd cmd;

    @Before
    public void setUp(){
        out=mock(OutputStream.class);
        is=mock(InputStream.class);

        cmd=new ReadBin2Cmd(out,is);
    }

    @Test
    public void testExecute() throws IOException {
        doNothing().when(out).write(val_cmd);

        cmd.execute();

        verify(out).write(val_cmd);
    }

    @Test
    public void test_findCmd(){
        assertArrayEquals(val_cmd, cmd.findCmd());
    }

    @Test
    public void test_isFinish_returnFalse(){
        cmd.nbResponse=0;

        assertFalse(cmd.isFinish());
    }

    @Test
    public void test_isFinish_returnFalse2(){
        cmd.nbResponse=2;

        assertFalse(cmd.isFinish());
    }

    @Test
    public void test_isFinish_returnTrue(){
        cmd.nbResponse=1;

        assertTrue(cmd.isFinish());
    }

    @Test
    public void testFindData(){
        cmd.nbResponse=0;

        cmd.findData();

        assertEquals(1, cmd.nbResponse);
    }

    @Test
    public void test_parseResponse(){
        cmd.readByte[1]=(byte)0x11;
        cmd.resp=null;

        cmd.parseResponse();

        assertEquals("17", cmd.resp);
    }
}
