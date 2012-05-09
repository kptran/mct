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
 * MCTNonBlockingLock.java Dec 1, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.lock;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.MCTLock;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.exception.MCTLockException;
import gov.nasa.arc.mct.roles.events.ReloadEvent;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.transaction.MCTTransaction;
import gov.nasa.arc.mct.version.manager.MCTComponentVersionManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * MCT Non-blocking lock implementation.
 *
 */
public class MCTNonBlockingLock implements MCTLock {
    private enum LockState {
        locked, unlocked, unlockingInProgress;
        boolean isLocked() {
            return this == locked;
        }

        boolean isUnlockingInProgress() {
            return this == unlockingInProgress;
        }

        boolean canProceedToLock() {
            return this == unlocked;
        }
    };

    private final String componentId;

    private LockState lockState = LockState.unlocked;
    private String userId;
    private MCTTransaction transaction;
    private Set<View> lockedViewManifestations;
    private boolean lockAllManifestation = false;
    private GlobalLockStrategy globalLockStrategy;

    /**
     * Constructor for non-blocking lock.
     * @param componentId - The component id.
     */
    public MCTNonBlockingLock(String componentId) {
        this.componentId = componentId;
        initTransaction();
    }
    
    /**
     * Overloaded constructor.
     * @param componentId - The component id.
     * @param lockStrategy - Global lock strategy.
     */
    public MCTNonBlockingLock(String componentId, GlobalLockStrategy lockStrategy) {
        this(componentId);
        globalLockStrategy = lockStrategy;
    }

    /**
     * Global lock strategy interface definition.
     */
    public interface GlobalLockStrategy {
        /**
         * Returns true if the lock can be acquired from the cluster.
         * @param componentId for the component.
         * @return boolean flag for lock checking.
         */
        boolean lock(String componentId);
        
        /**
         * Returns true if the lock can be released from the cluster.
         * @param componentId for the component.
         */
        void unlock(String componentId);
    }
    
    
    private boolean acquireLock() {
        return globalLockStrategy == null || globalLockStrategy.lock(componentId);
    }
    
    private void releaseLock() {
        if (globalLockStrategy != null) {
            globalLockStrategy.unlock(componentId); 
        }
    }
    
    /**
     * Locks the view manifestation.
     * @param viewManifestation - The view manifestation.
     * @return boolean - flag to check for the view manfiestation lock.
     */
    public synchronized boolean lock(View viewManifestation) {
        if (!lockState.canProceedToLock() || !acquireLock()) {
            return false;
        }
        lockState = LockState.locked;
        this.userId = GlobalContext.getGlobalContext().getUser().getUserId();
        this.lockedViewManifestations.add(viewManifestation);
        if (viewManifestation != View.WILD_CARD_VIEW_MANIFESTATION) {
            MCTComponentVersionManager.versionized(viewManifestation);
        } else {
            lockAllManifestation = true;
            this.transaction.setAutoCommit(lockAllManifestation);
        }

        return true;
    }

    /**
     * Locks the set of view manifestations.
     * @param viewManifestation - set of view manifestations.
     * @return boolean - flag to check for set of view manfiestation lock.
     */
    public synchronized boolean lock(Set<View> viewManifestation) {
        if (!lockState.canProceedToLock() || !acquireLock()) {
            return false;
        }
        lockState = LockState.locked;
        this.userId = GlobalContext.getGlobalContext().getUser().getUserId();
        this.lockedViewManifestations = viewManifestation;
        if (!this.lockedViewManifestations.contains(View.WILD_CARD_VIEW_MANIFESTATION)) {
            MCTComponentVersionManager.versionized(viewManifestation);
        } else {
            lockAllManifestation = true;
            this.transaction.setAutoCommit(lockAllManifestation);
        }

        return true;
    }
    
    @Override
    public synchronized boolean lockForAllUser() {
        if (!lockState.canProceedToLock()) {
            return false;
        }
        lockState = LockState.locked;
        this.userId = User.WILDCARD_USER.getUserId();
        this.lockedViewManifestations.add(View.WILD_CARD_VIEW_MANIFESTATION);
        lockAllManifestation = true;
        this.transaction.setAutoCommit(lockAllManifestation);

        return true;
    }

    private void initTransaction() {
        this.lockedViewManifestations = new LinkedHashSet<View>();
        transaction = new MCTTransaction(this.lockAllManifestation, this.componentId);
        if (lockAllManifestation) {
            this.lockedViewManifestations.add(View.WILD_CARD_VIEW_MANIFESTATION);
        }
    }

    /**
     * Unlocks the view manifestation.
     * @param viewManifestation - The view manifestation.
     */
    public void unlock(View viewManifestation) {
        unlock(Collections.singleton(viewManifestation));
    }

    /**
     * Unlocks the set of view manifestations.
     * @param viewManifestation - Set o view manifestations.
     */
    public synchronized void unlock(final Set<View> viewManifestation) {
        if (!GlobalContext.getGlobalContext().getUser().getUserId().equals(this.userId)) {
            throw new MCTLockException("Trying to unlock a MCT lock without locking it.");
        }

        if (!viewManifestation.containsAll(lockedViewManifestations)) {
            throw new MCTLockException("Trying to unlock a different manifestation: manifestations being unlocked: " + viewManifestation + ": locked manifestations " + lockedViewManifestations);
        }

        assertLocked();

        try {
            lockState = LockState.unlockingInProgress;

            synchronousCommitTransaction();
            
            if (!lockedViewManifestations.contains(View.WILD_CARD_VIEW_MANIFESTATION)) {
                MCTComponentVersionManager.mergeVersionAndUpdate(lockedViewManifestations);
                initTransaction();
            }
            
            setUserId(null);
            releaseLock();
            setState(LockState.unlocked);
        } finally {
            cleanup(lockedViewManifestations);
        }
    }

    /**
     * unlock all manifestations
     * @param manifestations 
     */
    private void cleanup(Set<View> manifestations) {
        if (lockState == LockState.unlockingInProgress) {
            // avoid violating assertions in abort
            lockState = LockState.locked;
            abort(manifestations);
        }
    }
    
    /**
     * Sets the user id.
     * @param userId - Sets the user id.
     */
    synchronized void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the lock state.
     * @param state - the current lock state.
     */
    synchronized void setState(LockState state) {
        this.lockState = state;
    }

    /**
     * Checks for whether it's locked or not.
     * @return boolean - flag check on whether it's locked or not.
     */
    public synchronized boolean isLocked() {
        return lockState.isLocked();
    }

    /**
     * Checks whether the manifestation is locked or not.
     * @param viewManifestation - The view manifestation.
     * @return boolean - flag check whether manifestation is locked or not.
     */
    public synchronized boolean isManifestationLocked(View viewManifestation) {
        boolean isLocked = lockState.isLocked();
        if (!isLocked) {
            return false;
        }
        return this.lockedViewManifestations.contains(viewManifestation);
    }

    /**
     * Checks for extended locking.
     * @return boolean - flag check for extended locking.
     */
    public synchronized boolean isExtendedLocking() {
        return this.lockedViewManifestations.contains(View.WILD_CARD_VIEW_MANIFESTATION);
    }
    
    /**
     * Checks whether locked or not for all users.
     * @return boolean - flag check for whether locked for all users.
     */
    public synchronized boolean isLockedForAllUsers() {
        return User.WILDCARD_USER.getUserId().equals(this.userId);
    }

    /**
     * Checks for unlocking in progress.
     * @return boolean - flag check for unlocking in progress.
     */
    public synchronized boolean isUnlockingInProgress() {
        return lockState.isUnlockingInProgress();
    }

    private void localUnlock(final Set<View> viewManifestation) {
        if (!GlobalContext.getGlobalContext().getUser().getUserId().equals(this.userId)) {
            throw new MCTLockException("Trying to unlock a MCT lock without locking it.");
        }

        if (!this.lockedViewManifestations.containsAll(viewManifestation)) {
            throw new MCTLockException("Trying to unlock a different manifestation.");
        }

        assertLocked();

        userId = null;
        lockState = LockState.unlocked;
    }

    /**
     * Clears the manifestation.
     */
    public synchronized void clearManifestation() {
        localUnlock(this.lockedViewManifestations);
        this.lockedViewManifestations.clear();
        this.lockAllManifestation = false;
        this.transaction.setAutoCommit(false);
    }

    /**
     * Commits the transaction synchronously.
     */
    public synchronized void synchronousCommitTransaction() {
        assertLockedOrUnlockingInProgress();
        transaction.synchronousCommit();
    }

    @Override
    public synchronized MCTTransaction getTransaction() {
        assertLocked();

        return transaction;
    }

    /**
     * Checks for auto-commit.
     * @return boolean - flag for checking auto-commit.
     */
    public synchronized boolean isAutoCommit() {
        return this.lockedViewManifestations.contains(View.WILD_CARD_VIEW_MANIFESTATION);
    }

    private void assertLocked() {
        if (!lockState.isLocked()) {
            throw new MCTLockException("The lock for component [" + componentId + "] has not been obtained.");
        }
    }

    private void assertLockedOrUnlockingInProgress() {
        if (!lockState.isLocked() && !lockState.isUnlockingInProgress()) {
            throw new MCTLockException("The lock for component [" + componentId + "] has not been obtained.");
        }
    }

    @Override
    public synchronized void abort(Set<View> viewManifestations) {
        if (!GlobalContext.getGlobalContext().getUser().getUserId().equals(this.userId)) {
            throw new MCTLockException("Trying to unlock a MCT lock without locking it. This lock is owned by " + userId + 
                                        " but unlocked by " + GlobalContext.getGlobalContext().getUser().getUserId());
        }

        if (!this.lockedViewManifestations.containsAll(viewManifestations)) {
            throw new MCTLockException("Trying to unlock a different manifestation.");
        }

        assertLocked();

        AbstractComponent masterComponent = viewManifestations.iterator().next().getManifestedComponent().getMasterComponent();

        transaction.clearPendingChanges();

        // Aborts the persistence session
        GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().abortSession(componentId);
        
        for (View viewManifestation : viewManifestations) {
            // Restore view manifestation to reference the master component
            viewManifestation.setManifestedComponent(masterComponent);
            // Restore view manifestation
            viewManifestation.updateMonitoredGUI(new ReloadEvent(masterComponent==null?viewManifestation.getManifestedComponent():masterComponent));
        }
        
        // Restore lock state
        this.lockedViewManifestations.clear();
        this.userId = null;
        initTransaction();
        releaseLock();
        setState(LockState.unlocked);
    }
    
    @Override
    // Use this method with extreme care!! Currently, this method is only used from the JMX API and
    // from the recovery mechanism.
    public synchronized void forceUnlock()
    {
        transaction.clearPendingChanges();

        // Restore lock state
        this.lockedViewManifestations.clear();
        this.userId = null;
        initTransaction();
        
        setState(LockState.unlocked);
        releaseLock();
    }
    

    @Override
    public Set<View> getLockedManifestations() {
        return new HashSet<View>(lockedViewManifestations);
    }
        
    @Override
    public synchronized String getOwnerUserId() {
        return this.userId;
    }

}
