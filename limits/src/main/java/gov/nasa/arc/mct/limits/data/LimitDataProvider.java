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
package gov.nasa.arc.mct.limits.data;

import gov.nasa.arc.mct.api.feed.DataProvider;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.components.FeedProvider.RenderingInfo;
import gov.nasa.arc.mct.limits.LimitLineComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/** 
 * This class provides a single value data stream. It provides a cache mapping of component ID's to 
 * their limit values, and an API to clear that cache. 
 */

public class LimitDataProvider implements DataProvider {
	public  Map<String, String> limitDefinitions = new ConcurrentHashMap<String, String>();

	public  String getLimitDefinition(String key, String value) {
		return limitDefinitions.get(key);
	}
	
	public void putLimitDefinition(String key, String value) {
		assert key != null;
	    limitDefinitions.put(key, value);
	}
	
	public void clearLimitDefinition(String key) {
		assert key != null;
	    limitDefinitions.remove(key);
	}

	@Override
	public Map<String, SortedMap<Long, Map<String, String>>> getData(Set<String> feedIDs, long startTime, long endTime, TimeUnit timeUnit) {
		
		Map<String, SortedMap<Long, Map<String,String>>> values = Collections.emptyMap();
		
		for (String feedId : feedIDs) {
			if (isLimitFeedID(feedId)) {
				if (values.isEmpty()){
					values = new HashMap<String, SortedMap<Long, Map<String,String>>>();
				}
				String limitValue = limitDefinitions.get(LimitDataProvider.getID(feedId));
				if ( limitValue == null) continue;
				List<Long> timeTags = new ArrayList<Long>(2);
				timeTags.add(startTime);
				timeTags.add(endTime);

				SortedMap<Long, Map<String,String>> sortedMap = new TreeMap<Long, Map<String,String>>();
				values.put(feedId, sortedMap);
				for (Long prediction: timeTags) {
					sortedMap.put(prediction, convertPredictionToMap(prediction, limitValue));
				}
			}
		}
		return values;
	}

	@Override
	public Map<String, List<Map<String, String>>> getData(Set<String> feedIDs, TimeUnit timeUnit, long startTime, long endTime) {
		
		Map<String, List<Map<String,String>>> values = Collections.emptyMap();
		
		for (String feedId : feedIDs) {
			if (isLimitFeedID(feedId)) {
				if (values.isEmpty()){
					values = new HashMap<String, List<Map<String,String>>>();
				}
				String limitValue = this.limitDefinitions.get(feedId);
				List<Long> predictions = new ArrayList<Long>(2);
				predictions.add(startTime);
				predictions.add(endTime);
				
				List<Map<String,String>> list = new ArrayList<Map<String,String>>(predictions.size());
				values.put(feedId, list);
				for (Long prediction: predictions) {
					list.add(convertPredictionToMap(prediction, limitValue)); 
				}
			}
		}
		return values;
	}
	
	private Map<String, String> convertPredictionToMap(Long time, String stringifiedValue) {
		Map<String,String> data = new HashMap<String,String>();
		data.put(FeedProvider.NORMALIZED_TIME_KEY, Long.toString(time));
		data.put(FeedProvider.NORMALIZED_VALUE_KEY, stringifiedValue);

		RenderingInfo ri = LimitLineComponent.getRenderingInfo(stringifiedValue);
		data.put(FeedProvider.NORMALIZED_RENDERING_INFO, ri.toString());
		data.put(FeedProvider.NORMALIZED_TELEMETRY_STATUS_CLASS_KEY, "1");
		
		return data;
	}
	
	/** Returns true if this feed ID corresponds to a limit component.
	 * @param the feedId feed id
	 * @return true if this feed ID corresponds to a limit component
	 */
	public static boolean isLimitFeedID(String feedId) {
		return feedId.contains(LimitLineComponent.LIMIT_FEED_PREFIX);
	}
	
	/**
	 * Returns the component ID corresponding to a feedID.
	 * @param topicID the feed ID
	 * @return component ID
	 */
	public static String getID(String topicID) {
		return topicID.substring(topicID.indexOf(':') + 1);
	}
	
	@Override
	public LOS getLOS() {
		return LOS.medium;
	}
	
	@Override
	public boolean isFullyWithinTimeSpan(String feedID, long startTime, TimeUnit timeUnit) {
		return false;
	}
}
