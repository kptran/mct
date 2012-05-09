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
 * MCTLockManager.java Dec 1, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.lock.manager;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.MCTLock;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.MCTNonBlockingLock;
import gov.nasa.arc.mct.lock.MCTNonBlockingLock.GlobalLockStrategy;
import gov.nasa.arc.mct.lock.exception.MCTLockException;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.transaction.MCTTransaction;
import gov.nasa.arc.mct.util.IdGenerator;

import java.awt.Frame;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DB lock manager implementation.
 *
 */
public class DbLockManager implements LockManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DbLockManager.class);
    private Map<String, MCTLock> localLocks = new HashMap<String, MCTLock>();
    private Map<String, MCTLock> sharedLocks = new HashMap<String, MCTLock>();
    private static final int LOCK_TIMEOUT_IN_MINUTES = 480;
    private final String sessionId;
    /**
     * This is used to provide exclusive locks in MySQL. This is part of a composite unique index
     * where NULL allows duplicate entries. Using a common string provides a way to ensure exclusivity, but changing this
     * value to null would support optimistic locking. 
     */
    private static final String EXCLUSIVE_MARKER = "x";
    private final GlobalLockStrategy lockStrategy;
    
    /**
     * Default constructor for lock manager.
     */
    DbLockManager() {
        sessionId = IdGenerator.nextComponentId();
        lockStrategy = new DbGlobalLockStrategy();
    }
    
    /**
     * Construct a new lock.
     * @param componentId - Component Id.
     */
    public synchronized void newLock(String componentId) {
        newLock(componentId, View.WILD_CARD_VIEW_MANIFESTATION);
    }

    private MCTLock newLock(String componentId, View viewManifestation) {
        return newLock(componentId, Collections.singleton(viewManifestation));
    }

    private MCTLock newLock(String componentId, Set<View> viewManifestation) {
        MCTLock lock = getLock(componentId);
        if (lock != null) {
            return lock;
        }

        boolean isShared = false;
        for (View manifest : viewManifestation) {
            if (manifest.getManifestedComponent().isShared()) {
                isShared = true;
                break;
            }
        }

        lock = new MCTNonBlockingLock(componentId, isShared ? lockStrategy : null);
        
        if (isShared) {
            sharedLocks.put(componentId, lock);
        } else {
            localLocks.put(componentId, lock);
        }
        return lock;
    }
    
    /**
     * Removes an existing lock.      
     * @param componentId - Component Id.
     */
    public synchronized void removeLock(String componentId) {
        assertHasLock(componentId);
        MCTLock lock = this.localLocks.remove(componentId);
        if (lock == null) {
            this.sharedLocks.remove(componentId);
        }
    }
    
    @Override
    public synchronized String getOwnerUserId(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            return "No user";
        }
        return lock.getOwnerUserId();
        
    }

    /**
     * Checks the view manifestation based on component id for a lock.
     * @param componentId - Component id.
     * @param viewManifestation - View manifestation.
     * @return component is locked or not (defaults to false).
     */
    public synchronized boolean lock(String componentId, View viewManifestation) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            lock = newLock(componentId, viewManifestation);
        }

        if (lock.lock(viewManifestation)) {
            Set<View> hashSet = new HashSet<View>();
            hashSet.add(viewManifestation);
            fireEnterLockedState(hashSet);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean lock(String componentId, Set<View> viewManifestations) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            lock = newLock(componentId, viewManifestations);
        }

        if (lock.lock(viewManifestations)) {
            fireEnterLockedState(viewManifestations);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean lock(String componentId) {        
        if (lock(componentId, View.WILD_CARD_VIEW_MANIFESTATION)) {
            AbstractComponent component = GlobalComponentRegistry.getComponent(componentId);
            if (component != null) {
                fireEnterLockedState(component.getAllViewManifestations());
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean lockForAllUser(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            lock = newLock(componentId, View.WILD_CARD_VIEW_MANIFESTATION);
        }

        if (lock.lockForAllUser()) {
            AbstractComponent component = GlobalComponentRegistry.getComponent(componentId);
            fireEnterLockedState(component.getAllViewManifestations());
            return true;
        }
        return false;

    }

    /**
     * Unlock the component.
     * @param componentId - The component id argument.
     */
    public synchronized void unlock(String componentId) {
        MCTLock lock = getLock(componentId);
        try {
            unlock(componentId, View.WILD_CARD_VIEW_MANIFESTATION);
        } finally {
            if (!lock.isLocked()) {
                AbstractComponent component = GlobalComponentRegistry.getComponent(componentId);
                if (component != null) {
                    fireExitLockedState(component.getAllViewManifestations());
                }
            }
        }
    }

    /**
     * Unlocks the view manifestation based on the component id. 
     * @param componentId - Component id.
     * @param viewManifestation - View manifestation.
     */
    public synchronized void unlock(String componentId, View viewManifestation) {
        unlock(componentId,Collections.singleton(viewManifestation));
    }

    @Override
    public void unlock(String componentId, Set<View> viewManifestations) {
        assertHasLock(componentId);

        MCTLock lock = getLock(componentId);
        try {
            lock.unlock(viewManifestations);
        } finally {
            if (!lock.isLocked()) {
                fireExitLockedState(viewManifestations);
            }
        }
    }

    /**
     * Force unlock the component.
     * @param componentId - The component id.
     */
    public synchronized void forceUnlock(String componentId) {
        assertHasLock(componentId);
        
        MCTLock lock = getLock(componentId);
        lock.forceUnlock();
        
        
        AbstractComponent component = GlobalComponentRegistry.getComponent(componentId);
        fireExitLockedState(component.getAllViewManifestations());
    }
    
    /**
     * Checks whether component is locked or not.
     * @param componentId - The component id.
     * @return boolean - checks for whether a component is locked or not.
     */     
    public synchronized boolean isLocked(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            lock = getLock(HibernateUtil.getAssociatedDelegateSessionId(componentId));
            if (lock == null) {
                return false;
            }
        }
        return lock.isLocked();
    }

    /**
     * Checks whether the manifestation is locked or not.
     * @param componentId - The component id.
     * @param viewManifestation - The view manifestation argument.
     * @return boolean - checks for whether a component is locked or not.
     */
    public synchronized boolean isManifestationLocked(String componentId, View viewManifestation) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            return false;
        }
        return lock.isManifestationLocked(viewManifestation);
    }

    /**
     * Checks whether the manifestation set is locked or not.
     * @param componentId - The component id
     * @param viewManifestations - The view manifestation argument.
     * @return boolean flag to check whether the manifestation set is locked.
     */
    @Override
    public boolean isManifestationSetLocked(String componentId, Set<View> viewManifestations) {
        MCTLock lock = getLock(componentId);
        if (lock == null)
            return false;
        
        Set<View> lockedManifestations = lock.getLockedManifestations();

        // Compare two sets of manifestations
        
        if (lockedManifestations.size() != viewManifestations.size())
            return false;

        if (!lockedManifestations.containsAll(viewManifestations))
            return false;

        if (!viewManifestations.containsAll(lockedManifestations))
            return false;

        return true;
    }

    /** 
     * Checks for extended locking or not.
     * @param componentId - The component id.
     * @return boolean - extended locking or not.
     */
    public synchronized boolean isExtendedLocking(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            return false;
        }
        return lock.isExtendedLocking();
    }
    
    /**
     * Checks whether it's locked for all users.
     * @param componentId - The component id.
     * @return boolean - flag to check whether it's locked for all users.
     */
    @Override
    public synchronized boolean isLockedForAllUsers(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            return false;
        }
        return lock.isLockedForAllUsers();
    }

    /**
     * Checks for whether locking is in progress or not.
     * @param componentId - The component id.
     * @return boolean - unlocking is in progress or not.
     */
    public synchronized boolean isUnlockingInProgress(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            return false;
        }
        return lock.isUnlockingInProgress();
    }

    /**
     * Shared lock.
     * @param componentId - The component id.
     */
    public synchronized void shareLock(String componentId) {
        MCTLock lock = localLocks.remove(componentId);
        if (lock != null) {
            MCTLock sharedLock = new MCTNonBlockingLock(componentId,lockStrategy);
            if (lock.isLockedForAllUsers()) {
                sharedLock.lockForAllUser();
            }
            sharedLocks.put(componentId, sharedLock);
            
            if (!lock.isLockedForAllUsers()) {
                lock.clearManifestation();

                AbstractComponent component = GlobalComponentRegistry.getComponent(componentId);
                fireExitLockedState(component.getAllViewManifestations());
            }
        }
    }

    /**
     * Checks for whether the component is a shared lock.
     * @param componentId - The component id.
     * @return boolean - whether component is a shared lock or not.
     */
    public synchronized boolean isSharedLock(String componentId) {
        return sharedLocks.containsKey(componentId);
    }

    /**
     * Pushes the changes for the lock state.
     * @param componentId - The component id.
     * @param daoObject - DAO object to push the changes to.
     */
    public synchronized void pushChanges(String componentId, DaoObject daoObject) {
        assertHasLock(componentId);

        MCTLock lock = getLock(componentId);
        if (lock.isUnlockingInProgress()) {
            return;
        }

        MCTTransaction transaction = lock.getTransaction();

        if (transaction == null) {
            throw new MCTLockException("Transaction does not exist for component [" + componentId + "]");
        }

        transaction.pushChanges(daoObject);

        fireProcessDirtyState(lock.getLockedManifestations());
    }

    /**
     * Checks for pending lock transaction.
     * @param componentId - The component id.
     * @return boolean - Component has pending changes or not.
     */
    public synchronized boolean hasPendingTransaction(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null || !lock.isLocked())
            return false;

        MCTTransaction transaction = lock.getTransaction();

        return transaction.hasPendingChanges();
    }

    private MCTLock getLock(String componentId) {
        MCTLock lock = localLocks.get(componentId);
        if (lock == null) {
            lock = sharedLocks.get(componentId);
        }
        return lock;
    }

    private void assertHasLock(String componentId) {
        MCTLock lock = getLock(componentId);
        if (lock == null) {
            throw new MCTLockException("Cannot find the lock to unlock for component [" + componentId + "]");
        }
    }

    /**
     * Aborts the locking.
     * @param componentId - The component id.
     * @param viewManifestations - The view manifestation argument.
     */     
    @Override
    public void abort(String componentId, Set<View> viewManifestations) {
        assertHasLock(componentId);
        MCTLock lock = getLock(componentId);
        lock.abort(viewManifestations);
        fireExitLockedState(viewManifestations);
    }

    private final void fireEnterLockedState(Set<View> viewManifestations) {
        for (View viewManifestation : viewManifestations) {
            viewManifestation.enterLockedState();
        }
    }

    private final void fireExitLockedState(Set<View> viewManifestations) {
        for (View viewManifestation : viewManifestations) {
            viewManifestation.exitLockedState();
        }
    }

    private final void fireProcessDirtyState(Set<View> viewManifestations) {
        for (View viewManifestation : viewManifestations) {
            viewManifestation.processDirtyState();
        }
    }

    /**
     * Get all the locked manifestations.
     * @return map of <String, Set<View>>
     */
    @Override
    public synchronized Map<String, Set<View>> getAllLockedManifestations() {
        Map<String, Set<View>> allLockedManifestations = new LinkedHashMap<String, Set<View>>();

        for (Entry<String, MCTLock> entry: sharedLocks.entrySet()) {
            MCTLock lock = entry.getValue();
            if (lock.isLocked()) {
                Set<View> lockedManifestations = lock.getLockedManifestations();
                if (!lockedManifestations.contains(View.WILD_CARD_VIEW_MANIFESTATION))
                    allLockedManifestations.put(entry.getKey(), lockedManifestations);
            }
        }

        return allLockedManifestations;
    }
    

    /**
     * Gets all the shared locks.
     * @return collection of MCTLock
     */
    @Override
    public Collection<MCTLock> getAllSharedLocks() {
        return sharedLocks.values();
    }

    /**
     * Gets all local locks.
     * @return map of <String, MCTLock>
     */
    public Map<String, MCTLock> getLocalLocks() {
        return localLocks;
    }
    
    /**
     * Gets the lock owner.
     * @param componentId - The component id.
     * @return current lock owner.
     */
    public String getLockOwner(String componentId) {
        return getCurrentLockHolder(componentId);
    }
    
    /**
     * DB Global locking strategy.
     *
     */
    public class DbGlobalLockStrategy implements GlobalLockStrategy {
        
        /**
         * Locks the component by id.
         * @param componentId - The component id.
         * @return boolean flag to check.
         */
        public boolean lock(String componentId) {
            return obtainExclusiveLock(componentId);
        }
        
        /**
         * Unlock.
         * @param componentId - The component id.
         */
        public void unlock(String componentId) {
            removeExclusiveLock(componentId);
        }
    }
    
    
    /**
     * Attempt to add a row to the database with the current session and component id. If there is already
     * a row with an exclusive lock, this will fail.
     * @return true if exclusive lock was obtained false otherwise
     */
    private boolean obtainExclusiveLock(String componentId) {
        if (!HibernateUtil.canOpenAnotherConnection()) {
            SwingUtilities.invokeLater(new Runnable() {
                
                @Override
                public void run() {
                    Frame frame = null;
                    for (Frame f: Frame.getFrames()) {
                        if (f.isActive() || f.isFocused()) {
                            frame = f;
                            break;
                        }
                    }
                    ResourceBundle bundle = ResourceBundle.getBundle("LockAction");
                    OptionBox.showMessageDialog(frame, bundle.getString("ExceedMaxPoolSizeMessage"));
                }
            });
            return false;
        }
        String temporarySessionId = IdGenerator.nextComponentId();
        if (!getExclusiveLock(componentId, temporarySessionId, true)) {
            cleanExpiredLocks(componentId);
            return getExclusiveLock(componentId, temporarySessionId, false);
        }
        return true;
        
    }
    
    private boolean getExclusiveLock(String componentId, String temporarySessionId, boolean firstTime) {
        try {
            insertForExclusiveLock(temporarySessionId, componentId);
            return true;
        } catch (Exception e) {
            if (!firstTime) {
                LOGGER.warn("exception while getting exclusive lock", e);
            }
            return false;
        }
    }
    
    private String getCurrentLockHolder(String componentId) {
        String temporarySessionId = IdGenerator.nextComponentId();
        PersistenceBroker pb = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        String delete = "select user_id from edit_locks where component_id = ?";
        String[] parameters = new String[] {
                componentId
        };
        String lockHolder = "";
        try {
            List<Object> lockHolders = pb.queryNativeSQL(temporarySessionId, delete, parameters);
            if (!lockHolders.isEmpty()) {
                lockHolder = lockHolders.get(0).toString();
            }
        } catch (Exception e) {
            LOGGER.error("unable to get lock holders", e);
        }
        return lockHolder;
    }
    
    private void cleanExpiredLocks(String componentId) {
        String temporarySessionId = IdGenerator.nextComponentId();
        PersistenceBroker pb = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        String delete = "delete from edit_locks where component_id = ? and lease_start < (NOW() - INTERVAL ? MINUTE)";
        String[] parameters = new String[] {
                componentId,
                Integer.toString(LOCK_TIMEOUT_IN_MINUTES)
        };
        try {
            pb.executeNativeSQL(temporarySessionId, delete, parameters);
        } catch (Exception e) {
            LOGGER.error("unable to remove expired locks", e);
        }
    }
    
    private void removeExclusiveLock(String componentId) {
        String temporarySessionId = IdGenerator.nextComponentId();
        PersistenceBroker pb = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        String delete = "DELETE FROM edit_locks WHERE component_id = ? AND session = ?";
        String[] parameters = new String[] {
                componentId,
                this.sessionId
        };
        try {
            pb.executeNativeSQL(temporarySessionId, delete, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void insertForExclusiveLock(String sessionId, String componentId) {
        String userId = GlobalContext.getGlobalContext().getUser().getUserId();
        PersistenceBroker pb = GlobalContext.getGlobalContext().getSynchronousPersistenceBroker();
        String insert = "INSERT INTO edit_locks (component_id, session, user_id, exclusive) VALUES (?, ?, ?, ?)";
        String[] parameters = new String[] {
                componentId,
                this.sessionId,
                userId,
                EXCLUSIVE_MARKER
        };
        pb.executeNativeSQL(sessionId, insert, parameters);
    }
    

}
