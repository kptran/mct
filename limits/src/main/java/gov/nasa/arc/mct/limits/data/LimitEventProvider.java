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
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.event.services.EventProvider;
import gov.nasa.arc.mct.limits.ComponentRegistryAccess;
import gov.nasa.arc.mct.limits.LimitLineComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * This class supports the event provider interface for limits. Limits maintain their own telemetry
 * value in the component, so there is no need for a subscription. 
 */
public class LimitEventProvider implements EventProvider {
	private static final Logger logger = LoggerFactory.getLogger(LimitEventProvider.class);
	private final AtomicReference<DataProvider> dataProvider = new AtomicReference<DataProvider>();

	@Override
	public Collection<String> subscribeTopics(String... topics) {
		List<String> topicIDs = new ArrayList<String>(Arrays.asList(topics));
		Iterator<String> it = topicIDs.iterator();
		while (it.hasNext()) {
			String topicID = it.next();
			if (! LimitDataProvider.isLimitFeedID(topicID)) {
				it.remove();
			} else {
				// keep map of active limit Line subscriptions, and the component's limit line attributes
				AbstractComponent component = ComponentRegistryAccess.getComponentRegistry().getComponent(LimitDataProvider.getID(topicID));
				try {
					assert component != null : "Limit Object not found: "+ topicID;
					LimitLineComponent comp = LimitLineComponent.class.cast(component);
					
					String value = comp.getModel().getValue();
					comp.setDataProvider(dataProvider.get());
								
					if (dataProvider.get() == null) {
						throw new Exception("Null data provider");
					}
					LimitDataProvider ldp = (LimitDataProvider)dataProvider.get();
					ldp.putLimitDefinition(LimitDataProvider.getID(topicID), value);
				} catch (Exception e) {
					logger.error("invalid data type for limit line value", e);
				}
			}
		}
		return topicIDs;
	}
	
	@Override
	public void unsubscribeTopics(String... topics) {
		for (String topic:topics) {
			if (topic == null || !LimitDataProvider.isLimitFeedID(topic)) {
				continue;
			} else {
				String symbolKey = LimitDataProvider.getID(topic);
				LimitDataProvider ldp = (LimitDataProvider)dataProvider.get();
				ldp.clearLimitDefinition(symbolKey);
				return;			
			}
		}
	}
	
	@Override
	public void refresh() {
	}
	
	public void setDataProvider(DataProvider dp) {
		if (!LimitDataProvider.class.isInstance(dp)) {
			return;
		}
		dataProvider.set(dp);
	}

	public void releaseDataProvider(DataProvider dp) {
		if (!LimitDataProvider.class.isInstance(dp)) {
			return;
		}
		dataProvider.set(null);
	}
}
