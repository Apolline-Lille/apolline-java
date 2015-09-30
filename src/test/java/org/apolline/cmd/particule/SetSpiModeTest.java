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

public class SetSpiModeTest {
    protected OutputStream out;
    protected InputStream is;
    protected byte[] val_cmd={(byte)0x5A,(byte)0x02,(byte)0x92,(byte)0x0B};

    protected SetSpiModeCmd cmd;

    @Before
    public void setUp(){
        out=mock(OutputStream.class);
        is=mock(InputStream.class);

        cmd=new SetSpiModeCmd(out,is);
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
        cmd.readByte[0]=(byte)0x00;

        assertFalse(cmd.isFinish());
    }

    @Test
    public void test_isFinish_returnTrue(){
        cmd.readByte[0]=(byte)0xFF;

        assertTrue(cmd.isFinish());
    }
}
