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
package gov.nasa.arc.mct.persistmgr;

import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implements a block of code that should be executed within a
 * Hibernate transaction. The transaction executor is obtained
 * from a static instance field. That field may be overridden
 * by unit tests to replace the transaction mechanism with
 * mock objects, for example.
 * 
 * @author mrose
 *
 */
public abstract class PersistenceTransaction {
    
    /** A singleton instance of a transaction executor. */
    protected static final TransactionExecutor executor = new TransactionExecutor();
    
    /**
     * Perform the transaction code in the given session. This method
     * should be overridden with the actual work to perform in the
     * transaction.
     * 
     * @param session the session to use
     */
    public abstract void perform(Session session);

    /**
     * Run the transaction.
     */
    public void run(String sessionId, boolean hasOpenSession) {
        run(sessionId, null, hasOpenSession, false);
    }
    
    public void run() {
        run(null, null, false, false);
    }
    
    /**
     * Run the transaction, with a callback to a completion handler.
     * The handler is called at the end, whether or not the
     * transaction succeeded.
     * 
     * @param handler the handler to call
     */
    public void run(String sessionId, PersistenceCompletedCallbackHandler handler, boolean hasOpenSession, boolean alwaysCommit) {
        executor.run(sessionId, this, handler, hasOpenSession, alwaysCommit);
    }
    
    public void run(PersistenceCompletedCallbackHandler handler) {
        executor.run(null, this, handler, false, false);
    }
    
    /**
     * Implements the pattern for executing the transaction.
     * 
     * @author mrose
     *
     */
    protected static class TransactionExecutor {
       
        /** The logger to use for logging errors. */
        private static MCTLogger log = MCTLogger.getLogger(TransactionExecutor.class);

        /**
         * Run the transaction and call a completion handler. The completion
         * handler may be null to indicate that no completion handler should
         * be called.
         * 
         * @param xa the class containing the work to perform in the transaction
         * @param handler the completion handler to call
         */
        public void run(String sessionId, PersistenceTransaction xa, PersistenceCompletedCallbackHandler handler, boolean hasOpenSession, boolean alwaysCommit) {
            boolean sessionAlreadyOpen = hasOpenSession || HibernateUtil.hasCurrentSession(sessionId);
            Session session = HibernateUtil.getCurrentSession(sessionId);
            Transaction transaction = session.beginTransaction();
            boolean success = false;

            // Perform the work, and commit the transaction if we opened the
            // session, and if there are dirty objects in the session.
            try {
                xa.perform(session);
                if (!sessionAlreadyOpen && (session.isDirty() || alwaysCommit)) {
                    transaction.commit();
                }
                success = true;
            } finally {
                // If we opened the session, clean up: 1) Roll back, if
                // we didn't succeed in doing the work and committing;
                // 2) and close the session.
                //
                // Then, call the completion handler.
                
                if (!sessionAlreadyOpen) {
                    try {
                        if (!success) {
                            transaction.rollback();
                        }
                    } catch (Exception t) {
                        log.error("Error rolling back database transaction", t);
                    }
                    try {
                        HibernateUtil.closeSession(sessionId);
                    } catch (Exception t) {
                        log.error("Error closing Hibernate session", t);
                    }
                }

                if (handler != null) {
                    try {
                        handler.saveCompleted();
                    } catch (Exception t) {
                        // ignore
                    }
                }
            }
        }
    }
    
}
