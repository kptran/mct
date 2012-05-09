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
 * MCTTransaction.java Dec 1, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.transaction;

import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.persistmgr.SynchronousPersistenceBroker;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;

import java.util.LinkedList;
import java.util.List;

public class MCTTransaction {
    private final static PersistenceBroker syncPersistenceBroker = SynchronousPersistenceBroker.getSynchronousPersistenceBroker();

    private final List<DaoObject> pendingChanges = new LinkedList<DaoObject>();
    private final String transactionId;
    private boolean isCommited = false;
    private boolean autoCommit;

    public MCTTransaction() {
        this.autoCommit = false;
        this.transactionId = null;
    }

    public MCTTransaction(boolean autoCommit, String transactionId) {
        this.autoCommit = autoCommit;
        this.transactionId = transactionId;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void pushChanges(DaoObject daoObject) {
        if (!pendingChanges.contains(daoObject)) {
            daoObject.lockDaoObject();
            pendingChanges.add(daoObject);
        } 
        if (autoCommit) {
            synchronousCommit();
        }
    }

    public void synchronousCommit() {
        if (!pendingChanges.isEmpty()) {
            if (syncPersistenceBroker != null) {
                syncPersistenceBroker.saveBatch(transactionId, pendingChanges, new PersistenceCompletedCallbackHandler() {
                    @Override
                    public void saveCompleted() {
                        for (DaoObject daoObject : pendingChanges) {
                            daoObject.unlockDaoObject();
                        }
                    }
                });
            }
            pendingChanges.clear();
        }
        isCommited = true;
    }

    public boolean hasPendingChanges() {
        return !this.pendingChanges.isEmpty();
    }

    public boolean isCommited() {
        return this.isCommited;
    }
    
    public String getTransactionId() {
        return this.transactionId;
    }

    public void clearPendingChanges() {
        for (DaoObject daoObject : pendingChanges)
            daoObject.unlockDaoObject();

        pendingChanges.clear();
    }
}
