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
package gov.nasa.arc.mct.buffer.util;

import java.util.concurrent.TimeUnit;

/**
 * Provide a timer than can be used for performance monitoring. The timer provides overall time as well as 
 * elapsed time.
 * 
 * @version $Revision: $ $Date: $
 *
 */
public class ElapsedTimer {
    private long overallTime;
    private long elapsedTimeStart;
    private long elapsedTimeStop;
    private long intervals;
    
    public ElapsedTimer() {
        overallTime = 0;
        intervals = 0;
    }
    
    public void startInterval() {
        elapsedTimeStart = getCurrentTime();
        elapsedTimeStop = 0;
    }
    
    public void stopInterval() {
        elapsedTimeStop = getCurrentTime();
        intervals++;
        overallTime += (elapsedTimeStop-elapsedTimeStart);
    }
    /**
     * Gets the number of intervals that have been completed.
     * @return intervals - long in millisecs.
     */
    public long getIntervals() {
        return intervals;
    }
    
    /**
     * Return the last time interval in millis
     * @return elapsedTimeStop - elapsedTimeStart
     */
    public long getIntervalInMillis() {
        return elapsedTimeStop - elapsedTimeStart;
    }
    
    /**
     * 
     * @return sum of all interval times in milliseconds
     */
    public long getTotalTime() {
        return overallTime;
    }

    public double getMean() {
        return getTotalTime()/((double)intervals);
    }
    
    long getCurrentTime() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
    }
    
}
