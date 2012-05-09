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
package gov.nasa.arc.mct.platform.spi;

/**
 * Provides a generic service for feed subscription management. This interface will be implemented by subscription 
 * providers to allow subscriptions to be done via reference counting the number of feed manifestations. 
 *
 */
public interface SubscriptionManager {
    /**
     * Subscribe to the specified feed. The mechanism for subscription (and even whether it is necessary) is at
     * the sole discretion of the providers. 
     * @param feedIDs opaque strings representing a unique identifier for the feed (this string must be recognized 
     * uniquely among multiple feeds). A concrete example of this would be the URL for an ATOM feed. 
     */
    public void subscribe(String... feedIDs);
       
    /**
     * Remove the subscription from the specified feed. The mechanism for removing the subscription (and even whether it is necessary) is at
     * the sole discretion of the providers. 
     * @param feedIDs opaque string representing a unique identifier for the feed (this string must be recognized 
     * uniquely among multiple feeds). A concrete example of this would be the URL for an ATOM feed. 
     */
    public void unsubscribe(String... feedIDs);
    
    /**
     * Refresh all feeds. This will ask the isp adapter to push the latest value to the client.
     */
    public void refresh();
}
