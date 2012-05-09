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
/**
 * IdentityManager Sept 11, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */

package gov.nasa.arc.mct.identitymgr;

/**
 * Provides user and group information interface.
 */
public interface IIdentityManager {

    // public void start(String siteProperties) throws IOException;

    /**
     * Gets the current MCT group.
     * @return current group.
     */
    public String getCurrentGroup();

    /**
     * Gets the current MCT user.
     * @return current user.
     */
    public String getCurrentUser();
    
    /**
     * Checks for whether the monitor is running or not.
     * @return boolean check whether monitor is running or not.
     */
    public boolean isMonitorRunning();

    // Not known if needed for Module1
    // public String[] getAllUsers();

    // public String[] getAllGroups();

    // public String[] getGroupMembership(String user);

    // public String[] getUsersInGroup(String group);
}
