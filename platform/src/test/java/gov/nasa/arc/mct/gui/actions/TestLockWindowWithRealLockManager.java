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

import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.defaults.view.MCTHousingViewManifestation;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.gui.housing.MCTStandardHousing;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.lock.manager.MCTLockManagerFactory;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestLockWindowWithRealLockManager {
    private MCTHousing housing;
    private AbstractComponent component;
    
    private final PlatformAccess access = new PlatformAccess();
    private final Platform platform = new MockPlatform();
    
    @BeforeClass
    public void setup() {
        if (GraphicsEnvironment.isHeadless()) return;
        access.setPlatform(platform);
        
        // User setup
        MCTUser user = new MCTUser();
        user.setUserId("tester");
        GlobalContext.getGlobalContext().switchUser(user, null);
        
        // MCT component and view setup 
        component = new MockComponent();
        component.setShared(false);
        component.getCapability(ComponentInitializer.class).initialize();
        component.setOwner("tester");

        
        housing = new MockHousing(new MCTHousingViewManifestation(component, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
        
        // Lock manager setup
        GlobalContext.getGlobalContext().setLockManager(MCTLockManagerFactory.getLockManager());
    }
    
    @AfterClass
    public void cleanup() {
        if (GraphicsEnvironment.isHeadless()) return;

        // Cleanup
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        globalContext.setLockManager(null);
        globalContext.switchUser(null, null);
        GlobalComponentRegistry.clearRegistry();
        
        access.setPlatform(platform);
    }
    
    
    
    @Test
    public void testCanHandle_NodeUnlocked() {
       if (GraphicsEnvironment.isHeadless()) return;
        
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        Set<View> lockedManifestations = new HashSet<View>();
        View mockedView = Mockito.mock(View.class);
        Mockito.when(mockedView.getManifestedComponent()).thenReturn(component);
        lockedManifestations.add(mockedView);
        lockManager.lock(component.getId(), lockedManifestations);        
        
        LockWindowAction action = new LockWindowAction();

        // Check node lock status
        ActionContextImpl context = new ActionContextImpl();
        context.setTargetHousing(housing);
        context.addTargetViewComponent(new MCTHousingViewManifestation(component, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
        Assert.assertTrue(action.canHandle(context));
        Assert.assertTrue((Boolean) action.getValue(Action.SELECTED_KEY));
    }
    
     
     /**
      * Apply a LockWindowAction where the target view component of the action is not 
      * the root component of the target housing and that component is locked. The root
      * component of the target housing is locked and we want to make sure that it does not
      * become unlocked.   
      */
     @Test (dependsOnMethods = { "testCanHandle_NodeUnlocked" })
     public void testCanHandle_targetViewComponentDifferentFromHousingRootComponent() {
       if (GraphicsEnvironment.isHeadless()) return;
       
       LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        
       // Window to be unlocked (in UI terms) 
       AbstractComponent window = new MockComponent();
       window.setShared(false);
       window.getCapability(ComponentInitializer.class).initialize();
       window.setOwner("tester");

    
       MCTHousing windowHousing = new MockHousing(new MCTHousingViewManifestation(window, new ViewInfo(MCTHousingViewManifestation.class,"", ViewType.LAYOUT)));
       Assert.assertEquals(windowHousing.getRootComponent(), window);
       lockManager.lock(window.getId(), windowHousing.getHousedViewManifestation()); 
       

      // Create the item to be placed in the window
       AbstractComponent itemComponent = new MockComponent();
       itemComponent.setShared(false);
       itemComponent.getCapability(ComponentInitializer.class).initialize();

       View itemNodeViewManifestation = Mockito.mock(View.class);
       Mockito.when(itemNodeViewManifestation.getManifestedComponent()).thenReturn(itemComponent);
    
      
       // Create action where target view component is not the root component of the target housing.       
       LockWindowAction action = new LockWindowAction();

       // Check node lock status when housing is selected.
       ActionContextImpl context = new ActionContextImpl();
       context.setTargetHousing(windowHousing);
       context.addTargetViewComponent(itemNodeViewManifestation);
       
       Assert.assertTrue(action.canHandle(context));
       Assert.assertFalse((Boolean) action.getValue(Action.SELECTED_KEY));
    }

    @SuppressWarnings("serial")
    private class MockHousing extends MCTStandardHousing {
        
        public MockHousing(View housingView) {
            super("Mock", 0, 0, 0, housingView);
        }
    }
    
}
