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
import gov.nasa.arc.mct.api.feed.DataProvider.LOS;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DataBufferTest {
    private DataBuffer dataBuffer;
    private String testFeedID1 = "TestPui1";
    private String testFeedID2 = "TestPui2";
    private File bufferLocation;

    @BeforeMethod
    public void setup() throws IOException {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed.properties"));
        prop.put("buffer.partitions", "1");
        prop.put("buffer.time.millis", "-1");
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
    public void fastLOSTest() {
        Assert.assertEquals(dataBuffer.getLOS(), LOS.medium);
    }

    @Test
    public void putSingleDataTest() throws BufferFullException {
        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1),
                        TimeUnit.NANOSECONDS, nanotime, nanotime + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value);

        assertHasSameValue(returnValue, value);
    }

    @Test
    public void putBulkValueTest() throws BufferFullException {
        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.currentTimeMillis();
        long nanotime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        final AtomicInteger callbackCount = new AtomicInteger(0);
        Runnable r = new Runnable() {
            public void run() {
                callbackCount.incrementAndGet();
            }
        };
        Map<String, Map<Long, Map<String, String>>> bulkValue = new HashMap<String, Map<Long, Map<String, String>>>();
        Map<Long, Map<String, String>> aValue = new HashMap<Long, Map<String, String>>();
        aValue.put(time, value);
        bulkValue.put(testFeedID1, aValue);
        dataBuffer.putData(bulkValue, TimeUnit.MILLISECONDS, r);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1),
                        TimeUnit.NANOSECONDS, nanotime, nanotime + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value);

        assertHasSameValue(returnValue, value);
    }

    @Test
    public void notExactTimestampTest() throws BufferFullException {
        Map<String, String> value = new HashMap<String, String>();
        value.put("value", "1.3");
        value.put("status", "ok");
        long time = System.nanoTime();
        dataBuffer.putData(testFeedID1, TimeUnit.NANOSECONDS, time, value);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1),
                        TimeUnit.NANOSECONDS, time - 100, time + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value);

        assertHasSameValue(returnValue, value);
    }

    @Test
    public void multipleFeedsTest() throws BufferFullException {
        Map<String, String> value1 = new HashMap<String, String>();
        value1.put("value", "1.3");
        value1.put("status", "ok");
        long time1 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time1, value1);

        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "2.3");
        value2.put("status", "ok2");
        long time2 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID2, TimeUnit.MILLISECONDS, time2, value2);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1),
                        TimeUnit.MILLISECONDS, time1, time1 + 100).get(testFeedID1);
        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value1);

        assertHasSameValue(returnValue, value1);

        returnData = dataBuffer.getData(Collections.singleton(testFeedID2), TimeUnit.MILLISECONDS, time2, time2 + 100).get(testFeedID2);
        Assert.assertEquals(returnData.size(), 1);
        returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value2);

        assertHasSameValue(returnValue, value2);
    }

    @Test
    public void isFullyWithinTimeSpanTest() throws BufferFullException {
        Map<String, String> value1 = new HashMap<String, String>();
        value1.put("value", "1.3");
        value1.put("status", "ok");
        long time1 = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time1, value1);

        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "2.3");
        value2.put("status", "ok2");
        long time2 = time1 + 100;
        dataBuffer.putData(testFeedID2, TimeUnit.MILLISECONDS, time2, value2);

        Map<String, String> value3 = new HashMap<String, String>();
        value3.put("value", "2.3");
        value3.put("status", "ok2");
        long time3 = time1 + 200;
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time3, value3);

        Map<String, String> value4 = new HashMap<String, String>();
        value4.put("value", "2.3");
        value4.put("status", "ok2");
        long time4 = time1 + 300;
        dataBuffer.putData(testFeedID2, TimeUnit.MILLISECONDS, time4, value4);

        Assert.assertTrue(dataBuffer.isFullyWithinTimeSpan(testFeedID1, time1, TimeUnit.MILLISECONDS));
        Assert.assertFalse(dataBuffer.isFullyWithinTimeSpan(testFeedID1, time1 - 1, TimeUnit.MILLISECONDS));
        Assert.assertTrue(dataBuffer.isFullyWithinTimeSpan(testFeedID2, time2, TimeUnit.MILLISECONDS));
        Assert.assertFalse(dataBuffer.isFullyWithinTimeSpan(testFeedID2, time2 - 1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void multipleFeedsSameTimeTest() throws BufferFullException {
        Map<String, String> value1 = new HashMap<String, String>();
        value1.put("value", "1.3");
        value1.put("status", "ok");
        long time = System.currentTimeMillis();
        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value1);

        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "2.3");
        value2.put("status", "ok2");
        dataBuffer.putData(testFeedID2, TimeUnit.MILLISECONDS, time, value2);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1),
                        TimeUnit.MILLISECONDS, time, time + 100).get(testFeedID1);
        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value1);

        assertHasSameValue(returnValue, value1);

        returnData = dataBuffer.getData(Collections.singleton(testFeedID2), TimeUnit.MILLISECONDS, time, time + 100).get(testFeedID2);
        Assert.assertEquals(returnData.size(), 1);
        returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value2);

        assertHasSameValue(returnValue, value2);
    }

    @Test
    public void putMultipleDataTest() throws InterruptedException, BufferFullException {
        Map<Long, Map<String, String>> data = new HashMap<Long, Map<String, String>>();

        Map<String, String> value1 = new HashMap<String, String>();
        value1.put("value", "1.3");
        value1.put("status", "ok");
        long time1 = System.currentTimeMillis();
        data.put(time1, value1);

        Thread.sleep(200);

        Map<String, String> value2 = new HashMap<String, String>();
        value2.put("value", "1.4");
        value2.put("status", "not-ok");
        long time2 = System.currentTimeMillis();

        data.put(time2, value2);

        dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, data);

        List<Map<String, String>> returnData = dataBuffer.getData(Collections.singleton(testFeedID1),
                        TimeUnit.MILLISECONDS, time1, time1 + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 1);
        Map<String, String> returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value1);

        assertHasSameValue(returnValue, value1);

        returnData = dataBuffer.getData(Collections.singleton(testFeedID1), TimeUnit.MILLISECONDS, time1, time2 + 100).get(testFeedID1);

        Assert.assertEquals(returnData.size(), 2);
        returnValue = returnData.get(0);

        Assert.assertNotSame(returnValue, value1);

        assertHasSameValue(returnValue, value1);

        returnValue = returnData.get(1);

        Assert.assertNotSame(returnValue, value2);

        assertHasSameValue(returnValue, value2);
    }

    @Test (enabled=false)
    public void longRunningTest() throws InterruptedException, BrokenBarrierException {
        final CyclicBarrier barrier = new CyclicBarrier(3);

        final PutDataRunnable putDataTask = new PutDataRunnable(barrier);
        final GetDataRunnable getDataTask = new GetDataRunnable(barrier);
        final Thread t1 = new Thread(putDataTask);
        final Thread t2 = new Thread(getDataTask);

        t1.start();
        t2.start();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                putDataTask.interrupt();
                getDataTask.interrupt();
            }
        };
        timer.schedule(task, 6000);

        barrier.await();
        Assert.assertTrue(getDataTask.isPassed());
    }

    private void assertHasSameValue(Map<String, String> actualValue,
                    Map<String, String> expectedValue) {
        Assert.assertEquals(actualValue.size(), expectedValue.size());

        for (String key : actualValue.keySet()) {
            Assert.assertEquals(actualValue.get(key), expectedValue.get(key));
        }
    }

    private final class GetDataRunnable implements Runnable {
        private boolean stop = false;
        private CyclicBarrier barrier;
        private boolean assertPassed = true;

        public GetDataRunnable(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            List<Map<String, String>> readData = new LinkedList<Map<String, String>>();

            long startTime = System.currentTimeMillis();
            long endTime = startTime;
            int oldValue = -1;
            while (!stop) {
                List<Map<String, String>> data = dataBuffer.getData(Collections.singleton(testFeedID1),
                                TimeUnit.MILLISECONDS, startTime, endTime).get(testFeedID1);

                if (data == null) {
                    data = Collections.emptyList();
                }
                int index = 0;
                for (Map<String, String> record : data) {
                    int value = Integer.parseInt(record.get("value"));
                    long time = Long.parseLong(record.get("time"));
                    
                    assertPassed = assertPassed && time <= endTime; // since this is now COD the last time may be less than the last requested
                    if (index++ > 0) {
                        assertPassed = assertPassed && time >= startTime;
                    }
                    assertPassed = assertPassed && (value >= oldValue);
                    if (!assertPassed) {
                        stop = true;
                    }
                    oldValue = value;
                }

                readData.addAll(data);
                startTime = endTime;
                endTime = System.currentTimeMillis();
            }

            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            } catch (BrokenBarrierException e) {
                throw new AssertionError(e);
            }
        }

        public void interrupt() {
            this.stop = true;
        }

        public boolean isPassed() {
            return assertPassed;
        }
    }

    private final class PutDataRunnable implements Runnable {
        private boolean stop = false;
        private CyclicBarrier barrier;

        public PutDataRunnable(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            int i = 0;

            while (!stop) {
                Map<String, String> value = new HashMap<String, String>();
                long time = System.currentTimeMillis();
                value.put("value", String.valueOf(i));
                value.put("time", String.valueOf(time));
                try {
                    dataBuffer.putData(testFeedID1, TimeUnit.MILLISECONDS, time, value);
                    i++;
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // ignore interrupt
                } catch (BufferFullException e1) {
                    e1.printStackTrace();
                }

            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            } catch (BrokenBarrierException e) {
                throw new AssertionError(e);
            }
        }

        public void interrupt() {
            stop = true;
        }
    }
}
