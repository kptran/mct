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
package gov.nasa.arc.mct.buffer.disk.internal;


import com.sleepycat.persist.model.Persistent;


@Persistent
public final class PartitionTimestamps implements Cloneable {
    private long startTimestamp;
    private long endTimestamp;
    
    public PartitionTimestamps() {
        //
    }
    
    public PartitionTimestamps(long startTimestamp, long endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }
    
    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    @Override
    public PartitionTimestamps clone() {
        try {
            return (PartitionTimestamps) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // should never happen
        }
    }

    public void merge(long aStartTimestamp, long aEndTimestamp) {
        startTimestamp = Math.min(startTimestamp, aStartTimestamp);
        endTimestamp = Math.max(endTimestamp, aEndTimestamp);
    }
    
    public void merge(PartitionTimestamps timeStamp) {
        merge(timeStamp.startTimestamp, timeStamp.endTimestamp);
    }

    @Override
    public String toString() {
        return "[" + startTimestamp + ", " + endTimestamp + "]";
    }
}
