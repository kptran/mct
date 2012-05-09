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
package gov.nasa.arc.mct.api.feed;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This interface defines the api for putting data into the archive.
 *
 */
public interface FeedDataArchive {
    /**
     * This api will put data into the archive.
     * 
     * @param feedID the feedID from which the data should be archived.
     * @param timeUnit the time unit of the time stamp of each data record that is put into the
     * archive.
     * @param entries a map from timestamp to data record.
     * @throws BufferFullException - Buffer full exception.
     */
	public void putData(String feedID, TimeUnit timeUnit, Map<Long, Map<String, String>> entries) throws BufferFullException;
	
	/**
	 * This api will put data into the archive.
	 * @param feedID the feedID from which the data should be archived.
	 * @param timeUnit the time unit of the time stamp of each data record that is put into the
     * archive.
	 * @param time the timestamp of the data record.
	 * @param value the data record to be saved in the archive that corresponds to the time.
	 * @throws BufferFullException - Buffer full exception.
	 */
	public void putData(String feedID, TimeUnit timeUnit, long time, Map<String, String> value) throws BufferFullException;
	
	/**
	 * This method accepts a set of data and will invoke the runnable once all the data has
	 * been persisted.
	 * @param value for the set of data, feedId is the key for the Map based on key of time
	 * @param timeUnit the time unit of the time stamp of each data record that is put into the
     * archive.
	 * @param callback to execute when the data has been committed to the repository
	 * @throws BufferFullException - Buffer full exception.
	 */
    public void putData(Map<String,Map<Long, Map<String, String>>> value, TimeUnit timeUnit, Runnable callback) throws BufferFullException;
    
    /**
     * Reset the Feed Aggregator so that the content provided by the Feed Aggregator starts from the very beginning.
     */
    public void reset();

}
