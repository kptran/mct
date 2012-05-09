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

import gov.nasa.arc.mct.buffer.config.DataBufferEnv;
import gov.nasa.arc.mct.buffer.config.MemoryBufferEnv;
import gov.nasa.arc.mct.buffer.disk.internal.PartitionFastDiskBuffer;
import gov.nasa.arc.mct.buffer.memory.internal.MemoryDataBufferHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class DataBufferEvictorTest {
    private DataBuffer dataBuffer;
    private File bufferLocation;

    @BeforeMethod
    public void setup() throws IOException {
        DataBufferFactory.reset();
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("buffer.partitions", "3");
        prop.put("buffer.time.millis", "12000");
        prop.put("buffer.partition.overlap.millis", "1000");
        bufferLocation = File.createTempFile("mct-buffer", "");
        bufferLocation.delete();
        bufferLocation.mkdir();
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

        Thread.sleep(5000);

        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 1);

        Thread.sleep(5000);

        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 2);

        Thread.sleep(5000);

        currentPartition = getCurrentBufferPartition();
        Assert.assertEquals(currentPartition, 0);
    }

    @Test
    public void testExceptions() {
        Properties prop = new Properties();
        prop.put("memory.buffer.partition", "2");
        prop.put("memory.buffer.time.millis", "100");
        final CountDownLatch latch = new CountDownLatch(2);
        DataBufferEnv env = new MemoryBufferEnv(prop);
        DataBufferHelper partitionBufferFactory = new MemoryDataBufferHelper();
        DataBuffer mockBuffer = new DataBuffer(env, partitionBufferFactory) {
            int prepareCount;
            int moveCount;

            @Override
            public void prepareForNextPartition() {
                prepareCount++;
                if (prepareCount == 1) {
                    throw new RuntimeException("This exception is normal.");
                } else if (prepareCount == 2) {
                    latch.countDown();
                }
            }

            @Override
            public void moveToNextPartition() {
                moveCount++;
                if (moveCount == 1) {
                    throw new RuntimeException("This exception is normal.");
                } else if (moveCount == 2) {
                    latch.countDown();
                }
            }
        };
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail("Evictor failed");
        }
        mockBuffer.closeBuffer();
    }

    private int getCurrentBufferPartition() throws Exception {
        Field f = DataBuffer.class.getDeclaredField("currentParition");
        f.setAccessible(true);
        PartitionFastDiskBuffer currentPartitionBuffer = (PartitionFastDiskBuffer)f.get(dataBuffer);
        return currentPartitionBuffer.getBufferEnv().getCurrentBufferPartition();
    }

}
