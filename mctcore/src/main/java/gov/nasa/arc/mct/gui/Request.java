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
package gov.nasa.arc.mct.gui;

/**
 * This class defines a time span. The time span is defined in terms of Unix epoch values.
 */
public class Request {
    private final long startTime;
    private final long endTime;

    /**
     * Creates a new instance of a request. 
     * @param start time of the request, in Unix epoch time. 
     * @param end time of the request in Unix epoch time. 
     */
    public Request(long start, long end) {
        startTime = start;
        endTime = end;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Request && ((Request) obj).endTime == endTime
                        && ((Request) obj).startTime == startTime;
    }

    @Override
    public int hashCode() {
        return (int) ((startTime + endTime) % Integer.MAX_VALUE);
    }

    /**
     * Gets the start time.
     * @return start time of the request.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time.
     * @return end time of the request.
     */
    public long getEndTime() {
        return endTime;
    }
    
    @Override
    public String toString() {
        return "Request["+getStartTime()+","+getEndTime()+"]";
    }
}