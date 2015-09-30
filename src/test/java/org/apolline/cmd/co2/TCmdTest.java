package org.apolline.cmd.co2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NCmd.class)
public class TCmdTest {

    protected DataOutputStream out;
    protected BufferedReader is;

    protected TCmd cmd;

    @Before
    public void setUp(){
        out=mock(DataOutputStream.class);
        is=mock(BufferedReader.class);

        cmd=new TCmd(out,is);
    }

    @Test
    public void test_findCmd(){
        assertEquals("T\r",cmd.findCmd());
    }

    @Test
    public void test_execute() throws IOException {
        cmd.finish=true;
        doNothing().when(out).writeBytes("T\r");

        cmd.execute();

        assertFalse(cmd.finish);

        verify(out).writeBytes("T\r");
    }

    @Test
    public void test_read() throws IOException {
        cmd.resp="";
        cmd.finish=false;

        when(is.readLine()).thenReturn("2.3");

        cmd.read();

        assertEquals("2.3", cmd.resp);
        assertTrue(cmd.finish);

        verify(is).readLine();
    }
}