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

import gov.nasa.arc.mct.buffer.disk.internal.PartitionFastDiskBuffer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SwitchBetweenParitionsTest {
    private DataBuffer dataBuffer;
    private File bufferLocation;

    @BeforeMethod
    public void setup() throws IOException {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("buffer.partitions", "3");
        prop.put("buffer.time.millis", "-1");
        bufferLocation = File.createTempFile("mct-buffer", "");
        bufferLocation.delete();
        Assert.assertTrue(bufferLocation.mkdir());
        prop.put("buffer.disk.loc", bufferLocation.toString());
        dataBuffer = DataBufferFactory.getFastDiskDataBuffer(prop);
        if (dataBuffer.isDataBufferClose()) {
            dataBuffer.reset();
        }
        
    }

    @AfterMethod
    public void reset() {
        if (dataBuffer != null) {
            dataBuffer.closeBuffer();
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
    public void switchPartitionsTest() throws Exception {
        int currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 0);
        
        dataBuffer.prepareForNextPartition();
        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 0);
        
        dataBuffer.moveToNextPartition();
        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 1);
        
        dataBuffer.prepareForNextPartition();
        
        dataBuffer.moveToNextPartition();
        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 2);
        
        dataBuffer.prepareForNextPartition();
        
        dataBuffer.moveToNextPartition();
        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 0);
    }
    
    private int getCurrentBufferPartition() throws Exception {
        Field f = DataBuffer.class.getDeclaredField("currentParition");
        f.setAccessible(true);
        PartitionFastDiskBuffer currentPartitionBuffer = (PartitionFastDiskBuffer)f.get(dataBuffer);
        return currentPartitionBuffer.getBufferEnv().getCurrentBufferPartition();
    }
}
