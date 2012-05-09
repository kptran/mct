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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CODataBufferTest {
    private DataBuffer codataBuffer;
    private String testFeedID1 = "TestPui1";
    private String testFeedID2 = "TestPui2";
    private File bufferLocation;

    @BeforeMethod
    public void setup() throws IOException {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("buffer.partitions", "2");
        prop.put("buffer.time.millis", "-1");
        bufferLocation = File.createTempFile("mct-buffer", "");
        bufferLocation.delete();
        bufferLocation.mkdir();
        prop.put("buffer.disk.loc", bufferLocation.toString());
        DataBufferFactory.reset();
        codataBuffer = DataBufferFactory.getFastDiskDataBuffer(prop);
        if (codataBuffer.isDataBufferClose()) {
            codataBuffer.reset();
        }
    }

    @AfterMethod
    public void reset() {
        if (codataBuffer != null) {
            codataBuffer.closeBuffer();
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
    
    @Test
    public void CODReadTest() throws Exception {
        int currentPartition = getCurrentBufferPartition(codataBuffer);
        Assert.assertEquals(currentPartition, 0);

        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        codataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);

        codataBuffer.prepareForNextPartition();
        currentPartition = getCurrentBufferPartition(codataBuffer);
        Assert.assertEquals(currentPartition, 0);

        codataBuffer.moveToNextPartition();
        currentPartition = getCurrentBufferPartition(codataBuffer);
        Assert.assertEquals(currentPartition, 1);

        List<Map<String, String>> returnData = codataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.NANOSECONDS, nanotime+1000,
                nanotime + 2000).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value);

        assertHasSameValue(returnValue, value);

    }
    
    @Test
    public void prevPointTest() throws Exception {
        int currentPartition = getCurrentBufferPartition(codataBuffer);
        Assert.assertEquals(currentPartition, 0);

        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        codataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);

        Assert.assertEquals(getCurrentBufferPartition(codataBuffer), 0);

        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "1.4");
        value2.put("status", "ok");
        codataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time + 1, value2);

        Assert.assertEquals(getCurrentBufferPartition(codataBuffer), 0);

        List<Map<String, String>> returnData = codataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.NANOSECONDS, nanotime + 2000000,
                nanotime + 3000000).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value2);

        assertHasSameValue(returnValue, value2);
    }

    @Test
    public void noNextPointTest() throws Exception {
        int currentPartition = getCurrentBufferPartition(codataBuffer);
        Assert.assertEquals(currentPartition, 0);

        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        codataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);

        List<Map<String, String>> returnData = codataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.NANOSECONDS, nanotime-2000,
                nanotime - 1000).get(testFeedID1);

        Assert.assertNull(returnData);
    }
    
    @Test
    public void putDataTimeRangeTest() throws Exception {
        long time = System.currentTimeMillis();

        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        Map<Long, Map<String, String>> feedData1 = new HashMap<Long, Map<String, String>>();
        feedData1.put(time, value);

        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "1.4");
        value2.put("status", "ok");
        Map<Long, Map<String, String>> feedData2 = new HashMap<Long, Map<String, String>>();
        feedData2.put(time + 100, value2);

        Map<String, Map<Long, Map<String, String>>> data = new HashMap<String, Map<Long, Map<String, String>>>();
        data.put(testFeedID1, feedData1);
        data.put(testFeedID2, feedData2);
        codataBuffer.putData(data, TimeUnit.MILLISECONDS, null);

        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        long nanotime2 = TimeUnit.NANOSECONDS.convert(time + 100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(codataBuffer.metaDataBuffer.getStartTimestamp(0, testFeedID1), nanotime);
        Assert.assertEquals(codataBuffer.metaDataBuffer.getEndTimestamp(0, testFeedID1), nanotime);
        Assert.assertEquals(codataBuffer.metaDataBuffer.getStartTimestamp(0, testFeedID2), nanotime2);
        Assert.assertEquals(codataBuffer.metaDataBuffer.getEndTimestamp(0, testFeedID2), nanotime2);
    }

    private void assertHasSameValue(Map<String, String> actualValue, Map<String, String> expectedValue) {
        Assert.assertEquals(actualValue.size(), expectedValue.size());

        for (String key : actualValue.keySet()) {
            Assert.assertEquals(actualValue.get(key), expectedValue.get(key));
        }
    }

    private int getCurrentBufferPartition(DataBuffer dataBuffer) throws Exception {
        Field f = DataBuffer.class.getDeclaredField("currentParition");
        f.setAccessible(true);
        PartitionDataBuffer currentPartitionBuffer = (PartitionDataBuffer) f.get(dataBuffer);
        return currentPartitionBuffer.getBufferEnv().getCurrentBufferPartition();
    }
}
