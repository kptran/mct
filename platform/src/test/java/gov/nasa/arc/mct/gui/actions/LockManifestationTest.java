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
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.lock.manager.MCTLockManagerFactory;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;

import javax.swing.Action;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LockManifestationTest {
    @Mock
    private AbstractComponent mockShareGroupComponent;
    
    @Mock
    private AbstractComponent mockLockForAllUserComponent;
    
    @Mock
    private View mockManifestation;
    
    private LockManifestation lockManifestation;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockShareGroupComponent.getId()).thenReturn("shareComp");
        Mockito.when(mockShareGroupComponent.isShared()).thenReturn(true);
        Mockito.when(mockLockForAllUserComponent.getId()).thenReturn("lockForAllUserComp");
        Mockito.when(mockLockForAllUserComponent.isShared()).thenReturn(true);
        Mockito.when(mockManifestation.getHousedViewManifestation()).thenReturn(mockManifestation);
        
        GlobalContext.getGlobalContext().setLockManager(MCTLockManagerFactory.getLockManager());
        
        GlobalComponentRegistry.registerComponent(mockShareGroupComponent);
        GlobalComponentRegistry.registerComponent(mockLockForAllUserComponent);
        
        lockManifestation = new LockManifestation();
    }
    
    @AfterMethod
    public void tearDown() {
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        globalContext.setLockManager(null);
        globalContext.switchUser(null, null);
        GlobalComponentRegistry.clearRegistry();
    }
    
    @Test
    public void testCanHandle() {
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        lockManager.lockForAllUser(mockLockForAllUserComponent.getId());
        
        ActionContextImpl actionContext = new ActionContextImpl();
        actionContext.setTargetComponent(mockShareGroupComponent);
        actionContext.addTargetViewComponent(mockManifestation);
        
        lockManifestation.canHandle(actionContext);
        Assert.assertTrue(Boolean.class.cast(lockManifestation.getValue(Action.SELECTED_KEY)).booleanValue());
        
        actionContext = new ActionContextImpl();
        actionContext.setTargetComponent(mockLockForAllUserComponent);
        actionContext.addTargetViewComponent(mockManifestation);
        
        lockManifestation.canHandle(actionContext);
        Assert.assertFalse(Boolean.class.cast(lockManifestation.getValue(Action.SELECTED_KEY)).booleanValue());
    }
    
    @Test
    public void testCanHandleNoSelection() {
        ActionContextImpl actionContext = new ActionContextImpl();        
        MCTHousing housing = Mockito.mock(MCTHousing.class);
        Mockito.when(housing.getRootComponent()).thenReturn(mockShareGroupComponent);
        actionContext.setTargetHousing(housing);
        actionContext.setTargetComponent(mockShareGroupComponent);
        
        Assert.assertTrue(lockManifestation.canHandle(actionContext));
        Assert.assertFalse(lockManifestation.isEnabled());
    }

}
