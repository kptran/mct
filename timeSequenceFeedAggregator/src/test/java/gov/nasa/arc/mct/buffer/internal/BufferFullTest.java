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
import gov.nasa.arc.mct.api.feed.DataArchive;
import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.api.feed.DataProvider.LOS;
import gov.nasa.arc.mct.feed.FeedAggregatorService;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BufferFullTest {
    private FeedAggregatorService feedAggregatorService;
    private Vector<DataArchive> dataArchives;
    private MockLogger mockLogger;
    private File bufferLocation;
    
    @BeforeMethod
    public void startup() throws Exception {
        Properties prop = new Properties();
        prop.load(ClassLoader.getSystemResourceAsStream("properties/testFeed2.properties"));
        prop.put("buffer.partitions", "2");
        prop.put("buffer.time.millis", "-1");
        bufferLocation = File.createTempFile("mct-buffer", "");
        bufferLocation.delete();
        bufferLocation.mkdir();
        prop.put("buffer.disk.loc", bufferLocation.toString());
        feedAggregatorService = new FeedAggregatorService(prop);
        dataArchives = getDataArchives();
        dataArchives.clear();
        dataArchives.add(new MockBuffer(false));
        dataArchives.add(new MockBuffer(true));
        mockLogger = new MockLogger();
        setMockLogger();
    }

    @AfterMethod
    public void reset() {
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
    public void testBufferFull() throws InterruptedException {
        Map<String, String> testData = new HashMap<String, String>();
        testData.put("key1", "value1");
        
        Assert.assertEquals(dataArchives.size(), 2);
        Assert.assertFalse(mockLogger.errorLogged);
        feedAggregatorService.putData("testFeed", TimeUnit.MILLISECONDS, System.currentTimeMillis(), testData);
        
        Thread.sleep(5000);
        
        Assert.assertEquals(dataArchives.size(), 1);
        Assert.assertEquals(dataArchives.get(0).getLOS(), LOS.fast);
        Assert.assertTrue(mockLogger.errorLogged);
    }
    
    private void setMockLogger() throws Exception {
        Field f = feedAggregatorService.getClass().getDeclaredField("LOGGER");
        f.setAccessible(true);
        f.set(null, mockLogger);
    }
    
    @SuppressWarnings("unchecked")
    private Vector<DataArchive> getDataArchives() throws Exception {
        Field f = feedAggregatorService.getClass().getDeclaredField("dataArchives");
        f.setAccessible(true);
        return (Vector<DataArchive>)f.get(feedAggregatorService);
    }
    
    private static class MockLogger implements Logger {
        private static final long serialVersionUID = 531417069158028639L;
        private boolean errorLogged = false;
        @Override
        public void debug(String arg0) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(String arg0, Object arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(String arg0, Object[] arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(String arg0, Throwable arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(Marker arg0, String arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(String arg0, Object arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(Marker arg0, String arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(Marker arg0, String arg1, Object[] arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(Marker arg0, String arg1, Throwable arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(String arg0) {
            this.errorLogged = true;
        }
        @Override
        public void error(String arg0, Object arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(String arg0, Object[] arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(String arg0, Throwable arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(Marker arg0, String arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(String arg0, Object arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(Marker arg0, String arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(Marker arg0, String arg1, Object[] arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(Marker arg0, String arg1, Throwable arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public void info(String arg0) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(String arg0, Object arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(String arg0, Object[] arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(String arg0, Throwable arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(Marker arg0, String arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(String arg0, Object arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(Marker arg0, String arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(Marker arg0, String arg1, Object[] arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(Marker arg0, String arg1, Throwable arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public boolean isDebugEnabled() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isDebugEnabled(Marker arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isErrorEnabled() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isErrorEnabled(Marker arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isInfoEnabled() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isInfoEnabled(Marker arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isTraceEnabled() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isTraceEnabled(Marker arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isWarnEnabled() {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public boolean isWarnEnabled(Marker arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public void trace(String arg0) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(String arg0, Object arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(String arg0, Object[] arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(String arg0, Throwable arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(Marker arg0, String arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(String arg0, Object arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(Marker arg0, String arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(Marker arg0, String arg1, Object[] arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(Marker arg0, String arg1, Throwable arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(String arg0) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(String arg0, Object arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(String arg0, Object[] arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(String arg0, Throwable arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(Marker arg0, String arg1) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(String arg0, Object arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(Marker arg0, String arg1, Object arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(Marker arg0, String arg1, Object[] arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(Marker arg0, String arg1, Throwable arg2) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
            // TODO Auto-generated method stub
            
        }
    }

    private static class MockBuffer implements DataArchive, DataProvider {
        private boolean bufferFull;
        
        MockBuffer(boolean bufferFull) {
            this.bufferFull = bufferFull;
        }
        
        @Override
        public LOS getLOS() {
            if (bufferFull) {
                return LOS.medium;
            }
            return LOS.fast;
        }

        @Override
        public void putData(String feedID, TimeUnit timeUnit, Map<Long, Map<String, String>> entries)
                throws BufferFullException {
            if (bufferFull) {
                throw new BufferFullException("Test buffer full.");
            }
        }

        @Override
        public void putData(String feedID, TimeUnit timeUnit, long time, Map<String, String> value)
                throws BufferFullException {
            if (bufferFull) {
                throw new BufferFullException("Test buffer full.");
            }
        }

        @Override
        public void putData(Map<String, Map<Long, Map<String, String>>> value, TimeUnit timeUnit, Runnable callback)
                throws BufferFullException {
            if (bufferFull) {
                throw new BufferFullException("Test buffer full.");
            }
        }

        @Override
        public void reset() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, long startTime,
                long endTime, TimeUnit timeUnit) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isFullyWithinTimeSpan(String feedID, long startTime, TimeUnit timeUnit) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit, long startTime,
                long endTime) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
}
