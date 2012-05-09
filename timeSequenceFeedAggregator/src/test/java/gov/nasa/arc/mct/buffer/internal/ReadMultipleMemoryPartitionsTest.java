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

import gov.nasa.arc.mct.api.feed.BufferFullException;

import java.io.IOException;
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

public class ReadMultipleMemoryPartitionsTest {
    private DataBuffer dataBuffer;
    private String testFeedID1 = "TestPui1";
    
    @BeforeMethod
    public void setup() throws IOException {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("memory.buffer.partition", "2");
        prop.put("memory.buffer.time.millis", "-1");
        dataBuffer = DataBufferFactory.getMemoryDataBuffer(prop);
        if (dataBuffer.isDataBufferClose()) {
            dataBuffer.reset();
        }
    }

    @AfterMethod
    public void reset() {
        if (dataBuffer != null) {
            dataBuffer.reset();
        }
        DataBufferFactory.reset();
    }
    
    @Test
    public void readMultiplePartitionsTest() throws InterruptedException, BufferFullException {
        long time0 = System.currentTimeMillis();
        Thread.sleep(3000);
        
        Map<String, String> value11 = new HashMap<String, String>();
        value11.put("value", "1.3");
        value11.put("status", "ok");
        long time11 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time11, value11);
        
        Thread.sleep(3000);
        
        Map<String, String> value12 = new HashMap<String, String>();
        value12.put("value", "1.4");
        value12.put("status", "ok");
        long time12 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time12, value12);
        
        List<Map<String, String>> data = dataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.MILLISECONDS, time11, time12).get(testFeedID1);
        
        Assert.assertNotNull(data);
        
        Assert.assertEquals(data.size(), 2);
        
        Assert.assertEquals(data.get(0), value11);
        Assert.assertEquals(data.get(1), value12);
        
        dataBuffer.prepareForNextPartition();
        dataBuffer.moveToNextPartition();
        
        Thread.sleep(3000);
        
        Map<String, String> value21 = new HashMap<String, String>();
        value21.put("value", "2.3");
        value21.put("status", "ok");
        long time21 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time21, value21);
        
        Thread.sleep(3000);
        
        Map<String, String> value22 = new HashMap<String, String>();
        value22.put("value", "2.4");
        value22.put("status", "ok");
        long time22 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time22, value22);
        
        List<Map<String, String>> data2 = dataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.MILLISECONDS, time0, time22).get(testFeedID1);
        
        Assert.assertNotNull(data2);
        
        Assert.assertEquals(data2.size(), 4);
    }
}
