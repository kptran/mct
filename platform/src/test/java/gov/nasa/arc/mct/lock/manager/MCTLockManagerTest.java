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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.User;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MCTLockManagerTest {

    @Mock User user;
    
    private DbLockManager manager;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        
        when(user.getUserId()).thenReturn("user1");

        manager = new DbLockManager();
        GlobalContext.getGlobalContext().switchUser(user, null);
    }
    
    @Test
    public void testNewLock() {
        manager.newLock("MCTLockManagerTest1");
        assertTrue(manager.getLocalLocks().containsKey("MCTLockManagerTest1"));
        int nLocks = manager.getLocalLocks().size();
        
        // Asking for a lock twice should reuse the same lock.
        manager.newLock("MCTLockManagerTest1");
        assertTrue(manager.getLocalLocks().containsKey("MCTLockManagerTest1"));
        assertEquals(manager.getLocalLocks().size(), nLocks);
    }
    
    @Test
    public void testLockLocalManifestation() {
        AbstractComponent comp = mock(AbstractComponent.class);
        View view = mock(View.class);
        when(view.getManifestedComponent()).thenReturn(comp);
        when(comp.isShared()).thenReturn(false);
        
        Assert.assertEquals(manager.getOwnerUserId("MCTLockManagerTest2"), "No user");
        manager.lock("MCTLockManagerTest2", view);
        Assert.assertEquals(manager.getOwnerUserId("MCTLockManagerTest2"), "user1");
        assertTrue(manager.getLocalLocks().containsKey("MCTLockManagerTest2"));
        int nLocks = manager.getLocalLocks().size();
        
        // Asking for a lock twice should reuse the same lock.
        manager.lock("MCTLockManagerTest2", view);
        assertTrue(manager.getLocalLocks().containsKey("MCTLockManagerTest2"));
        assertEquals(manager.getLocalLocks().size(), nLocks);
    }
    
    @Test
    public void testConcurrentModificationOnSharedLocks() {
        final String prefix = "testConcurrentModificationOnSharedLocks";
        final int numberOfLocks = 1000;
        for (int i = 0; i < numberOfLocks; i++) {
            String componentId = prefix + Integer.toString(i);
            AbstractComponent component = Mockito.mock(AbstractComponent.class);
            Mockito.when(component.getId()).thenReturn(componentId);
            GlobalComponentRegistry.registerComponent(component);
            manager.newLock(componentId);
            manager.lock(componentId);
        }
        
        Thread modifyingThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                for (int i = 0; i < numberOfLocks; i++) {
                    manager.shareLock(prefix + Integer.toString(i));
                }
            }
        });
        modifyingThread.start();
        
        for (int i = 0; i < numberOfLocks; i++) {
            manager.getAllLockedManifestations();
        }
        
        try {
            modifyingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
