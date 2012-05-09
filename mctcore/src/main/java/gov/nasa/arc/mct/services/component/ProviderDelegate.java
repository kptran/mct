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
package gov.nasa.arc.mct.services.component;

/**
 * Provides an interface for providers to implement delegate methods. 
 */
public interface ProviderDelegate {
    
    /**
     * This method is called by <code>PersistenceService.addNewUser()</code>
     * after creating a new user in the mct_users table in the database. 
     * @param sessionId Hibernate session used to create the new user
     * @param userId user's unique ID
     * @param group the group in which the user belongs to
     */
    void userAdded(String sessionId, String userId, String group);
    
    /**
     * This method is called when <code>PersistenceService.addNewUser()</code>
     * fails to create the new user.
     * @param userId user ID
     * @param group the group in which the user belongs to 
     */
    void userAddedFailed(String userId, String group);
    
    /**
     * This method is called after <code>PersistenceService.addNewUser()</code>
     * successfully creates the new user. This method can be used to clean
     * up certain states.
     * @param userId user ID
     * @param group the group in which the user belongs to
     */
    void userAddedSuccessful(String userId, String group);
    
    /**
     * Provides the status of this plugin bundle during MCT startup.
     * @return <code>PluginStartupStatus</code> during MCT startup
     */
    PluginStartupStatus check();
    
}
