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

import gov.nasa.arc.mct.services.internal.component.User;

import java.util.Set;

/**
 * A service that provides role information.   
 *
 */
public interface RoleService {

    /**
     * Lookup the primary role associated with user. If the primary role
     * matches roleID, return true, else return false. 
     * @param user the user
     * @param roleId the role
     * @return Returns true if the user belongs to this role, otherwise false
     */
    public boolean hasRole(User user, String roleId);

    /**
     * Gets all known roles.
     * @return list of known roles
     */
    public Set<String> getAllRoles();

    /**
     * Gets all known users.
     * @return list of known users
     */
    public Set<String> getAllUsers();

    
    /**
     * Gets the default role.
     * @return the default role
     */
    public String getDefaultRole();
    
    /**
     * Gets the description of a role.
     * @param role the role
     * @return description of the role, or null if this role does not exist.
     */
    public String getDescription(String role);
    
    /**
     * Get the primary role for a user.  Get the list of roles for this user from 
     * Role Data.  If this user has an entry in Role Data, the primary role is the first listed. 
     * If the user has no entry in Role Data, return the default role for all users.
     * @param user the user
     * @return primary role of user or default role if this user does not exist in the role data.
     */
    public String getPrimaryRole(User user);

    /** 
     * Gets all the roles for a user.
     * @param user the user
     * @return the roles
     */
    public Set<String> getAllRoles(String user);
}
