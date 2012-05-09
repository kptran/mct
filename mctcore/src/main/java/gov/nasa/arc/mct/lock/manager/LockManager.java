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
package gov.nasa.arc.mct.lock.manager;

import gov.nasa.arc.mct.components.MCTLock;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Lock manager interface.
 *
 */
public interface LockManager {
    
    /**
     * Provides new lock.
     * @param componentId - The component id.
     */
    public void newLock(String componentId);
    
    /**
     * Removes the lock.
     * @param componentId - The component id.
     */
    public void removeLock(String componentId);
    
    /**
     * Checks whether it's locked or not by providing component id and view manifestation.
     * @param componentId - The component id.
     * @param viewManifestation - The view manifestation.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean lock(String componentId, View viewManifestation);
    
    /**
     * Checks whether it's locked or not by providing component id and view set manifestations.
     * @param componentId - The component id.
     * @param viewManifestation - The set of view manifestation.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean lock(String componentId, Set<View> viewManifestation);
    
    /**
     * Checks whether it's locked or not by providing component id.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean lock(String componentId);
    
    /**
     * Checks whether it's locked for all users.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean lockForAllUser(String componentId);
    
    /**
     * Unlock by providing component id and view manifestation.
     * @param componentId - The component id.
     * @param viewManifestation - The view manifestation.
     */
    public void unlock(String componentId, View viewManifestation);
    
    /**
     * Unlock by providing component id and set of view manifestations.
     * @param componentId - The component id.
     * @param viewManifestation  - The set of view manifestation.
     */
    public void unlock(String componentId, Set<View> viewManifestation);
    
    /**
     * Unlock by providing component id.
     * @param componentId - The component id.
     */
    public void unlock(String componentId);
    
    /**
     * Checks whether component is locked or not.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean isLocked(String componentId);
    
    /**
     * Checks whether the manifestation is locked or not.
     * @param componentId - The component id.
     * @param viewManifestation - The view manifestation.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean isManifestationLocked(String componentId, View viewManifestation);
    
    /**
     * Checks whether the set of manifestation is locked.
     * @param componentId - The component id.
     * @param viewManifestations - The set of view manifestations.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean isManifestationSetLocked(String componentId, Set<View> viewManifestations);
    
    /**
     * Checks for extended locking.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean isExtendedLocking(String componentId);
    
    /**
     * Checks whether it's locked for all users.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean isLockedForAllUsers(String componentId);
    
    /**
     * Checks whether it's unlocking in progress.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean isUnlockingInProgress(String componentId);
    
    /**
     * Checks whether it's a shared lock.
     * @param componentId - The component id.
     */
    public void shareLock(String componentId);
    
    /**
     * Pushes the changes.
     * @param componentId - The component id.
     * @param daoObject - DAO object to push changes to.
     */
    public void pushChanges(String componentId, DaoObject daoObject);
    
    /**
     * Checks whether the component has pending transaction.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked or not.
     */
    public boolean hasPendingTransaction(String componentId);
    
    /**
     * This method should abort all pending changes and release the lock.
     * @param componentId the currently locked component's ID
     * @param viewManifestations the set of modified view manifestations
     */
    public void abort(String componentId, Set<View> viewManifestations);
    
    /**
     * Gets all the locked manifestations.
     * @return map of <String, Set<View>>
     */
    public Map<String, Set<View>> getAllLockedManifestations();
    
    /**
     * Gets all the shared locks.
     * @return collection of MCTLock
     */
    public Collection<MCTLock> getAllSharedLocks();
    
    /**
     * Gets the current owner user id.
     * @param componentId - The component id.
     * @return owner userid
     */
    public String getOwnerUserId(String componentId);
    
    /**
     * Gets the lock owner id.
     * @param componentId - The component id.
     * @return lock owner userid
     */
    public String getLockOwner(String componentId);
    
}
