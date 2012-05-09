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
 * MCTLock.java Dec 1, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.components;

import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.transaction.MCTTransaction;

import java.util.Set;

/**
 * Defines an object which supports locking and unlocking
 * operations, to enforce mutual exclusion during updates
 * of another, shared object.
 */
public interface MCTLock {

    /**
     * Gets a lock on a shared view manifestation, in order
     * to perform updates on that shared object.
     * 
     * @param viewManifestation the view manifestation to lock
     * @return true, if the lock was obtained
     */
	public boolean lock(View viewManifestation);
	
	/**
	 * Gets locks on a set of shared view manifestations. This
	 * is an atomic operation: either all locks are gained or
	 * none are.
	 * 
	 * @param viewManifestations the set of view manifestations to lock
	 * @return true, if the locks are obtained
	 */
	public boolean lock(Set<View> viewManifestations);

	/**
	 * Locks all users for editing. All users maintained by MCT
	 * may be edited after the lock is obtained.
	 * 
	 * @return true, if the lock was gained successfully
	 */
	public boolean lockForAllUser();
	
	/**
	 * Releases a lock on a shared view manifestation.
	 * 
	 * @param viewManifestation the view manifestation to unlock
	 */
	public void unlock(View viewManifestation);
	
	/**
	 * Releases locks on a set of shared view manifestations. This
	 * is an atomic operation: all locks are released at the same
	 * time.
	 * 
	 * @param viewManifestations the set of view manifestations to unlock
	 */
	public void unlock(Set<View> viewManifestations);
	
	/**
	 * Forces the release of the lock. All shared objects locked
	 * by this lock are unlocked afterward.
	 */
	public void forceUnlock();
	
	/**
	 * Tests whether any shared objects are locked.
	 * 
	 * @return true, if at least one object is locked by this lock
	 */
	public boolean isLocked();
	
	/**
	 * Tests whether a particular view manifestation is locked
	 * by this lock.
	 * 
	 * @param viewManifestation the view manifestation to test for
	 * @return true, if the view manifestation is locked
	 */
	public boolean isManifestationLocked(View viewManifestation);
	
	/**
	 * Tests whether the lock is in extended locking mode.
	 * 
	 * @return true, if the lock is in extended locking mode
	 */
	public boolean isExtendedLocking();
	
    /**
     * Tests whether all users are locked for editing.
     * 
     * @return true, if users are locked for editing
     */
	public boolean isLockedForAllUsers();
	
	/**
	 * Tests whether objects are in the middle of being unlocked.
	 * 
	 * @return true, if unlocking is currently pending
	 */
	public boolean isUnlockingInProgress();
	
	/**
	 * Gets the current transaction.
	 * 
	 * @return the current transaction
	 */
	public MCTTransaction getTransaction();
	
	/**
	 * Empties the set of view manifestations locked by this lock.
	 */
	public void clearManifestation();
	
	/**
	 * Abandons the locks on the given view manifestations and restores
	 * the view manifestations to their state prior to locking.
	 * 
	 * @param viewManifestations the view manifestations to abandon locks for
	 */
	public void abort(Set<View> viewManifestations);

	/**
	 * Returns the set of view manifestations locked by this lock object.
	 * 
	 * @return the set of locked manifestations
	 */
	public Set<View> getLockedManifestations();
	
	/**
	 * Gets the user ID that has locked objects using this lock.
	 * 
	 * @return the ID of the locking user
	 */
	public String getOwnerUserId();
			
}
