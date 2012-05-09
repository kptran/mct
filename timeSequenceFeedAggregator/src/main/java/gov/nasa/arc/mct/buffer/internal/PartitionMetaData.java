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

import gov.nasa.arc.mct.buffer.disk.internal.PartitionTimestamps;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public final class PartitionMetaData {
    @PrimaryKey
    private Integer partitionId;
    
    private Map<String, PartitionTimestamps> timestamps = new HashMap<String, PartitionTimestamps>();
    private volatile boolean currentPartition;
    
    public PartitionMetaData() {
        
    }
    
    public PartitionMetaData(int partitionId) {
        this.partitionId = partitionId;
    }
    
    public int getPartitionId() {
        return this.partitionId;
    }
    
    public void setCurrentPartition(boolean currentPartition) {
        this.currentPartition = currentPartition;
    }
    
    public boolean isCurrentPartition() {
        return this.currentPartition;
    }

    public void updateTimestamp(String feedID, long startTime, long endTime) {
        PartitionTimestamps ts = timestamps.get(feedID);
        if (ts == null) {
            timestamps.put(feedID, new PartitionTimestamps(startTime,endTime));
        } else {
            ts.merge(startTime,endTime);
        }
    }
    
    public void updateTimestamp(String feedID, PartitionTimestamps originalTs) {
        PartitionTimestamps ts = timestamps.get(feedID);
        if (ts == null) {
            timestamps.put(feedID, originalTs);
        } else {
            ts.merge(originalTs);
        }
    }
    
    public void setTimeStamp(Map<String, PartitionTimestamps> ts) {
        timestamps.clear();
        for (String feedID : ts.keySet()) {
            timestamps.put(feedID, ts.get(feedID).clone());
        }
    }

    public boolean hasFeed(String feedID) {
        return timestamps.containsKey(feedID);
    }
    
    public void addFeeds(Set<String> feedIDs) {
        for (String feedID : feedIDs) {
            if(!timestamps.containsKey(feedID)) {
                timestamps.put(feedID, null);
            }
        }
    }
    
    public Set<String> getFeeds() {
        return timestamps.keySet();
    }
    
    public void reset() {
        timestamps.clear();
    }
    
    public long getStartTimestamp(String feedID) {
        PartitionTimestamps ts = timestamps.get(feedID);
        if (ts != null) {
            return ts.getStartTimestamp();
        }
        return -1;
    }

    public long getEndTimestamp(String feedID) {
        PartitionTimestamps ts = timestamps.get(feedID);
        if (ts != null) {
            return ts.getEndTimestamp();
        }
        return -1;
    }

    public boolean isWithinTimeSpan(String feedID, long startTime, long endTime) {
        PartitionTimestamps timeStamp = timestamps.get(feedID);
        if(timeStamp == null) {
            return false;
        }
        long start = timeStamp.getStartTimestamp();
        long end = timeStamp.getEndTimestamp();
        return (startTime <= end || end == -1) && endTime >= start;
    }

    public boolean isFullyWithinTimeSpan(String feedID, long startTime) {
        PartitionTimestamps timeStamp = timestamps.get(feedID);
        if (timeStamp == null) {
            return false;
        }
        
        if (startTime >= timeStamp.getStartTimestamp()) {
            return true;
        }

        return false;
    }

    public void updateTimestamp(Map<String, PartitionTimestamps> ts) {
        for (Entry<String, PartitionTimestamps> entry : ts.entrySet()) {
            String feedID = entry.getKey();
            PartitionTimestamps newTS = entry.getValue();
            PartitionTimestamps timeStamp = timestamps.get(feedID);
            if (timeStamp == null) {
                timestamps.put(feedID, newTS.clone());
                continue;
            }
            timeStamp.merge(newTS);
        }
    }
}
