package org.apolline.reader;

import org.apolline.ApisenseSender;
import org.apolline.cmd.Ordonnanceur;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApisenseSender.class,CommandeReader.class})
public class CommandeReaderTest {
    protected Ordonnanceur ordonnanceur;
    protected CommandeReader reader;
    protected Map sensors;
    protected ApisenseSender sender;
    protected GregorianCalendar date;

    @Before
    public void setUp() throws Exception {
        sender=mock(ApisenseSender.class);
        date=new GregorianCalendar();
        whenNew(GregorianCalendar.class).withAnyArguments().thenReturn(date);

        mockStatic(ApisenseSender.class);
        when(ApisenseSender.getInstance("url","token")).thenReturn(sender);
        ordonnanceur=mock(Ordonnanceur.class);
        sensors= mock(Map.class);
        reader=new CommandeReader(ordonnanceur,"url","token","moduleId","condId","2.3","4.5",3,sensors,10);
    }

    @Test
    public void test_readData_with_no_values(){
        doNothing().when(ordonnanceur).read();
        when(ordonnanceur.haveValues()).thenReturn(false);
        doNothing().when(ordonnanceur).execute();

        reader.readData();

        verify(ordonnanceur).read();
        verify(ordonnanceur).haveValues();
        verify(ordonnanceur,never()).getValues();
        verify(ordonnanceur).execute();
    }

    @Test
    public void test_readData() {
        reader.index=53;
        String vals[]={"val1","val2"};

        doNothing().when(ordonnanceur).read();
        when(ordonnanceur.haveValues()).thenReturn(true);
        doNothing().when(ordonnanceur).execute();
        when(ordonnanceur.getValues()).thenReturn(vals);
        doNothing().when(sender).send("moduleId","condId","2.3","4.5", sensors, date.getTime(), vals, 53);

        reader.readData();
        assertEquals(54, reader.index);

        verify(ordonnanceur).read();
        verify(ordonnanceur).haveValues();
        verify(ordonnanceur).execute();
        verify(ordonnanceur).getValues();
        verify(sender).send("moduleId","condId","2.3","4.5", sensors, date.getTime(), vals, 53);

    }
}
