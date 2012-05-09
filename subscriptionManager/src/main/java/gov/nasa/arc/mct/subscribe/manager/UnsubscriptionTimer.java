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

import gov.nasa.arc.mct.subscribe.manager.config.ConfigurationService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UnsubscriptionTimer extends Timer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UnsubscriptionTimer.class);

	private TreeSet<UnsubscriptionContext> unsubscriptionList = new TreeSet<UnsubscriptionContext>(
			new Comparator<UnsubscriptionContext>() {
				public int compare(UnsubscriptionContext o1,
						UnsubscriptionContext o2) {
					
					if (o1.timeToUnsubscribeInMills < o2.timeToUnsubscribeInMills) {
						return -1;
					}
					if (o1.timeToUnsubscribeInMills == o2.timeToUnsubscribeInMills) {
						return o1.equals(o2) ? 0 : System.identityHashCode(o2) - System.identityHashCode(o1);
					}
					return 1;
				}
			});

	private Map<String, UnsubscriptionContext> unsubcriptionListIndex = new HashMap<String, UnsubscriptionContext>();

	private final int timerSleepTime;
	private final SubscriptionManagerService subscriptionManager;

	UnsubscriptionTimer(SubscriptionManagerService subscriptionManager) {
		super("Unsubscription Timer");
		this.timerSleepTime = ConfigurationService.getInstance()
				.getTimerSleepTime();
		this.subscriptionManager = subscriptionManager;

	}

	void addEligibleUnsubscriber(String feedID) {
		synchronized (subscriptionManager) {
			if (!unsubcriptionListIndex.containsKey(feedID)) {
				UnsubscriptionContext uc = new UnsubscriptionContext(feedID);
				unsubscriptionList.add(uc);
				unsubcriptionListIndex.put(feedID, uc);
			}
		}
	}

	boolean removeEligibleUnsubscriber(String feedID) {
		synchronized (subscriptionManager) {
			UnsubscriptionContext uc = unsubcriptionListIndex.remove(feedID);
			return uc != null && unsubscriptionList.remove(uc);
		}
	}

	public void schedule() {
		synchronized (subscriptionManager) {
			super.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					synchronized (subscriptionManager) {
						for (Iterator<UnsubscriptionContext> i = unsubscriptionList
								.iterator(); i.hasNext();) {
							UnsubscriptionContext uc = i.next();
							if (!uc.isTimeToUnsubscribe()) {
								break;
							}

							String feedID = uc.feedID;
							LOGGER.debug(
									"removing...{} from unsubscription list.",
									uc);

							subscriptionManager.unsubscribeFromFeeds(feedID);
							i.remove();
							unsubcriptionListIndex.remove(feedID);
						}
					}

					// call SubscriptionManager
				}
			}, timerSleepTime, timerSleepTime);
		}
	}

	private final static class UnsubscriptionContext {
		private final long timeToUnsubscribeInMills;
		private final String feedID;

		public UnsubscriptionContext(String feedID) {
			int unsubscriptionGracePeriod = ConfigurationService.getInstance()
					.getUnSubscriptionGracePeriod();

			this.feedID = feedID;
			this.timeToUnsubscribeInMills = unsubscriptionGracePeriod
					+ System.currentTimeMillis();
		}

		public boolean isTimeToUnsubscribe() {
			return System.currentTimeMillis() > timeToUnsubscribeInMills;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof UnsubscriptionContext &&
				feedID.equals(((UnsubscriptionContext) obj).feedID);
		}

		@Override
		public int hashCode() {
			return feedID.hashCode();
		}

		@Override
		public String toString() {
			return feedID;
		}
	}

}
