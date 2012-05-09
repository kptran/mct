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
import gov.nasa.arc.mct.buffer.disk.internal.PartitionTimestamps;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MetaDataBuffer {
    private static final int NULL_TIMESTAMP = -1;
    
    protected volatile PartitionMetaData[] partitionMetaDatas;
    
    protected volatile int currentPartition = 0;
    
    private DataBufferEnv metaEnv;
    
    public MetaDataBuffer(DataBufferEnv metaEnv) {
        this.metaEnv = metaEnv;
        this.partitionMetaDatas = new PartitionMetaData[metaEnv.getNumOfBufferPartitions()];
    }
    
    protected synchronized PartitionMetaData getPartitionMetaData(int bufferPartition) {
        return partitionMetaDatas[bufferPartition];
    }
    
    public int getNumOfPartitions() {
        return metaEnv.getNumOfBufferPartitions();
    }
    
    public int getCurrentPartition() {
        return currentPartition;
    }

    public synchronized long getStartTimestamp(int bufferPartition, String feedID) {
        PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
        if (partitionMetaData == null) {
            return NULL_TIMESTAMP;
        } else {
            return partitionMetaData.getStartTimestamp(feedID);
        }
    }

    public synchronized long getEndTimestamp(int bufferPartition, String feedID) {
        PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
        if (partitionMetaData == null) {
            return NULL_TIMESTAMP;
        } else {
            return partitionMetaData.getEndTimestamp(feedID);
        }
    }

    public boolean isWithinTimeSpan(int bufferPartition, String feedID, TimeUnit timeunit, long startTime, long endTime) {
        long startTimeInNanos = TimeUnit.NANOSECONDS.convert(startTime, timeunit);
        long endTimeInNanos = TimeUnit.NANOSECONDS.convert(endTime, timeunit);

        synchronized (this) {
            PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
            if (partitionMetaData == null) {
                return false;
            }
            
            return partitionMetaData.isWithinTimeSpan(feedID, startTimeInNanos, endTimeInNanos);
        }
    }
    
    public boolean isFullyWithinTimeSpan(int bufferPartition, String feedID, TimeUnit timeunit, long startTime) {
        long startTimeInNanos = TimeUnit.NANOSECONDS.convert(startTime, timeunit);
        
        synchronized (this) {
            PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
            if (partitionMetaData == null) {
                return false;
            }
            
            return partitionMetaData.isFullyWithinTimeSpan(feedID, startTimeInNanos);
        }
    }

    public synchronized PartitionMetaData removePartitionMetaData(int bufferPartition) {
        PartitionMetaData partitionMetaData = this.partitionMetaDatas[bufferPartition];
        this.partitionMetaDatas[bufferPartition] = null;
        return partitionMetaData;
    }
    
    public synchronized Set<String> resetPartitionMetaData(int bufferPartition) {
        PartitionMetaData partitionMetaData = this.partitionMetaDatas[bufferPartition];
        Set<String> rowoverFeedIDs = getRowoverFeedIDs(bufferPartition);
        if (partitionMetaData != null) {
            partitionMetaData.reset();
            partitionMetaData.addFeeds(rowoverFeedIDs);
        }
        return rowoverFeedIDs;
    }
    
    private Set<String> getRowoverFeedIDs(int bufferPartition) {
        PartitionMetaData targetMetaData = getPartitionMetaData(bufferPartition);
        if (targetMetaData == null) { return Collections.emptySet(); }
        
        Set<String> targetFeedIDs = new HashSet<String>(targetMetaData.getFeeds());
        
        int i = bufferPartition;
        i = metaEnv.previousBufferPartition(i);
        while (i != bufferPartition) {
            PartitionMetaData metaData = getPartitionMetaData(i);
            if (metaData == null) { break; }
            Set<String> feedIDs = metaData.getFeeds();
            targetFeedIDs.removeAll(feedIDs);
            i = metaEnv.previousBufferPartition(i);
        }
        return targetFeedIDs;
    }
    
    public synchronized boolean hasFeed(int bufferPartition, String feedID) {
        PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
        if (partitionMetaData == null) {
            return false;
        } else {
            return partitionMetaData.hasFeed(feedID);
        }
    }
    
    public void updatePartitionMetaData(int bufferPartition, Map<String, PartitionTimestamps> timeStamps) {
        PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
        if (partitionMetaData == null) {
            partitionMetaData = new PartitionMetaData(bufferPartition);
            partitionMetaData.setTimeStamp(timeStamps);
            this.partitionMetaDatas[bufferPartition] = partitionMetaData;
        } else {
            partitionMetaData.updateTimestamp(timeStamps);
        }
    }

    public synchronized void updatePartitionMetaData(int bufferPartition, String feedID, long startTime, long endTime) {
        PartitionMetaData partitionMetaData = partitionMetaDatas[bufferPartition];
        if (partitionMetaData == null) {
            partitionMetaData = new PartitionMetaData(bufferPartition);
            this.partitionMetaDatas[bufferPartition] = partitionMetaData;
        }
        partitionMetaData.updateTimestamp(feedID, startTime, endTime);
    }

    public void writePartitionMetaData(int bufferPartition) {
    }

    public void writeCurrentBufferPartition(int newCurrentBufferPartition) {
    }

    public void restart() {
        metaEnv.restartEnvironment(false);
        partitionMetaDatas = new PartitionMetaData[metaEnv.getNumOfBufferPartitions()];
    }

    public void close() {
        partitionMetaDatas = new PartitionMetaData[metaEnv.getNumOfBufferPartitions()];
        metaEnv = null;
    }
    
    public void closeAndRestartEnvironment() {
        partitionMetaDatas = new PartitionMetaData[metaEnv.getNumOfBufferPartitions()];
        metaEnv.closeAndRestartEnvironment();
    }

    public void closeDatabase() {
        partitionMetaDatas = new PartitionMetaData[metaEnv.getNumOfBufferPartitions()];
    }

}
