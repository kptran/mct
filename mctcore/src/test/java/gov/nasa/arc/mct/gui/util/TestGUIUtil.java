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
package gov.nasa.arc.mct.gui.util;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.util.IdGenerator;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestGUIUtil {

    private MockComponent componentA;
    private MockComponent componentB;
    private MockComponent componentC;
    
    @Mock
    private Platform mockPlatform;
    @Mock
    private PolicyManager mockPolicyManager;
    
    /*
     * Component A
     *   |
     *   +-- Component B
     *         |
     *         +-- Component C
     */
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);

        (new PlatformAccess()).setPlatform(mockPlatform);
        Mockito.when(mockPlatform.getPolicyManager()).thenReturn(mockPolicyManager);
        ExecutionResult er = new ExecutionResult(null, true, null);
        Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.any(PolicyContext.class))).thenReturn(er);
        
        componentA = new MockComponent(IdGenerator.nextComponentId(), false);
        componentA.getCapability(ComponentInitializer.class).initialize();

        componentB = new MockComponent(IdGenerator.nextComponentId(),false);
        componentB.getCapability(ComponentInitializer.class).initialize();

        componentC = new MockComponent(IdGenerator.nextComponentId(), false);
        componentC.getCapability(ComponentInitializer.class).initialize();
        
        componentA.addDelegateComponent(componentB);
        componentB.addDelegateComponent(componentC);
    }
    
    @Test
    public void testLazyLoading() {
        // Clone subtree of component A
        MCTMutableTreeNode clonedTreeNode = GUIUtil.cloneTreeNode(componentA, new ViewInfo(NodeViewTest.class, "", ViewType.NODE));
        Assert.assertNotNull(clonedTreeNode);
        Assert.assertEquals(clonedTreeNode.getChildCount(), 1);
        Assert.assertTrue(clonedTreeNode.isProxy());
    }
    
    @Test
    public void testLockedManifestationsInTree() {
        JPanel panel = new JPanel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        panel.add(tree);
        
        AbstractComponent component = Mockito.mock(AbstractComponent.class);
        Mockito.when(component.getId()).thenReturn("test");
        View manifestation = Mockito.mock(View.class);        
        Mockito.when(manifestation.getManifestedComponent()).thenReturn(component);
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(manifestation);
        root.add(child);        
        
        LockManager lockManager = Mockito.mock(LockManager.class);
        Map<String, Set<View>> map = Collections.singletonMap(component.getId(), Collections.singleton(manifestation));
        Mockito.when(lockManager.getAllLockedManifestations()).thenReturn(map);
        GlobalContext.getGlobalContext().setLockManager(lockManager);
        
        // Test case: client property is NOT set
        Map<String, Set<View>> lockedManifestations = GUIUtil.getLockedManifestations(Collections.singletonList(panel));
        Assert.assertNotNull(lockedManifestations);
        Assert.assertEquals(lockedManifestations.size(), 0);
        
        // Test case: client property is set
        manifestation.putClientProperty(MCTMutableTreeNode.PARENT_CLIENT_PROPERTY_NAME, tree);
        lockedManifestations = GUIUtil.getLockedManifestations(Collections.singletonList(panel));
        Assert.assertNotNull(lockedManifestations);
        Assert.assertEquals(lockedManifestations.size(), 1);
        
        GlobalContext.getGlobalContext().setLockManager(null);
    }
    
    @Test
    public void testLockedManifestationProvider() {
        
        AbstractComponent mockHousedComponent = Mockito.mock(AbstractComponent.class);
        View mockHousedManifestation = Mockito.mock(View.class);
        Mockito.when(mockHousedManifestation.getManifestedComponent()).thenReturn(mockHousedComponent);
        HousingContainer  housing = new HousingContainer(mockHousedManifestation);
        
        LockManager lockManager = Mockito.mock(LockManager.class);
        Map<String, Set<View>> map = Collections.singletonMap(mockHousedComponent.getId(), Collections.singleton(mockHousedManifestation));
        Mockito.when(lockManager.getAllLockedManifestations()).thenReturn(map);
        GlobalContext.getGlobalContext().setLockManager(lockManager);
        
        Map<String, Set<View>> lockedManifestations = GUIUtil.getLockedManifestations(Collections.singletonList(housing));
        Assert.assertNotNull(lockedManifestations);
        Assert.assertEquals(lockedManifestations.size(), 1);
        
        GlobalContext.getGlobalContext().setLockManager(null);
    }
    
    public final static class NodeViewTest extends View {
        private static final long serialVersionUID = -4755584459025450842L;

        public NodeViewTest(AbstractComponent ac, ViewInfo vi) {
            super(ac,vi);
        }
    }
    
    @SuppressWarnings("serial")
    private final static class HousingContainer extends View {
        
        private final View housedManifestation;
        
        public HousingContainer(final View housedManifestation) {
            this.housedManifestation = housedManifestation;
        }
        
        @Override
        public View getHousedViewManifestation() {
            return housedManifestation;
        }
    }

}
