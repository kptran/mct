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
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mock lock manager for unit testing.
 *
 */
public class MockLockManager implements LockManager {
	private final PersistenceBroker persistenceBroker = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
	private final Set<String> lockForAllUsersComponents = new HashSet<String>();
	
	/**
	 * Default Constructor.
	 */
	public MockLockManager() {
		//
	}

	/**
	 * Initialize new lock.
	 * @param componentId - The component id.
	 */
	public void newLock(String componentId) {
		//
	}
	
	@Override
	public void removeLock(String componentId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Checks for lock. Always returns true for unit testing.
	 * @param componentId - The component id.
	 * @param viewManifestation - The view manifestation.
	 * @return boolean - always returns true for unit testing.
	 */
	public boolean lock(String componentId,
			View viewManifestation) {
		return true;
	}

	@Override
	public boolean lock(String componentId) {
		return true;
	}
	
	@Override
	public boolean lock(String componentId, Set<View> viewManifestation) {
		return true;
	}

	/**
	 * Unlock the component.
	 * @param componentId - The component id.
	 * @param viewManifestation - The view manifestation.
	 */
	public void unlock(String componentId,
			View viewManifestation) {
		//
	}
	
	@Override
	public void unlock(String componentId,
			Set<View> viewManifestation) {
		// 
		
	}

	/**
	 * Checks for lock. Always returns true for unit testing.
	 * @param componentId - The component id.
	 * @return boolean - always returns true for unit testing.
	 */
	public boolean isLocked(String componentId) {
		return true;
	}
	
	@Override
	public boolean isManifestationLocked(String componentId,
			View viewManifestation) {
		return true;
	}
	
	@Override
	public boolean isExtendedLocking(String componentId) {
		return false;
	}

	/**
	 * Checks for unlocking in progress. Always returns false for unit testing.
	 * @param componentId - The component id.
	 * @return false - flag check whether unlocking is in progress.
	 */
	public boolean isUnlockingInProgress(String componentId) {
		return false;
	}

	/**
	 * Synchronizes the commit transaction. Does nothing special for unit testing.
	 * @param componentId - component id.
	 */
	public void synchronousCommitTransaction(String componentId) {
		//
	}

	/**
	 * Asynch commit. Does nothing special for unit testing.
	 * @param componentId - component id.
	 */
	public void asynchronousCommit(String componentId) {
		//
	}

	/**
	 * Shares the lock. Does nothing special for unit testing.
	 * @param componentId - component id.
	 */
	public void shareLock(String componentId) {
		//
	}

	/**
	 * Checks for shared lock for unit testing.
	 * @param componentId - component id.
	 * @return false - always return false for unit testing.
	 */
	public boolean isSharedLock(String componentId) {
		return false;
	}

	/**
	 * Pushes the changes to persistence for unit testing.
	 * @param componentId - the component id.
	 * @param daoObject - the DAO object.
	 */
	public void pushChanges(String componentId, DaoObject daoObject) {
		if (persistenceBroker != null) {
			persistenceBroker.save(componentId, daoObject, null);
		}
	}

	/**
	 * Checks for pending transactions for unit testing.
	 * @param componentId - the component id.
	 * @return false - for unit testing.
	 */
	public boolean hasPendingTransaction(String componentId) {
		return false;
	}

	@Override
	public void abort(String componentId, Set<View> viewManifestations) {
	}

	@Override
	public void unlock(String componentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean lockForAllUser(String componentId) {
		this.lockForAllUsersComponents.add(componentId);
		return true;
	}

	@Override
	public boolean isLockedForAllUsers(String componentId) {
		return this.lockForAllUsersComponents.contains(componentId);
	}

	@Override
	public Map<String, Set<View>> getAllLockedManifestations() {
		return null;
	}

	@Override
	public Collection<MCTLock> getAllSharedLocks() {
		return Collections.emptyList();
	}

	@Override
	public boolean isManifestationSetLocked(String componentId, Set<View> viewManifestations) {
		return false;
	}

	@Override
	public String getOwnerUserId(String componentId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Gets the lock owner for unit testing.
	 * @param componentId - the component id.
	 * @return null - for unit testing.
	 */
	public String getLockOwner(String componentId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
