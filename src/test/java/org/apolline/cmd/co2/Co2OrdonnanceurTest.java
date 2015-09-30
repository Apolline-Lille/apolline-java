package org.apolline.cmd.co2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Co2Ordonnanceur.class)
public class Co2OrdonnanceurTest {
    protected NCmd ncmd;
    protected TCmd tcmd;
    protected VCmd vcmd;
    protected Co2Ordonnanceur ordonnanceur;

    protected DataOutputStream out;
    protected BufferedReader is;

    @Before
    public void setUp() throws Exception {

        out=mock(DataOutputStream.class);
        is=mock(BufferedReader.class);
        ncmd=mock(NCmd.class);
        tcmd=mock(TCmd.class);
        vcmd=mock(VCmd.class);

        whenNew(NCmd.class).withArguments(out,is).thenReturn(ncmd);
        whenNew(TCmd.class).withArguments(out, is).thenReturn(tcmd);
        whenNew(VCmd.class).withArguments(out, is).thenReturn(vcmd);

        ordonnanceur=new Co2Ordonnanceur(out,is);
    }

    @Test
    public void testConstructor() throws Exception {
        Co2Cmd[] cmd={ncmd,tcmd,vcmd};
        assertArrayEquals(cmd, ordonnanceur.cmd);

        verifyNew(NCmd.class).withArguments(out, is);
        verifyNew(TCmd.class).withArguments(out, is);
        verifyNew(VCmd.class).withArguments(out, is);
    }

    @Test
    public void test_execute1() throws IOException {
        ordonnanceur.cmdNumber=0;
        doNothing().when(ncmd).execute();

        ordonnanceur.execute();

        verify(ncmd).execute();
    }

    @Test
    public void test_execute2() throws IOException {
        ordonnanceur.cmdNumber=1;
        doNothing().when(tcmd).execute();

        ordonnanceur.execute();

        verify(tcmd).execute();
    }

    @Test
    public void test_execute3() throws IOException {
        ordonnanceur.cmdNumber=2;
        doNothing().when(vcmd).execute();

        ordonnanceur.execute();

        verify(vcmd).execute();
    }

    @Test
    public void test_getValues(){
        String val[]={"2.3","3.4","4.5"};
        ordonnanceur.values=val;

        assertArrayEquals(val, ordonnanceur.getValues());
        assertNull(ordonnanceur.values);
    }

    @Test
    public void test_hasValue_returnFalse(){
        ordonnanceur.values=null;

        assertFalse(ordonnanceur.haveValues());
    }

    @Test
    public void test_hasValue_returnTrue(){
        String val[]={"2.3","3.4","4.5"};
        ordonnanceur.values=val;

        assertTrue(ordonnanceur.haveValues());
    }

    @Test
    public void test_read(){
        ordonnanceur.cmdNumber=0;
        doNothing().when(ncmd).read();
        when(ncmd.getResponse()).thenReturn("2.3");

        ordonnanceur.read();

        assertEquals("2.3", ordonnanceur.values[0]);
        assertEquals(1, ordonnanceur.cmdNumber);

        verify(ncmd).read();
        verify(ncmd).getResponse();
    }

    @Test
    public void test_read1(){
        ordonnanceur.cmdNumber=1;
        doNothing().when(tcmd).read();
        when(tcmd.getResponse()).thenReturn("2.3");

        ordonnanceur.read();

        assertEquals("2.3", ordonnanceur.values[1]);
        assertEquals(2, ordonnanceur.cmdNumber);

        verify(tcmd).read();
        verify(tcmd).getResponse();
    }

    @Test
    public void test_read2(){
        ordonnanceur.cmdNumber=2;
        doNothing().when(vcmd).read();
        when(vcmd.getResponse()).thenReturn("2.3");

        ordonnanceur.read();

        assertEquals("2.3", ordonnanceur.values[2]);
        assertEquals(0, ordonnanceur.cmdNumber);

        verify(vcmd).read();
        verify(vcmd).getResponse();
    }
}
