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
import gov.nasa.arc.mct.buffer.config.DataBufferEnv;
import gov.nasa.arc.mct.buffer.disk.internal.PartitionTimestamps;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public interface PartitionDataBuffer {
    public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit, long startTime, long endTime);
    
    /**
     * Similar to {@link #getData(Set, TimeUnit, long, long)}, but returns only the last point in the range for each feed.
     */
    public Map<String, SortedMap<Long, Map<String, String>>> getLastData(Set<String> feedIDs, TimeUnit timeUnit, long startTime, long endTime);
    
    public Map<String, PartitionTimestamps> putData(Map<String, Map<Long, Map<String, String>>> value, final TimeUnit timeUnit)  throws BufferFullException;
    
    public void putData(Map<String, Map<Long, Map<String, String>>> value, final TimeUnit timeUnit, MetaDataBuffer metadata, int metadataIndex)  throws BufferFullException;
    
    public boolean isActive();
    
    public void removeBuffer();
    
    public void closeBuffer();
    
    public boolean isClosed();
    
    public void inactive();
    
    public DataBufferEnv getBufferEnv();
    
    public void resetBuffer();
}
