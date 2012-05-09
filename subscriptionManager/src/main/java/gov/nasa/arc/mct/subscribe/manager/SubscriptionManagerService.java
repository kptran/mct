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
package gov.nasa.arc.mct.subscribe.manager;

import gov.nasa.arc.mct.event.services.EventProvider;
import gov.nasa.arc.mct.platform.spi.SubscriptionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

public class SubscriptionManagerService implements SubscriptionManager {
	private LogService log = null;
	private BundleContext bc = null;

	private Set<EventProvider> providers = new HashSet<EventProvider>();
	private Set<String> pendingFeedIDs = new HashSet<String>();
	private UnsubscriptionTimer unsubscriptionTimer = new UnsubscriptionTimer(this);

	public synchronized void activate(ComponentContext context) {
		log.log(LogService.LOG_INFO, "SubscriptionManager activated");
		bc = context.getBundleContext();

		trySubscribePendingFeeds();
		unsubscriptionTimer.schedule();
	}
	
	public synchronized void deactivate(ComponentContext context) {
		unsubscriptionTimer.cancel();
	}

	/**
	 * Sets the logger object to use.
	 * 
	 * @param newLog
	 *            the new logging service object to use
	 */
	protected synchronized void setLogger(LogService newLog) {
		log = newLog;
	}

	/**
	 * Removes the logger service.
	 * 
	 * @param removedLog
	 *            the log service being removed
	 */
	protected synchronized void unsetLogger(LogService removedLog) {
		log = null;
	}
	
	@Override
	public void refresh() {
		if (bc != null) {
			for (EventProvider provider : providers) {
				if (provider != null) {
					provider.refresh();
				}
			}
		}
	}

	public synchronized void subscribe(String... feedIDs) {
		List<String> subscriptions = new ArrayList<String>(Arrays.asList(feedIDs));
		Iterator<String> it = subscriptions.iterator();
		while (it.hasNext()) {
			if (this.unsubscriptionTimer.removeEligibleUnsubscriber(it.next())) {
				it.remove();
			}
		}
		
		if (bc != null) {
			Collection<String> feeds = subscribeToFeeds(subscriptions.toArray(new String[subscriptions.size()]));
			pendingFeedIDs.removeAll(feeds);
		} else {
			pendingFeedIDs.addAll(subscriptions);
		}
	}

	public synchronized void unsubscribe(String... feedIDs) {
		List<String> feeds = new ArrayList<String>(Arrays.asList(feedIDs));
		Iterator<String> it = feeds.iterator();
		while (it.hasNext()) {
			if (pendingFeedIDs.remove(it.next())) {
				it.remove();
			}
		}
		
		if (bc != null) {
			for (String feedID:feeds) {
				unsubscriptionTimer.addEligibleUnsubscriber(feedID);
			}
		}
	}

	private String[] createTopics(String...feedIDs) {
		String[] feeds = new String[feedIDs.length];
		for (int i = 0; i < feedIDs.length; i++) {
			feeds[i] = EventProvider.TELEMETRY_TOPIC_PREFIX+feedIDs[i];
		}
		
		return feeds;
	}
	
	synchronized void unsubscribeFromFeeds(String... feedIDs) {
		String[] topics = createTopics(feedIDs);
		for (EventProvider provider : providers) {
			if (provider != null) {
				provider.unsubscribeTopics(topics);
			}
		}
	}

	/**
	 * 
	 * @param feedIDs
	 * @return list of feeds that were not able to be subscribed to
	 */
	private Collection<String> subscribeToFeeds(String... feedIDs) {
		List<String> originalFeeds = new ArrayList<String>(Arrays.asList(feedIDs));
		Iterator<String> it = originalFeeds.iterator();
		while (it.hasNext()) {
			if (unsubscriptionTimer.removeEligibleUnsubscriber(it.next())) {
				it.remove();
			}
		}
		
		if (!originalFeeds.isEmpty()) {
			String[] topics = createTopics(feedIDs);
			for (EventProvider provider : providers) {
				if (provider != null) {
					originalFeeds.removeAll(provider.subscribeTopics(topics));
				}
			}
		}
		return originalFeeds;
	}

	/**
	 * Handles a new subscription provider binding event. If the component has
	 * already been activated, process the binding, else make the binding
	 * pending and process it at activation.
	 * 
	 * @param providerRef
	 *            a reference to the provider that has been added
	 */
	public synchronized void addProvider(EventProvider providerRef) {
		this.providers.add(providerRef);
		trySubscribePendingFeeds();
	}

	private void trySubscribePendingFeeds() {
		pendingFeedIDs.retainAll(subscribeToFeeds(pendingFeedIDs.toArray(new String[pendingFeedIDs.size()])));
	}

	/**
	 * Updates the subscriptions when an <code>EventProvider</code> has been
	 * unregistered. If we have been activated, process the removal of the
	 * subscriber. Otherwise, remove the provider from the pending providers
	 * set.
	 * 
	 * @param providerRef
	 *            a reference to the provider that has been removed
	 */
	public synchronized void removeProvider(EventProvider providerRef) {
		this.providers.remove(providerRef);
	}
}
