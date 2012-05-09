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
package gov.nasa.arc.mct.publish;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.MCTLockManagerFactory;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.User;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PublishLockTest extends PersistenceUnitTest {
    @Override
    protected User getUser() {
        return new MockUser("CATO", "asi");
    }
    
    @Override
    protected void postSetup() {
        GlobalContext.getGlobalContext().setLockManager(MCTLockManagerFactory.getLockManager());
        GlobalComponentRegistry.clearRegistry();
    }
    
    @Test
    public void publishTest() {
        AbstractComponent testComp = new AbstractComponent(){
        };
        testComp.setId("Test");
        testComp.getCapability(ComponentInitializer.class).initialize();

        MCTLockManagerFactory.getLockManager().lock(testComp.getId(), View.WILD_CARD_VIEW_MANIFESTATION);
        Assert.assertTrue(MCTLockManagerFactory.getLockManager().isLocked(testComp.getId()));
        
        MCTLockManagerFactory.getLockManager().shareLock(testComp.getId());
        Assert.assertFalse(MCTLockManagerFactory.getLockManager().isLocked(testComp.getId()));
    }
    
    private static class MockUser implements User {
        private final String disciplineId;
        private final String userId;
        
        public MockUser(String disciplineId, String userId) {
            this.disciplineId = disciplineId;
            this.userId = userId;
        }
        
        @Override
        public String getDisciplineId() {
            return this.disciplineId;
        }
        
        @Override
        public String getUserId() {
            return this.userId;
        }

        @Override
        public User getValidUser(String userID) {
            if (userId.equals(userID)) {
                return this;
            }
            return null;
        }
        
        @Override
        public boolean hasRole(String role) {
            return false;
        }
    }
}
