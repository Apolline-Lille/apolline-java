package org.apolline.cmd.particule;


import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CheckStatusCmdTest {

    protected OutputStream out;
    protected InputStream is;
    protected byte[] val_cmd={(byte)0x61,(byte)0xCF};

    protected CheckStatusCmd cmd;

    @Before
    public void setUp(){
        out=mock(OutputStream.class);
        is=mock(InputStream.class);

        cmd=new CheckStatusCmd(out,is);
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
        cmd.readByte[1]=(byte)0x00;

        assertFalse(cmd.isFinish());
    }

    @Test
    public void test_isFinish_returnTrue(){
        cmd.readByte[1]=(byte)0xF3;

        assertTrue(cmd.isFinish());
    }
}
