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
package gov.nasa.arc.mct.gui.housing;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.gui.util.TestSetupUtilities;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.services.internal.component.User;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;

public class TestHousing extends PersistenceUnitTest {
    
    @Mock private MCTUser user;
    private MCTHousing housing;
    
    @Override
    protected User getUser() {
        MockitoAnnotations.initMocks(this);
        
        when(user.getUserId()).thenReturn("asi");
        when(user.getDisciplineId()).thenReturn("CATO");
        return user;
    }
    
    @Override
    protected void postSetup() {
        housing = TestSetupUtilities.setUpActiveHousing();
    }
    
    @Test
    public void testShowCommitDialog() {
        // the housing is in it's own window, now let's see if it has any pending transactions...
        // grab the lockManager...
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
       
        if (housing.getRootComponent() != null)
            if (housing.getRootComponent().getId() != null && lockManager != null)
                assertEquals(lockManager.lock(housing.getRootComponent().getId()), true);
       
        // success - since no pending transaction - so no comit dialog box comes up.
        //  Now, just do some cleanup...
       
        housing = null;  // force a GC on it...
    }
//    @Test
//    public void testShowCommitDialog() {
//        MCTHousing housing = TestSetupUtilities.setUpActiveHousing();
// 
//        // the housing is in it's own window, now let's see if it has any pending transactions...
//        // grab the lockManager...
//        MCTLockManager lockManager = (MCTLockManager) GlobalContext.getGlobalContext().getLockManager();
//        assertEquals(lockManager.lock(housing.getRootComponent().getId()), true);
//        
//        // success - since no pending transaction - so no comit dialog box comes up.
//        //  Now, just do some cleanup...
//        
//        housing = null;  // force a GC on it...
//    }
}
