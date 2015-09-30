package org.apolline.configuration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apolline.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.http.impl.client.HttpClients;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class,HttpClients.class,Json.class})
public class ConfigurationTest{

    private Configuration configEmpty;
    private Configuration config;

    @Before
    public void setUp() throws IOException {
        configEmpty=new Configuration("src/main/resources/emptyConfTest.properties"){
            protected void findModuleInformation(){};
        };
        config=new Configuration("src/main/resources/confTest.properties"){
            protected void findModuleInformation(){};
        };
    }

    @Test(expected = FileNotFoundException.class)
    public void testFileNotFound() throws IOException {
        new Configuration("ok"){
            protected void findModuleInformation(){};
        };
    }

    @Test
    public void testFileFound() throws IOException {
        new Configuration("src/main/resources/confTest.properties"){
            protected void findModuleInformation(){};
        };
    }

    @Test
    public void testDeviceNotFound(){
        String device=configEmpty.getValueOf(Configuration.DEVICE);
        assertEquals(device, "/dev/ttyUSB0");
    }

    @Test
    public void testDevice(){
        String device=config.getValueOf(Configuration.DEVICE);
        assertEquals(device, "/dev/ttyACM0");
    }

    @Test
    public void testTypeNotFound(){
        String type=configEmpty.getValueOf(Configuration.TYPE);
        assertNull(type);
    }

    @Test
    public void testType(){
        String type=config.getValueOf(Configuration.TYPE);
        assertEquals(type, "ADC");
    }

    @Test
    public void testTimeoutNotFound(){
        int timeout=configEmpty.getIntValueOf(Configuration.TIMEOUT);
        assertEquals(timeout, 10000);
    }

    @Test
    public void testTimeout(){
        int timeout=config.getIntValueOf(Configuration.TIMEOUT);
        assertEquals(timeout, 9);
    }

    @Test
    public void testBaudNotFound(){
        int baud=configEmpty.getIntValueOf(Configuration.BAUD);
        assertEquals(baud, 9600);
    }

    @Test
    public void testBaud(){
        int baud=config.getIntValueOf(Configuration.BAUD);
        assertEquals(baud, 12600);
    }

    @Test
    public void testBitsNotFound(){
        int bits=configEmpty.getIntValueOf(Configuration.BITS);
        assertEquals(bits, 8);
    }

    @Test
    public void testBits(){
        int bits=config.getIntValueOf(Configuration.BITS);
        assertEquals(bits, 5);
    }

    @Test
    public void testStopBitsNotFound(){
        int sbits=configEmpty.getIntValueOf(Configuration.STOP_BITS);
        assertEquals(sbits, 1);
    }

    @Test
    public void testStopBits(){
        int sbits=config.getIntValueOf(Configuration.STOP_BITS);
        assertEquals(sbits, 2);
    }

    @Test
    public void testParityNotFound(){
        int parity=configEmpty.getIntValueOf(Configuration.PARITY);
        assertEquals(parity, 0);
    }

    @Test
    public void testParity(){
        int parity=config.getIntValueOf(Configuration.PARITY);
        assertEquals(parity, 1);
    }

    @Test
    public void testSensorsNumber(){
        int sensorsNumber=config.getIntValueOf("numberOfValue");
        assertEquals(6,sensorsNumber);
    }

    @Test
    public void testNotFound(){
        assertNull(configEmpty.getIntValueOf("key"));
    }

    @Test
    public void testGetSensors(){
        Map<String,List<SensorConfig>> sensors=new HashMap<String,List<SensorConfig>>();
        List<SensorConfig> list1=new ArrayList<SensorConfig>();
        list1.add(new SensorConfig(0,"55c1b2403600008e0001031d","55c1b2403600008e0001030d"));
        List<SensorConfig> list2=new ArrayList<SensorConfig>();
        list2.add(new SensorConfig(1,"55c1b2403600008e0001031e","55c1b2403600008e0001030e"));
        sensors.put("55472b1439000049000afea7",list1);
        sensors.put("55472b1439000049000afea8",list2);
        Map<String,List<SensorConfig>> res=config.getSensors();

        assertEquals(res, sensors);
    }

    @Test
    public void testFindModuleInformationWithNoWebsite() throws Exception {
        Configuration config=mock(Configuration.class);

        when(config.getValueOf("website")).thenReturn(null);
        doCallRealMethod().when(config).findModuleInformation();

        config.findModuleInformation();

        verify(config).getValueOf("website");
        verify(config,never()).getValueOf("moduleId");
    }

    @Test
    public void testFindModuleInformationWithError() throws Exception {
        mockStatic(HttpClients.class);

        CloseableHttpClient client=mock(CloseableHttpClient.class);
        Configuration config=mock(Configuration.class);
        HttpGet get=mock(HttpGet.class);
        CloseableHttpResponse response=mock(CloseableHttpResponse.class);
        StatusLine statusLine=mock(StatusLine.class);

        when(config.getValueOf("website")).thenReturn("http://localhost:9000/");
        when(config.getValueOf("moduleId")).thenReturn("idModule");
        when(HttpClients.createDefault()).thenReturn(client);
        whenNew(HttpGet.class).withArguments("http://localhost:9000/inventary/modules/idModule/information").thenReturn(get);
        when(client.execute(get)).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(500);
        doCallRealMethod().when(config).findModuleInformation();

        config.findModuleInformation();

        verify(config,times(2)).getValueOf("website");
        verify(config).getValueOf("moduleId");
        verifyNew(HttpGet.class).withArguments("http://localhost:9000/inventary/modules/idModule/information");
        verify(client).execute(get);
        verify(response).getStatusLine();
        verify(statusLine).getStatusCode();
        verify(response,never()).getEntity();
    }

    @Test
    public void testFindModuleInformationWithoutGPSCoordinates() throws Exception {
        mockStatic(HttpClients.class);
        mockStatic(Json.class);

        CloseableHttpClient client=mock(CloseableHttpClient.class);
        Configuration config=mock(Configuration.class);
        HttpGet get=mock(HttpGet.class);
        CloseableHttpResponse response=mock(CloseableHttpResponse.class);
        StatusLine statusLine=mock(StatusLine.class);
        HttpEntity entity=mock(HttpEntity.class);
        InputStream is=mock(InputStream.class);
        JsonReader reader=mock(JsonReader.class);
        JsonObject obj=mock(JsonObject.class);
        Properties prop=mock(Properties.class);

        config.prop=prop;
        when(config.getValueOf("website")).thenReturn("http://localhost:9000/");
        when(config.getValueOf("moduleId")).thenReturn("idModule");
        when(HttpClients.createDefault()).thenReturn(client);
        whenNew(HttpGet.class).withArguments("http://localhost:9000/inventary/modules/idModule/information").thenReturn(get);
        when(client.execute(get)).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getEntity()).thenReturn(entity);
        when(entity.getContent()).thenReturn(is);
        when(Json.createReader(is)).thenReturn(reader);
        when(reader.readObject()).thenReturn(obj);
        when(obj.getString("dataToken")).thenReturn("token");
        when(obj.getString("collectUrl")).thenReturn("url");
        when(obj.getString("condition")).thenReturn("condId");
        when(prop.setProperty("dataToken", "token")).thenReturn(null);
        when(prop.setProperty("collectUrl","url")).thenReturn(null);
        when(prop.setProperty("condition", "condId")).thenReturn(null);
        doCallRealMethod().when(config).findModuleInformation();

        config.findModuleInformation();

        verify(config, times(2)).getValueOf("website");
        verify(config).getValueOf("moduleId");
        verifyNew(HttpGet.class).withArguments("http://localhost:9000/inventary/modules/idModule/information");
        verify(client).execute(get);
        verify(response).getStatusLine();
        verify(statusLine).getStatusCode();
        verify(response).getEntity();
        verify(entity).getContent();
        verify(reader).readObject();
        verify(obj).getString("dataToken");
        verify(obj).getString("collectUrl");
        verify(obj).getString("condition");
        verify(prop).setProperty("dataToken", "token");
        verify(prop).setProperty("collectUrl","url");
        verify(prop).setProperty("condition","condId");
    }

    @Test
    public void testFindModuleInformationWithGPSCoordinates() throws Exception {
        mockStatic(HttpClients.class);
        mockStatic(Json.class);

        CloseableHttpClient client=mock(CloseableHttpClient.class);
        Configuration config=mock(Configuration.class);
        HttpGet get=mock(HttpGet.class);
        CloseableHttpResponse response=mock(CloseableHttpResponse.class);
        StatusLine statusLine=mock(StatusLine.class);
        HttpEntity entity=mock(HttpEntity.class);
        InputStream is=mock(InputStream.class);
        JsonReader reader=mock(JsonReader.class);
        JsonObject obj=mock(JsonObject.class);
        Properties prop=mock(Properties.class);

        config.prop=prop;
        when(config.getValueOf("website")).thenReturn("http://localhost:9000/");
        when(config.getValueOf("moduleId")).thenReturn("idModule");
        when(HttpClients.createDefault()).thenReturn(client);
        whenNew(HttpGet.class).withArguments("http://localhost:9000/inventary/modules/idModule/information").thenReturn(get);
        when(client.execute(get)).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getEntity()).thenReturn(entity);
        when(entity.getContent()).thenReturn(is);
        when(Json.createReader(is)).thenReturn(reader);
        when(reader.readObject()).thenReturn(obj);
        when(obj.getString("dataToken")).thenReturn("token");
        when(obj.getString("collectUrl")).thenReturn("url");
        when(obj.getString("condition")).thenReturn("condId");
        when(obj.getString("latitude")).thenReturn("2.3");
        when(obj.getString("longitude")).thenReturn("3.4");
        when(obj.containsKey("latitude")).thenReturn(true);
        when(obj.containsKey("longitude")).thenReturn(true);
        when(prop.setProperty("dataToken", "token")).thenReturn(null);
        when(prop.setProperty("collectUrl", "url")).thenReturn(null);
        when(prop.setProperty("condition", "condId")).thenReturn(null);
        when(prop.setProperty("latitude", "2.3")).thenReturn(null);
        when(prop.setProperty("longitude", "3.4")).thenReturn(null);
        doCallRealMethod().when(config).findModuleInformation();

        config.findModuleInformation();

        verify(config, times(2)).getValueOf("website");
        verify(config).getValueOf("moduleId");
        verifyNew(HttpGet.class).withArguments("http://localhost:9000/inventary/modules/idModule/information");
        verify(client).execute(get);
        verify(response).getStatusLine();
        verify(statusLine).getStatusCode();
        verify(response).getEntity();
        verify(entity).getContent();
        verify(reader).readObject();
        verify(obj).getString("dataToken");
        verify(obj).getString("collectUrl");
        verify(obj).getString("condition");
        verify(obj).getString("latitude");
        verify(obj).getString("longitude");
        verify(prop).setProperty("dataToken", "token");
        verify(prop).setProperty("collectUrl", "url");
        verify(prop).setProperty("condition","condId");
        verify(prop).setProperty("latitude", "2.3");
        verify(prop).setProperty("longitude", "3.4");
    }
}