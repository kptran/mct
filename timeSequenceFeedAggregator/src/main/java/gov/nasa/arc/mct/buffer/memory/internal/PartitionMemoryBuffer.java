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
package gov.nasa.arc.mct.buffer.memory.internal;

import gov.nasa.arc.mct.buffer.config.DataBufferEnv;
import gov.nasa.arc.mct.buffer.config.MemoryBufferEnv;
import gov.nasa.arc.mct.buffer.disk.internal.PartitionTimestamps;
import gov.nasa.arc.mct.buffer.internal.MetaDataBuffer;
import gov.nasa.arc.mct.buffer.internal.PartitionDataBuffer;
import gov.nasa.arc.mct.buffer.util.ElapsedTimer;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartitionMemoryBuffer implements PartitionDataBuffer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartitionMemoryBuffer.class);
    private static final Logger READ_PERF_LOGGER = LoggerFactory.getLogger("gov.nasa.arc.mct.performance.memory.partitionbuffer.read");
    private static final Logger WRITE_PERF_LOGGER = LoggerFactory.getLogger("gov.nasa.arc.mct.performance.memory.partitionbuffer.write");

    private static final class TimeStampComparator implements Comparator<Long>, Serializable {
        private static final long serialVersionUID = -665810351953536404L;

        @Override
        public int compare(Long o1, Long o2) {
            return o1.compareTo(o2);
        }
    }
    private static final Comparator<Long> TIMESTAMP_COMPARATOR = new TimeStampComparator();

    private volatile Map<String, TreeMap<Long, Map<String, String>>> cachedData = new HashMap<String, TreeMap<Long, Map<String, String>>>();
    private volatile SoftReference<Map<String, TreeMap<Long, Map<String, String>>>> claimableCachedData = null;

    private final MemoryBufferEnv env;
    private volatile boolean active;

    public PartitionMemoryBuffer(int partitionNumber) {
        this(new MemoryBufferEnv(null, partitionNumber));
        this.active = true;
        READ_PERF_LOGGER.debug("Newing memory partition {}", partitionNumber);
    }

    public PartitionMemoryBuffer(MemoryBufferEnv env) {
        this.env = env;
        this.active = true;
    }

    @Override
    public void removeBuffer() {
        cachedData = null;
        this.env.closeAndRestartEnvironment();
    }
    
    @Override
    public void closeBuffer() {
        removeBuffer();
    }

    @Override
    public DataBufferEnv getBufferEnv() {
        return this.env;
    }
    
    private Map<String, TreeMap<Long, Map<String, String>>> getCachedData() {
        Map<String, TreeMap<Long, Map<String, String>>> returnedCachedData = null;
        if (cachedData != null) {
            returnedCachedData = cachedData;
        } else if (claimableCachedData != null) {
            returnedCachedData = claimableCachedData.get();
        }
        if (returnedCachedData == null) {
            returnedCachedData = Collections.emptyMap();
        }
        return returnedCachedData;
    }
    
    @Override
    public Map<String, SortedMap<Long, Map<String, String>>> getLastData(Set<String> feedIDs, TimeUnit timeUnit, long startTime, long endTime) {
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();
        
        Map<String, TreeMap<Long, Map<String, String>>> cachedData = getCachedData();
        
        Map<String, SortedMap<Long, Map<String, String>>> returnedData = new HashMap<String, SortedMap<Long, Map<String, String>>>();

        for (String feedID : feedIDs) {
            synchronized (this) {
                TreeMap<Long, Map<String, String>> feedCachedData = cachedData.get(feedID);
                if (feedCachedData == null) {
                    continue;
                }

                long start = TimeUnit.NANOSECONDS.convert(startTime, timeUnit);
                long end = TimeUnit.NANOSECONDS.convert(endTime, timeUnit);
                Entry<Long, Map<String, String>> feedSearchedData = feedCachedData.subMap(start, true, end, true).lastEntry();
                if (feedSearchedData != null) {
                    SortedMap<Long, Map<String, String>> feedData = new TreeMap<Long, Map<String, String>>();
                    feedData.put(feedSearchedData.getKey(), feedSearchedData.getValue());
                    returnedData.put(feedID, feedData);
                }
            }
        }
        
        timer.stopInterval();
        READ_PERF_LOGGER.debug("Time to get {} feeds from memory: {} from partition " + this.env.getCurrentBufferPartition(), feedIDs.size(), timer.getIntervalInMillis());

        return returnedData;
    }

    @Override
    public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit, long startTime,
            long endTime) {
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();
        
        Map<String, TreeMap<Long, Map<String, String>>> cachedData = getCachedData();
        
        Map<String, SortedMap<Long, Map<String, String>>> returnedData = new HashMap<String, SortedMap<Long, Map<String, String>>>();

        startTime = TimeUnit.NANOSECONDS.convert(startTime, timeUnit);
        endTime = TimeUnit.NANOSECONDS.convert(endTime, timeUnit);

        for (String feedID : feedIDs) {
            synchronized (this) {
                TreeMap<Long, Map<String, String>> feedCachedData = cachedData.get(feedID);
                if (feedCachedData == null) {
                    continue;
                }

                Map<Long, Map<String, String>> feedSearchedData = feedCachedData.subMap(startTime, true, endTime, true);
                if (feedSearchedData != null && !feedSearchedData.isEmpty()) {
                    SortedMap<Long, Map<String, String>> feedData = new TreeMap<Long, Map<String, String>>();
                    feedData.putAll(feedSearchedData);
                    returnedData.put(feedID, feedData);
                }
            }
        }
        
        timer.stopInterval();
        READ_PERF_LOGGER.debug("Time to get {} feeds from memory: {} from partition " + this.env.getCurrentBufferPartition(), feedIDs.size(), timer.getIntervalInMillis());

        return returnedData;
    }

    @Override
    public void inactive() {
        claimableCachedData = new SoftReference<Map<String,TreeMap<Long,Map<String,String>>>>(cachedData);
        cachedData = null;
        this.active = false;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean isClosed() {
        return cachedData == null;
    }

    @Override
    public Map<String, PartitionTimestamps> putData(Map<String, Map<Long, Map<String, String>>> value, TimeUnit timeUnit) {
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();
        
        Map<String, PartitionTimestamps> timestamps = new HashMap<String, PartitionTimestamps>();
        Map<String, TreeMap<Long, Map<String, String>>> cachedData = getCachedData();

        for (Entry<String, Map<Long, Map<String, String>>> entry : value.entrySet()) {
            String feedID = entry.getKey();
            long largestTime = 0;
            long smallestTime = 0;
            synchronized (this) {
                TreeMap<Long, Map<String, String>> cachedFeedData = cachedData.get(feedID);
                if (cachedFeedData == null) {
                    cachedFeedData = new TreeMap<Long, Map<String, String>>(TIMESTAMP_COMPARATOR);
                    cachedData.put(feedID, cachedFeedData);
                }
                for (Entry<Long, Map<String, String>> feedData : entry.getValue().entrySet()) {
                    Long time = feedData.getKey();
                    time = TimeUnit.NANOSECONDS.convert(time, timeUnit);
                    LOGGER.debug("Putting data for feed {} with time {}", feedID, time);
                    if (time.longValue() > largestTime) {
                        largestTime = time.longValue();
                    }
                    if (smallestTime == 0) {
                        smallestTime = time.longValue();
                    } else if (time.longValue() < smallestTime) {
                        smallestTime = time.longValue();
                    }
                    Map<String, String> clonedFeedData = new HashMap<String, String>(feedData.getValue());
                    cachedFeedData.put(time, clonedFeedData);
                }
            }
            timestamps.put(feedID, new PartitionTimestamps(smallestTime, largestTime));
        }
        
        
        timer.stopInterval();
        WRITE_PERF_LOGGER.debug("Time to write {} feeds: {} from partition " + this.env.getCurrentBufferPartition(), value.size(), timer.getIntervalInMillis());

        return timestamps;
    }
    
    @Override
    public void putData(Map<String, Map<Long, Map<String, String>>> value, TimeUnit timeUnit, MetaDataBuffer metadata, int metadataIndex) {
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();
        
        Map<String, TreeMap<Long, Map<String, String>>> cachedData = getCachedData();

        for (Entry<String, Map<Long, Map<String, String>>> entry : value.entrySet()) {
            String feedID = entry.getKey();
            long largestTime = 0;
            long smallestTime = 0;
            synchronized (this) {
                TreeMap<Long, Map<String, String>> cachedFeedData = cachedData.get(feedID);
                if (cachedFeedData == null) {
                    cachedFeedData = new TreeMap<Long, Map<String, String>>(TIMESTAMP_COMPARATOR);
                    cachedData.put(feedID, cachedFeedData);
                }
                for (Entry<Long, Map<String, String>> feedData : entry.getValue().entrySet()) {
                    Long time = feedData.getKey();
                    time = TimeUnit.NANOSECONDS.convert(time, timeUnit);
                    LOGGER.debug("Putting data for feed {} with time {}", feedID, time);
                    if (time.longValue() > largestTime) {
                        largestTime = time.longValue();
                    }
                    if (smallestTime == 0) {
                        smallestTime = time.longValue();
                    } else if (time.longValue() < smallestTime) {
                        smallestTime = time.longValue();
                    }
                    Map<String, String> clonedFeedData = new HashMap<String, String>(feedData.getValue());
                    cachedFeedData.put(time, clonedFeedData);
                }
            }
            metadata.updatePartitionMetaData(metadataIndex, feedID, smallestTime, largestTime);
        }
        
        
        timer.stopInterval();
        if (WRITE_PERF_LOGGER.isDebugEnabled()) {
            WRITE_PERF_LOGGER.debug("Time to write {} feeds: {} from partition " + this.env.getCurrentBufferPartition(), value.size(), timer.getIntervalInMillis());
    
        }
    }
    
    @Override
    public void resetBuffer() {
        cachedData.clear();
    }
}
