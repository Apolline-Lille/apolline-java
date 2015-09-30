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

public class StopFanCmdTest {
    protected OutputStream out;
    protected InputStream is;
    protected byte[] val_cmd1={(byte)0x61,(byte)0x03};
    protected byte[] val_cmd2={(byte)0x61,(byte)0x01};

    protected StopFanCmd cmd;

    @Before
    public void setUp(){
        out=mock(OutputStream.class);
        is=mock(InputStream.class);

        cmd=new StopFanCmd(out,is);
    }

    @Test
    public void testExecute() throws IOException {
        doNothing().when(out).write(val_cmd1);

        cmd.execute();

        verify(out).write(val_cmd1);
    }

    @Test
    public void testExecute2() throws IOException {
        cmd.nbResponse=1;
        doNothing().when(out).write(val_cmd2);

        cmd.execute();

        verify(out).write(val_cmd2);
    }

    @Test
    public void test_findCmd(){
        assertArrayEquals(val_cmd1, cmd.findCmd());
    }

    @Test
    public void test_findCmd2(){
        cmd.nbResponse=1;
        assertArrayEquals(val_cmd2, cmd.findCmd());
    }

    @Test
    public void test_isFinish_returnFalse(){
        cmd.nbResponse=1;

        assertFalse(cmd.isFinish());
    }

    @Test
    public void test_isFinish_returnFalse2(){
        cmd.nbResponse=3;

        assertFalse(cmd.isFinish());
    }

    @Test
    public void test_isFinish_returnTrue(){
        cmd.nbResponse=2;

        assertTrue(cmd.isFinish());
    }

    @Test
    public void test_findData_FirstValueIsNotGood(){
        cmd.nbResponse=0;
        cmd.readByte[1]=(byte)0x00;

        cmd.findData();

        assertEquals(0, cmd.nbResponse);
    }

    @Test
    public void test_findData_FirstValueIsGood(){
        cmd.nbResponse=0;
        cmd.readByte[1]=(byte)0xF3;

        cmd.findData();

        assertEquals(1,cmd.nbResponse);
    }

    @Test
    public void test_findData_SecondValue(){
        cmd.nbResponse=1;

        cmd.findData();

        assertEquals(2,cmd.nbResponse);
    }
}
