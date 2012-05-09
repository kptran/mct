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
package gov.nasa.arc.mct.services.internal.component;

/**
 * MCT user interface.
 */
public interface User {
    
    /**
     * Wildcard user object defined.
     */
    public final static User WILDCARD_USER = new User() {
        
        /**
         * Gets any user id.
         * @return "ANY_USER"
         */
        public String getUserId() {
            return "ANY_USER";
        }
        
        /**
         * Gets the discipline id.
         * @return "ALL_DISCIPLINE"
         */
        public String getDisciplineId() {
            return "ALL_DISCIPLINE";
        }
        
        /**
         * Gets the valid user by user id.
         * @param userID - user id.
         * @return User - the valid user this object or null if not found.
         */
        public User getValidUser(String userID) {
            if (getUserId().equals(userID)) {
                return this;
            }
            return null;
        }
        
        @Override
        public boolean hasRole(String role) {
            return false;
        }
    };
    
    /**
     * Gets the user id.
     * @return userId - user id.
     */
    public String getUserId();
    
    /**
     * Gets the discipline id.
     * @return disciplineId - the discipline id.
     */
    public String getDisciplineId();

    /**
     * Gets the valid user by user id.
     * @param userID - user id.
     * @return User - the valid user.
     */
    public User getValidUser(String userID);
    
    /**
     * Checks whether user has a specific role.
     * @param role - the user role.
     * @return boolean - flag to check whether the user has any role.
     */
    public boolean hasRole(String role);
}
