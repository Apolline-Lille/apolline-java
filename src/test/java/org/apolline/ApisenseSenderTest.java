package org.apolline;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apolline.configuration.SensorConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.portable.ResponseHandler;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApisenseSender.class,Thread.class,Runnable.class,HttpClients.class,HttpPost.class})
public class ApisenseSenderTest {

    private ApisenseSender sender;

    @Before
    public void setUp(){
        sender=new ApisenseSender("url","token");
    }

    @Test
    public void testCreateCorrectJSON(){
        HashMap<String,List<SensorConfig>> sensors=new HashMap<String,List<SensorConfig>>();
        List<SensorConfig> list1=new ArrayList<SensorConfig>();
        list1.add(new SensorConfig(1,"esp1","info1"));
        List<SensorConfig> list2=new ArrayList<SensorConfig>();
        list2.add(new SensorConfig(3,"esp3","info3"));
        sensors.put("id1",list1);
        sensors.put("id2",list2);
        Date d=new Date();
        String values[]={"0","3.5","1","6"};

        String res=sender.createJson("moduleId","condId","2.3","4.5",sensors,d,values,5);

        String sensorsJson="{\"sensorId\":\"id2\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"6\",\"especeId\":\"esp3\",\"infoId\":\"info3\"}]},{\"sensorId\":\"id1\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"3.5\",\"especeId\":\"esp1\",\"infoId\":\"info1\"}]}";
        assertEquals(res, "{\"timestamp\":" + d.getTime() + ",\"moduleId\":\"moduleId\",\"conditionId\":\"condId\",\"latitude\":\"2.3\",\"longitude\":\"4.5\",\"index\":5,\"sensors\":[" + sensorsJson + "]}");
    }

    @Test
    public void testCreateCorrectJSON2(){
        HashMap<String,List<SensorConfig>> sensors=new HashMap<String,List<SensorConfig>>();
        List<SensorConfig> list1=new ArrayList<SensorConfig>();
        list1.add(new SensorConfig(3,"esp3","info3"));
        List<SensorConfig> list2=new ArrayList<SensorConfig>();
        list2.add(new SensorConfig(1,"esp1","info1"));
        sensors.put("id1",list1);
        sensors.put("id2",list2);
        Date d=new Date();
        String values[]={"0","3.5","1","6"};

        String res=sender.createJson("moduleId","condId","2.3","4.5",sensors,d,values,10000);

        String sensorsJson="{\"sensorId\":\"id2\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"3.5\",\"especeId\":\"esp1\",\"infoId\":\"info1\"}]},{\"sensorId\":\"id1\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"6\",\"especeId\":\"esp3\",\"infoId\":\"info3\"}]}";
        assertEquals(res, "{\"timestamp\":" + d.getTime() + ",\"moduleId\":\"moduleId\",\"conditionId\":\"condId\",\"latitude\":\"2.3\",\"longitude\":\"4.5\",\"index\":10000,\"sensors\":[" + sensorsJson + "]}");
    }

    @Test
    public void testCreateCorrectJSONWithoutCoordinates(){
        HashMap<String,List<SensorConfig>> sensors=new HashMap<String,List<SensorConfig>>();
        List<SensorConfig> list1=new ArrayList<SensorConfig>();
        list1.add(new SensorConfig(3,"esp3","info3"));
        List<SensorConfig> list2=new ArrayList<SensorConfig>();
        list2.add(new SensorConfig(1,"esp1","info1"));
        sensors.put("id1",list1);
        sensors.put("id2",list2);
        Date d=new Date();
        String values[]={"0","3.5","1","6"};

        String res=sender.createJson("moduleId","condId",null,null,sensors,d,values,10000);

        String sensorsJson="{\"sensorId\":\"id2\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"3.5\",\"especeId\":\"esp1\",\"infoId\":\"info1\"}]},{\"sensorId\":\"id1\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"6\",\"especeId\":\"esp3\",\"infoId\":\"info3\"}]}";
        assertEquals(res, "{\"timestamp\":" + d.getTime() + ",\"moduleId\":\"moduleId\",\"conditionId\":\"condId\",\"index\":10000,\"sensors\":[" + sensorsJson + "]}");
    }
/*
    @Test
    public void testSendDataToApisense() throws Exception {
        Map<String,List<SensorConfig>> sensors=new HashMap<String,List<SensorConfig>>();
        List<SensorConfig> list1=new ArrayList<SensorConfig>();
        list1.add(new SensorConfig(1,"esp1","info1"));
        List<SensorConfig> list2=new ArrayList<SensorConfig>();
        list2.add(new SensorConfig(3,"esp3","info3"));
        sensors.put("id1",list1);
        sensors.put("id2",list2);
        Date d=new Date();
        String values[]={"0","3.5","1","6"};
        String sensorsJson="{\"sensorId\":\"id2\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"6\",\"especeId\":\"esp3\",\"infoId\":\"info3\"}]},{\"sensorId\":\"id1\",\"mesures\":[{\"signalId\":1,\"rawValue\":\"3.5\",\"especeId\":\"esp1\",\"infoId\":\"info1\"}]}";
        CloseableHttpClient httpClient=mock(CloseableHttpClient.class);
        HttpPost httpPost=mock(HttpPost.class);
        StringEntity entity=mock(StringEntity.class);
        CloseableHttpResponse resp=mock(CloseableHttpResponse.class);
        HttpEntity respEntity=mock(HttpEntity.class);

        mockStatic(HttpClients.class);
        when(HttpClients.createDefault()).thenReturn(httpClient);
        whenNew(HttpPost.class).withArguments("url").thenReturn(httpPost);
        whenNew(StringEntity.class).withArguments("{\"timestamp\":" + d.getTime() + ",\"moduleId\":\"moduleId\",\"conditionId\":\"condId\",\"latitude\":\"2.3\",\"longitude\":\"4.5\",\"index\":1000,\"sensors\":[" + sensorsJson + "]}", ContentType.APPLICATION_JSON).thenReturn(entity);
        doNothing().when(httpPost).setEntity(entity);
        doNothing().when(httpPost).setHeader("Authorization", "dataToken token");
        when(httpClient.execute(eq(httpPost))).thenReturn(resp);
        when(resp.getEntity()).thenReturn(respEntity);
        when(respEntity.getContent()).thenReturn(new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        });
        doNothing().when(httpClient).close();

        sender.send("moduleId", "condId", "2.3", "4.5", sensors, d, values, 1000);

        verifyNew(HttpPost.class).withArguments("url");
        verifyNew(StringEntity.class).withArguments("{\"timestamp\":" + d.getTime() + ",\"moduleId\":\"moduleId\",\"conditionId\":\"condId\",\"latitude\":\"2.3\",\"longitude\":\"4.5\",\"index\":1000,\"sensors\":[" + sensorsJson + "]}", ContentType.APPLICATION_JSON);
        verify(httpPost).setEntity(entity);
        verify(httpPost).setHeader("Authorization", "dataToken token");
        verify(httpClient).execute(eq(httpPost));
        verify(resp).getEntity();
        verify(respEntity).getContent();
        verify(httpClient).close();
    }*/
}