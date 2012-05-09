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

public class MemoryBufferResetTest {
    private DataBuffer dataBuffer;
    private String testFeedID1 = "TestPui1";

    @BeforeMethod
    public void setup() throws IOException {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("buffer.partitions", "1");
        prop.put("buffer.time.millis", "-1");
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
    public void resetTest() throws BufferFullException {
        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.NANOSECONDS, nanotime,
                nanotime + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value);

        assertHasSameValue(returnValue, value);
        
        dataBuffer.reset();
        
        returnData = dataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.NANOSECONDS, nanotime,
                nanotime + 100).get(testFeedID1);

        Assert.assertNull(returnData);
        
        value = new HashMap<String, String>();
        value.put("value", "1.4");
        value.put("status", "true");
        time = System.currentTimeMillis();
        nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);

        returnData = dataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.NANOSECONDS, nanotime,
                nanotime + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        returnValue = returnData.get(0);

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
