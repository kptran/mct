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
import gov.nasa.arc.mct.buffer.config.DataBufferEnv;
import gov.nasa.arc.mct.buffer.disk.internal.PartitionTimestamps;
import gov.nasa.arc.mct.buffer.util.ElapsedTimer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBuffer implements DataArchive, DataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBuffer.class);
    private static final Logger PERF_LOGGER = LoggerFactory
                    .getLogger("gov.nasa.arc.mct.performance.buffer");

    protected final AtomicReference<PartitionDataBuffer>[] partitionDataBuffers;
    protected MetaDataBuffer metaDataBuffer;
    protected volatile PartitionDataBuffer currentParition;
    protected DataBufferEvictor evictor;
    protected final Object movePartitionLock = new Object();
    protected final Object resetLock = new Object();
    protected boolean readInProgress = false;
    protected boolean writeInProgress = false;
    protected boolean moveParitionInProgress = false;
    protected volatile boolean reset = false;
    protected volatile boolean prepareNewPartitionInProgress = false;
    protected final DataBufferHelper dataBufferHelper;

    @SuppressWarnings("unchecked")
    DataBuffer(DataBufferEnv env, DataBufferHelper partitionBufferFactory) {
        this.dataBufferHelper = partitionBufferFactory;
        if (env == null) {
            metaDataBuffer = partitionBufferFactory.newMetaDataBuffer(null);
        } else {
            metaDataBuffer = partitionBufferFactory.newMetaDataBuffer(partitionBufferFactory.newMetaDataBufferEnv(env.getConfigProperties()));
        }
        this.partitionDataBuffers = new AtomicReference[metaDataBuffer.getNumOfPartitions()];
        setupPartitionBuffers(env, partitionBufferFactory);
        startEvictor();
    }
    
    protected void setupPartitionBuffers(DataBufferEnv env, DataBufferHelper partitionBufferFactory) {
        PartitionDataBuffer partitionBuffer;
        if (env == null) {
            partitionBuffer = partitionBufferFactory.newPartitionBuffer(metaDataBuffer.getCurrentPartition());  
        } else {
            partitionBuffer = partitionBufferFactory.newPartitionBuffer(env); 
        }
        this.currentParition = partitionBuffer;
        
        DataBufferEnv currentEnv = currentParition.getBufferEnv();
        for (int i=0; i<partitionDataBuffers.length; i++) {
            this.partitionDataBuffers[i] = new AtomicReference<PartitionDataBuffer>();
        }
        this.partitionDataBuffers[currentEnv.getCurrentBufferPartition()].set(currentParition);
    }
    
    private void startEvictor() {
        DataBufferEnv currentEnv = currentParition.getBufferEnv();
        
        if (currentEnv.getNumOfBufferPartitions() > 1 && currentEnv.getBufferTime() != -1) {
            this.evictor = new DataBufferEvictor(this, currentEnv.getBufferTime()
                            - currentEnv.getBufferPartitionOverlap(), currentEnv.getBufferPartitionOverlap());
            evictor.schedule();
        }
    }
    
    @Override
    public boolean isFullyWithinTimeSpan(String feedID, long startTime, TimeUnit timeUnit) {
        int startPartition = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        int i = startPartition;
        do {
            if (metaDataBuffer.isFullyWithinTimeSpan(i, feedID, timeUnit, startTime)) {
                return true;
            }
            i = this.currentParition.getBufferEnv().previousBufferPartition(i);
        } while (i != startPartition);
        return false;
    }

    /**
     * Returns true if the entire request can be satisfied for all feeds. 
     * @param partition partition index
     * @param feedIDs feed IDs
     * @param startTime start time
     * @param timeUnit unit of time for startTime
     * @return
     */
    private boolean isFullyWithinTimeSpan(int partition, Set<String> feedIDs, long startTime, TimeUnit timeUnit) {
        for (String feedID : feedIDs) {
            if(!metaDataBuffer.isFullyWithinTimeSpan(partition, feedID, timeUnit, startTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinTimeSpan(int partition, Set<String> feedIDs, TimeUnit timeUnit, long startTime, long endTime) {
        for (String feedID : feedIDs) {
            if(metaDataBuffer.isWithinTimeSpan(partition, feedID, timeUnit, startTime, endTime)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, long startTime, long endTime,
            TimeUnit timeUnit) {
        synchronized (movePartitionLock) {
            if (reset) return Collections.emptyMap();
        }
        
        Map<String, SortedMap<Long, Map<String, String>>> aggregateData = new HashMap<String, SortedMap<Long, Map<String,String>>>();
        
        int startPartition = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        int i = startPartition;
        do {
            PartitionDataBuffer partitionBuffer = this.partitionDataBuffers[i].get();
            if (partitionBuffer != null && isWithinTimeSpan(i, feedIDs, timeUnit, startTime, endTime)) {
                Map<String, SortedMap<Long, Map<String, String>>> data = getData(partitionBuffer, feedIDs, timeUnit, startTime, endTime);
                for (Entry<String, SortedMap<Long, Map<String, String>>> entry: data.entrySet()) {
                    SortedMap<Long, Map<String, String>> cumulativeData = aggregateData.get(entry.getKey());
                    if (cumulativeData != null) {
                        cumulativeData.putAll(entry.getValue());
                    } else {
                        aggregateData.put(entry.getKey(), entry.getValue());
                    }
                }
                if (isFullyWithinTimeSpan(i, feedIDs, startTime, timeUnit)) {
                    break;
                }
            }
            i = this.currentParition.getBufferEnv().previousBufferPartition(i);
        } while (i != startPartition);
        
        return aggregateData;
    }
    
    @Override
    public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit, long startTime,
            long endTime) {
        synchronized (movePartitionLock) {
            if (reset) return Collections.emptyMap();
        }
        
        Map<String, List<Map<String, String>>> aggregateData = new HashMap<String, List<Map<String,String>>>();
        
        int startPartition = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        int i = startPartition;
        do {
            PartitionDataBuffer partitionBuffer = this.partitionDataBuffers[i].get();
            if (partitionBuffer != null && isWithinTimeSpan(i, feedIDs, timeUnit, startTime, endTime)) {
                Map<String, SortedMap<Long, Map<String, String>>> data = getData(partitionBuffer, feedIDs, timeUnit, startTime, endTime);
                for (Entry<String, SortedMap<Long, Map<String, String>>> entry: data.entrySet()) {
                    List<Map<String, String>> cumulativeData = aggregateData.get(entry.getKey());
                    if (cumulativeData != null) {
                        cumulativeData.addAll(0, entry.getValue().values());
                    } else {
                        aggregateData.put(entry.getKey(), new LinkedList<Map<String, String>>(entry.getValue().values()));
                    }
                }
                if (isFullyWithinTimeSpan(i, feedIDs, startTime, timeUnit)) {
                    break;
                }
            }
            i = this.currentParition.getBufferEnv().previousBufferPartition(i);
        } while (i != startPartition);
        
        return aggregateData;
    }

    private Map<String, SortedMap<Long, Map<String, String>>> getData(PartitionDataBuffer partitionDataBuffer, Set<String> feedIDs, TimeUnit timeUnit,
                    long startTime, long endTime) {
        synchronized (movePartitionLock) {
            if (reset) return Collections.emptyMap();
            
            while (moveParitionInProgress) {
                try {
                    movePartitionLock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            readInProgress = true;
        }
        
        Set<String> searchFeedIDS = new HashSet<String>(feedIDs);

        try {
            final ElapsedTimer timer = new ElapsedTimer();
            timer.startInterval();

            Map<String, SortedMap<Long, Map<String, String>>> returnedData = partitionDataBuffer.getData(searchFeedIDS, timeUnit, startTime, endTime);

            timer.stopInterval();
            LOGGER.debug("time to get Data for feeds {}: {}", feedIDs, timer.getIntervalInMillis());
            return returnedData;
        } finally {
            synchronized (movePartitionLock) {
                readInProgress = false;
                movePartitionLock.notifyAll();
            }
        }
    }

    @Override
    public void putData(String feedID, TimeUnit timeUnit, Map<Long, Map<String, String>> entries) throws BufferFullException {
        synchronized (movePartitionLock) {
            if (reset) return;
        }
        
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();

        Map<String, Map<Long, Map<String, String>>> feedDataToPut = new HashMap<String, Map<Long,Map<String,String>>>();
        feedDataToPut.put(feedID, entries);
        
        int i = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        int startPartition = i;
        do {
            PartitionDataBuffer partitionBuffer = this.partitionDataBuffers[i].get();
            if (partitionBuffer == null || !partitionBuffer.isActive()) {
                break;
            }
            
            LOGGER.debug("Putting in partition {}", i);

            Map<String, PartitionTimestamps> timeStamps = putData(partitionBuffer, feedDataToPut, timeUnit);
            if (timeStamps != null) {
                metaDataBuffer.updatePartitionMetaData(partitionBuffer.getBufferEnv().getCurrentBufferPartition(), timeStamps);
            }
            i = (i + 1) % this.currentParition.getBufferEnv().getNumOfBufferPartitions();
        } while (i != startPartition);

        timer.stopInterval();
        PERF_LOGGER.debug("Time to save data for feed {}: {}", feedID, timer.getIntervalInMillis());

    }

    private Map<String, PartitionTimestamps> putData(PartitionDataBuffer partitionBuffer, Map<String, Map<Long, Map<String, String>>> value, TimeUnit timeUnit)  throws BufferFullException {
        synchronized (movePartitionLock) {
            if (reset) return null;
            while (moveParitionInProgress) {
                try {
                    movePartitionLock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            writeInProgress = true;
        }
        if (this.partitionDataBuffers[partitionBuffer.getBufferEnv().getCurrentBufferPartition()].get() == null) {
            return null;
        }

        try {
            return partitionBuffer.putData(value, timeUnit);
        } finally {
            synchronized (movePartitionLock) {
                writeInProgress = false;
                movePartitionLock.notifyAll();
            }
        }
    }
    
    @Override
    public void putData(Map<String, Map<Long, Map<String, String>>> value, TimeUnit timeUnit,
                    Runnable callback)  throws BufferFullException {
        PERF_LOGGER.debug("Putting data for {} feeds", value.size());
        synchronized (movePartitionLock) {
            if (reset) return;
        }
        
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();

        int i = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        int startPartition = i;
        do {
            PartitionDataBuffer partitionBuffer = this.partitionDataBuffers[i].get();
            if (partitionBuffer == null || !partitionBuffer.isActive()) {
                break;
            }
            
            LOGGER.debug("Putting in partition {}", i);

            Map<String, PartitionTimestamps> timeStamps = putData(partitionBuffer, value, timeUnit);
            if (timeStamps != null) {
                metaDataBuffer.updatePartitionMetaData(partitionBuffer.getBufferEnv().getCurrentBufferPartition(), timeStamps);
            }

            timer.stopInterval();
            PERF_LOGGER.debug("Time to save data for {} feeds: {}", value.size(), timer
                            .getIntervalInMillis());
            i = (i + 1) % currentParition.getBufferEnv().getNumOfBufferPartitions();
        } while (i != startPartition);

        if (callback != null) {
            callback.run();
        }
    }


    @Override
    public void putData(String feedID, TimeUnit timeUnit, long time, Map<String, String> value) throws BufferFullException {
        synchronized (movePartitionLock) {
            if (reset) return;
        }
        
        final ElapsedTimer timer = new ElapsedTimer();
        timer.startInterval();

        Map<Long, Map<String, String>> dataToPut = new HashMap<Long, Map<String, String>>();
        dataToPut.put(Long.valueOf(time), value);
        
        Map<String, Map<Long, Map<String, String>>> feedDataToPut = new HashMap<String, Map<Long,Map<String,String>>>();
        feedDataToPut.put(feedID, dataToPut);

        int i = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        int startPartition = i;
        do {
            PartitionDataBuffer partitionBuffer = this.partitionDataBuffers[i].get();
            if (partitionBuffer == null || !partitionBuffer.isActive()) {
                break;
            }
            
            LOGGER.debug("Putting in partition {}", i);

            Map<String, PartitionTimestamps> timeStamps = putData(partitionBuffer, feedDataToPut, timeUnit);
            if (timeStamps != null) {
                metaDataBuffer.updatePartitionMetaData(partitionBuffer.getBufferEnv().getCurrentBufferPartition(), timeStamps);
            }
            i = (i + 1) % this.currentParition.getBufferEnv().getNumOfBufferPartitions();
        } while (i != startPartition);
        
        timer.stopInterval();
        PERF_LOGGER.debug("Time to save data for feed {}: {}", feedID, timer.getIntervalInMillis());
    }

    public int getConcurrencyDegree() {
        return this.currentParition.getBufferEnv().getConcurrencyDegree();
    }

    public int getBufferWriteThreadPoolSize() {
        return this.currentParition.getBufferEnv().getBufferWriteThreadPoolSize();
    }
    
    @Override
    public void reset() {
        synchronized (movePartitionLock) {
            while (moveParitionInProgress || writeInProgress || readInProgress) {
                try {
                    movePartitionLock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            reset = true;
        }
        synchronized(resetLock) {
            while (prepareNewPartitionInProgress) {
                try {
                    resetLock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        
        try {
            if (evictor != null) {
                evictor.cancel();
            }
            DataBufferEnv env = currentParition.getBufferEnv();
            for (int j = 0; j < this.partitionDataBuffers.length; j++) {
                if (partitionDataBuffers[j].get() != null) {
                    partitionDataBuffers[j].get().removeBuffer();
                    partitionDataBuffers[j].set(null);
                }
            }
            
            if (this.metaDataBuffer != null) {
                this.metaDataBuffer.restart();
            }
            
            DataBufferEnv currentEnv = (DataBufferEnv)env.clone();
            
            assert currentEnv != null : "Current DataBufferEnv should not be null.";
            assert dataBufferHelper != null : "DataBufferHelper should not be null.";
            
            PartitionDataBuffer partitionBuffer = dataBufferHelper.newPartitionBuffer(currentEnv);
            this.currentParition = partitionBuffer;
            this.partitionDataBuffers[currentEnv.getCurrentBufferPartition()].set(currentParition);

            startEvictor();
        } finally {
            synchronized(movePartitionLock) {
                reset = false;
            }
        }
    }
    
    public void closeBuffer() {
        if (evictor != null) {
            evictor.cancel();
            evictor = null;
        }

        for (int j = 0; j < this.partitionDataBuffers.length; j++) {
            if (partitionDataBuffers[j].get() != null) {
                partitionDataBuffers[j].get().closeBuffer();
                partitionDataBuffers[j].set(null);
            }
        }
        if (this.metaDataBuffer != null) {
            this.metaDataBuffer.close();
        }
    }

    private synchronized void closeBuffer(PartitionDataBuffer partitionBuffer) {
        partitionBuffer.removeBuffer();
    }

    public boolean isDataBufferClose() {
        return this.currentParition.isClosed();
    }

    public boolean isAllDataBuffersClose() {
        for (int i=0; i<this.partitionDataBuffers.length; i++) {
            PartitionDataBuffer partitionBuffer = partitionDataBuffers[i].get();
            if (partitionBuffer != null && !partitionBuffer.isClosed()) {
                return false;
            }
        }
        return true;
    }

    public void prepareForNextPartition() {
        synchronized(resetLock) {
            if (reset) { return; }
            prepareNewPartitionInProgress = true;
        }
        
        try {
            int newBufferPartition = this.currentParition.getBufferEnv().nextBufferPartition();
        
            PartitionDataBuffer toBeClosedBuffer = this.partitionDataBuffers[newBufferPartition].get();
        
            Map<String, SortedMap<Long, Map<String, String>>> rowOverData = null;
            if (toBeClosedBuffer != null) {
                Set<String> rowOverFeedIDs = metaDataBuffer.resetPartitionMetaData(newBufferPartition);
                if (!rowOverFeedIDs.isEmpty()) {
                    rowOverData = toBeClosedBuffer.getLastData(rowOverFeedIDs, TimeUnit.NANOSECONDS, 0, Long.MAX_VALUE);
                }
                closeBuffer(toBeClosedBuffer);
            }

            DataBufferEnv newBufferEnv = this.currentParition.getBufferEnv().advanceBufferPartition();
            PartitionDataBuffer newPartitionBuffer = dataBufferHelper.newPartitionBuffer(newBufferEnv);
            if (rowOverData != null) {
                Map<String, Map<Long, Map<String, String>>> data = new HashMap<String, Map<Long,Map<String,String>>>();
                for (Entry<String, SortedMap<Long, Map<String, String>>> entry: rowOverData.entrySet()) {
                    Map<Long, Map<String, String>> feedData = new HashMap<Long, Map<String,String>>(entry.getValue());
                    data.put(entry.getKey(), feedData);
                }
                try {
                    Map<String, PartitionTimestamps> timeStamps = putData(newPartitionBuffer, data, TimeUnit.NANOSECONDS);
                    if (timeStamps != null) {
                        metaDataBuffer.updatePartitionMetaData(newBufferPartition, timeStamps);
                    }
                } catch (BufferFullException e) {
                    LOGGER.error("Buffer full during prepareForNextPartition", e);
                }
            }
            this.partitionDataBuffers[newBufferEnv.getCurrentBufferPartition()].set(newPartitionBuffer);
        } finally {
            synchronized(resetLock) {
                prepareNewPartitionInProgress = false;
                resetLock.notifyAll();
            }
        }
    }

    public void moveToNextPartition() {
        int nextBufferPartition = this.currentParition.getBufferEnv().nextBufferPartition();
        int currentBufferPartition = this.currentParition.getBufferEnv().getCurrentBufferPartition();
        
        PartitionDataBuffer toBeInActiveBuffer = this.partitionDataBuffers[currentBufferPartition].get();
        
        metaDataBuffer.writeCurrentBufferPartition(nextBufferPartition);

        synchronized (movePartitionLock) {
            if (reset) { return; }
            
            while (readInProgress || writeInProgress) {
                try {
                    movePartitionLock.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            moveParitionInProgress = true;
        }
        try {
            this.currentParition = this.partitionDataBuffers[nextBufferPartition].get();
        } finally {
            synchronized (movePartitionLock) {
                moveParitionInProgress = false;
                movePartitionLock.notifyAll();
            }
        }
        
        metaDataBuffer.writePartitionMetaData(currentBufferPartition);
        
        if (toBeInActiveBuffer != null) {
            toBeInActiveBuffer.getBufferEnv().flush();
            toBeInActiveBuffer.inactive();
        } else {
            LOGGER.warn("PartitionDataBuffer object should not be null!");
            LOGGER.warn("currentBufferPartition={}, nextBufferPartition={}", currentBufferPartition, nextBufferPartition);
        }
    }

    @Override
    public LOS getLOS() {
        return this.currentParition.getBufferEnv().getLOS();
    }

    DataBufferEvictor getEvictor() {
        return this.evictor;
    }

}
