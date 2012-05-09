/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.buffer.internal;

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.api.feed.DataProvider.LOS;
import gov.nasa.arc.mct.feed.FeedAggregatorService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FeedAggregatorServiceTest {
    private DataBuffer dataBuffer;
    private DataBuffer memoryBuffer;
    private FeedAggregatorService feedAggregatorService;
    private String testFeedID1 = "TestPui1";
    private String testFeedID2 = "TestPui2";
    private File bufferLocation;

    @Mock
    private DataProvider mockDataProvider;
    private List<Map<String, String>> data;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setup() throws IOException {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("buffer.partitions", "1");
        prop.put("buffer.time.millis", "-1");
        bufferLocation = File.createTempFile("mct-buffer", "");
        bufferLocation.delete();
        Assert.assertTrue(bufferLocation.mkdir());
        prop.put("buffer.disk.loc", bufferLocation.toString());
        dataBuffer = DataBufferFactory.getFastDiskDataBuffer(prop);
        if (dataBuffer.isDataBufferClose()) {
            dataBuffer.reset();
        }
        memoryBuffer = DataBufferFactory.getMemoryDataBuffer(prop);
        feedAggregatorService = new FeedAggregatorService(prop);
        
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockDataProvider.getLOS()).thenReturn(LOS.slow);
        data = new ArrayList<Map<String,String>>();
        Map<String, String> value1 = new HashMap<String, String>();
        value1.put("value", "1.3");
        value1.put("status", "ok");
        data.add(value1);
        
        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "1.4");
        value2.put("status", "ok");
        data.add(value2);
        Map<String, SortedMap<Long, Map<String, String>>> mapData = new HashMap<String, SortedMap<Long, Map<String,String>>>();
        SortedMap<Long, Map<String, String>> sortedTimeData = new TreeMap<Long, Map<String,String>>();
        sortedTimeData.put(System.currentTimeMillis(), value1);
        sortedTimeData.put(System.currentTimeMillis()+100, value2);
        
        mapData.put(testFeedID1, sortedTimeData);
        Mockito.when(mockDataProvider.getData(Mockito.anySet(), Mockito.anyLong(), Mockito.anyLong(), Mockito.<TimeUnit>any())).thenReturn(mapData);
    }

    @AfterMethod
    public void reset() {
        if (dataBuffer != null) {
            dataBuffer.closeBuffer();
        }
        if (memoryBuffer != null) {
            memoryBuffer.closeBuffer();
        }
        DataBufferFactory.reset();
        delete(bufferLocation);
    }

    private void delete(File f) {
        if (f.isDirectory()) {
            for (File f2 : f.listFiles()) {
                delete(f2);
            }
        }
        f.delete();
    }

    @Test(groups="feedAggregatorServiceTest")
    public void multipleDataProvidersTest() {
        long time = System.currentTimeMillis();
        List<Map<String, String>> returnData = feedAggregatorService.getData(Collections.singleton(testFeedID1), TimeUnit.MILLISECONDS, time, time).get(testFeedID1);
        
        Assert.assertNull(returnData);
        
        feedAggregatorService.addDataProvider(mockDataProvider);
        
        returnData = feedAggregatorService.getData(Collections.singleton(testFeedID1), TimeUnit.MILLISECONDS, time, time+100).get(testFeedID1);
        Assert.assertEquals(returnData.size(), 2);
        assertHasSameValue(returnData.get(0), data.get(0));
        assertHasSameValue(returnData.get(1), data.get(1));
    }

    @Test(groups="feedAggregatorServiceTest")
    public void multipleDataProvidersTest2() throws InterruptedException {
        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        feedAggregatorService.putData(testFeedID2, TimeUnit.MILLISECONDS, time, value);

        feedAggregatorService.addDataProvider(mockDataProvider);

        Set<String> feedIDs = new HashSet<String>();
        feedIDs.add(testFeedID1);
        feedIDs.add(testFeedID2);
        Map<String, List<Map<String, String>>> returnData = feedAggregatorService.getData(feedIDs, TimeUnit.MILLISECONDS, time - 10000, time);
        Assert.assertEquals(returnData.size(), 2);

        List<Map<String, String>> data1 = returnData.get(testFeedID1);
        Assert.assertEquals(data1.size(), 2);
        assertHasSameValue(data1.get(0), data.get(0));
        assertHasSameValue(data1.get(1), data.get(1));

        List<Map<String, String>> data2 = returnData.get(testFeedID2);
        Assert.assertEquals(data2.size(), 1);
        assertHasSameValue(data2.get(0), value);
    }

    @Test(groups="feedAggregatorServiceTest")
    public void putSingleTest() throws InterruptedException {
        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        feedAggregatorService.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);
        
        Thread.sleep(1000);
        
        List<Map<String, String>> returnData = feedAggregatorService.getData(Collections.singleton(testFeedID1), TimeUnit.MILLISECONDS, time, time+100).get(testFeedID1);
        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value);

        assertHasSameValue(returnValue, value);
    }
    
    private void assertHasSameValue(Map<String, String> actualValue, Map<String, String> expectedValue) {
        Assert.assertEquals(actualValue.size(), expectedValue.size());

        for (String key : actualValue.keySet()) {
            Assert.assertEquals(actualValue.get(key), expectedValue.get(key));
        }
    }
}
