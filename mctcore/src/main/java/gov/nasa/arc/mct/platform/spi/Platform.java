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


import gov.nasa.arc.mct.api.feed.FeedAggregator;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.services.activity.TimeService;
import gov.nasa.arc.mct.services.component.ComponentTagService;
import gov.nasa.arc.mct.services.component.MenuManager;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ProviderDelegateService;
import gov.nasa.arc.mct.services.component.TagService;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.Dictionary;

/**
 * The <code>Platform</code> interface represents the support required in the
 * underlying MCT core. This class allows the platform implementation to be 
 * encapsulated and used within external facing API components. An instance of this
 * class will be made available as an OSGi service during runtime. This interface
 * supports the use of delegation instead of extension allowing the API and platform 
 * code to be decoupled. This interface is designed for use only for code within
 * this package and thus is outside the scope of compatibility guarantees for other
 * APIs. 
 * 
 * <em>This class is not intended to be used by component authors</em>
 * @author chris.webster@nasa.gov
 */
public interface Platform {
    
    /**
     * Provides an instance of the window manager. 
     * @return window manager provided by the platform
     */
    public WindowManager getWindowManager();

    /**
     * Provides an instance of the persistence service.
     * @return persistence service provided by the platform.
     */
    public PersistenceService getPersistenceService();
    
    /**
     * Provides an instance of the component registry.
     * @return component registry provided by the platform.
     */
    public CoreComponentRegistry getComponentRegistry();
    
    /**
     * Gets the current user.
     * @return current user.
     */
    public User getCurrentUser();
    
    /**
     * Provides an instance of lock manager.
     * @return lock manager provided by the platform.
     */
    public LockManager getLockManager();
    
    /**
     * Provides an instance of the policy manager.
     * @return policy manager provided by the platform
     */
    public PolicyManager getPolicyManager();
    
    /**
     * Provides an instance of the menu manager.
     * @return
     */
    public MenuManager getMenuManager();
    
    /**
     * Provides an instance of the subscription manager.
     * @return subscription manager provided by the platform. 
     */
    public SubscriptionManager getSubscriptionManager();
    
    /**
     * Provides an instance of the time service.
     * @return time service provided by the platform. 
     */
    public TimeService getTimeService();

    /**
     * Provides an instance of the default component provider.
     * @return provider to access various types of default components
     */
    public DefaultComponentProvider getDefaultComponentProvider();
    
    /**
     * Registers a service in the OSGi service registry. The same object
     * may be registered more than once under different service classes.
     * If the same object is registered more than once under the same
     * service class, only the first registration is effective. The service
     * properties may be null if the service has no properties.
     * 
     * @param serviceClass the class under which the service should be registered.
     * @param serviceObject the Java object providing the service
     * @param props properties for the new service registration, or null if the service has no properties
     * @throws IllegalArgumentException if the service object is an instance of the service class
     */
    public void registerService(Class<?> serviceClass, Object serviceObject, Dictionary<String,Object> props) throws IllegalArgumentException;
    
    /**
     * Unregisters a Java object as an OSGi service. If the object
     * is currently registered as an OSGi service, it is removed
     * from the OSGi service registry. If the object is not currently
     * registered as an OSGi service, this method has no effect.
     * If the same object is registered multiple times as an OSGi
     * service under different service classes, all registrations
     * are removed by this call.
     * 
     * @param serviceObject the Java object providing the service
     */
    public void unregisterService(Object serviceObject);
    
    /**
     * Returns the <code>TagService</code>.
     * @return the tag service
     */
    public TagService getTagService();
    
    /**
     * Returns the <code>ComponentTagService</code>.
     * @return the component tag service
     */
    public ComponentTagService getComponentTagService();
    
    public ProviderDelegateService getProviderDelegateService();

    /**
     * Returns the feed aggregator.
     * @return the feed aggregator
     */
    public FeedAggregator getFeedAggregator();
}
