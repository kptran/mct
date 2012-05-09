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
package gov.nasa.arc.mct.components;



/**
 * A TimeConversion is a capability components may support, where 
 * incoming data may be converted from its native format to MCT's internal 
 * time.
 * 
 * @author vwoeltje
 *
 */
public interface TimeConversion {
    /**
     * Convert this object to the UNIX epoch.
     * @param encodedTime some encoded representation of a time
     * @return milliseconds elapsed since start of UNIX epoch
     */
    public long convertToUNIXTime(String encodedTime);
    
    /**
     * Convert the supplied UNIX epoch time to whatever 
     * time representation this conversion generally supports.
     * @param localTimeMillis milliseconds elapsed wince start of UNIX epoch
     * @return an encoded representation of the time
     */
    public String convertFromUNIXTime(long localTimeMillis);
    
    /**
     * Should this be interpreted as a timer? (otherwise, it will 
     * be interpreted as a clock - for instance, negative numbers 
     * will be rendered as before the epoch, instead of as 
     * negative times.)
     * @return true if a timer, false if a clock
     */
    public boolean isTimer();
    
}
