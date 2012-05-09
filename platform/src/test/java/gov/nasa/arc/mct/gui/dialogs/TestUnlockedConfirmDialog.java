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
package gov.nasa.arc.mct.gui.dialogs;

import gov.nasa.arc.mct.component.MockDaoObject;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.defaults.view.NodeViewManifestation;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.lock.manager.MCTLockManagerFactory;
import gov.nasa.arc.mct.persistence.MockPersistenceBroker;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.CoreComponentRegistry;
import gov.nasa.arc.mct.util.IdGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestUnlockedConfirmDialog {
    private MockComponent component;
    @Mock Platform mockPlatform;
    @Mock CoreComponentRegistry mockComponentRegistry;
    @Mock PolicyManager mockPolicyManager;
    @Mock PersistenceService mockPersistenceService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        (new PlatformAccess()).setPlatform(mockPlatform);
        // User setup
        MCTUser user = new MCTUser();
        user.setUserId("tester");
        GlobalContext.getGlobalContext().switchUser(user, null);
        
        // MCT component and view setup 
        component = new MockComponent(IdGenerator.nextComponentId(), true);
        component.getCapability(ComponentInitializer.class).initialize();
        MockDaoStrategy daoStrategy = new MockDaoStrategy(component, new MockDaoObject());
        component.setDaoStrategy(daoStrategy);
        //GlobalComponentRegistry.registerViewRole(MockComponent.class, MockNodeViewRole.class);
        
        // Lock manager setup
        GlobalContext.getGlobalContext().setLockManager(MCTLockManagerFactory.getLockManager());
        
        // Persistence manager setup
        GlobalContext.getGlobalContext().setSynchronousPersistenceManager(MockPersistenceBroker.getInstance());
        
        Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.any(PolicyContext.class))).thenReturn(new ExecutionResult(null, true, ""));
        Mockito.when(mockPlatform.getComponentRegistry()).thenReturn(mockComponentRegistry);
        Mockito.when(mockPlatform.getPolicyManager()).thenReturn(mockPolicyManager);
        Mockito.when(mockPlatform.getPersistenceService()).thenReturn(mockPersistenceService);
    }
    
    @AfterMethod
    public void tearDown() {
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        globalContext.setLockManager(null);
        globalContext.switchUser(null, null);
        GlobalComponentRegistry.clearRegistry();
        (new PlatformAccess()).releasePlatform();
    }
    
    @Test
    public void testNoPendingChanges() {
        // Get manifestation
        View targetManifestation = new NodeViewManifestation(component,new ViewInfo(NodeViewManifestation.class, "", ViewType.NODE));
        String id = targetManifestation.getManifestedComponent().getId();
        Set<View> lockedManifestations = new HashSet<View>();
        lockedManifestations.add(targetManifestation);

        // Unlock the manifestation
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        lockManager.lock(id, lockedManifestations);
        
        // Invoke the dialog
        Map<String, Set<View>> lockMap = new HashMap<String, Set<View>>();
        lockMap.put(id, lockedManifestations);
        Assert.assertTrue(MCTDialogManager.showUnlockedConfirmationDialog(targetManifestation, lockMap, "apply", "widget"));
        
        // Verify that the component is now unlocked (i.e., lock released).
        Assert.assertTrue(!lockManager.isLocked(id));
    }
    
}
