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
import static org.testng.Assert.assertNotNull;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.defaults.view.NodeViewManifestation;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.util.TestSetupUtilities;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.GraphicsEnvironment;
import java.util.Collections;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDirectoryArea extends PersistenceUnitTest {

    MCTDirectoryArea dirArea;
    private JTree dirTree;
    private MCTHousing housing;
    private MCTMutableTreeNode treeNode;
    static final String NODE_NAME = "Root Node";

    @Mock private MCTUser user;
    @Mock private AbstractComponent rootComponent;
    @Mock private AbstractComponent leafComponent;
    
    @Override
    protected User getUser() {
        MockitoAnnotations.initMocks(this);
        
        when(user.getUserId()).thenReturn("asi");
        when(user.getDisciplineId()).thenReturn("CATO");
        return user;
    }
    
    @Override
    protected void postSetup() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        housing = TestSetupUtilities.setUpActiveHousing();
        MockitoAnnotations.initMocks(this);

        // Create a model role, a dummy component, and a tree with a single root node.
        String id = NODE_NAME;
        MyComponent c = new MyComponent(id);
        c.getCapability(ComponentInitializer.class).initialize();

        GlobalComponentRegistry.registerComponent(c);
        mockComponent(leafComponent, null);
        mockComponent(rootComponent,leafComponent);
        treeNode = createMCTMutableTreeStructure(rootComponent);
        dirArea = new MCTDirectoryArea(rootComponent, new ViewInfo(MCTDirectoryArea.class,"", ViewType.NAVIGATOR));
    }

    /**
     * Taken from BookmarkTest
     */
    private static MCTMutableTreeNode createMCTMutableTreeStructure(AbstractComponent root) {
        JTree tree = new JTree();
        MCTMutableTreeNode rootNode = new MCTMutableTreeNode(root.getViewInfos(ViewType.NODE).iterator().next().createView(root), tree);

        // Populate the tree
        for (AbstractComponent childComponent : root.getComponents()) {
            Set<ViewInfo> setViewInfos = childComponent.getViewInfos(ViewType.NODE);
            ViewInfo viewRole = setViewInfos.iterator().next();
            MCTMutableTreeNode childNode = new MCTMutableTreeNode(viewRole.createView(childComponent), tree);
            rootNode.add(childNode);
            if (childComponent.hasComponentReferences()) {
                childNode.setProxy(true);
                MCTMutableTreeNode grandChildNode = new MCTMutableTreeNode(View.NULL_VIEW_MANIFESTATION);
                childNode.add(grandChildNode);
            }
        }
        return rootNode;
    }
    
    private void mockComponent(AbstractComponent mockedComponent, AbstractComponent child) {
        View mockView = Mockito.mock(View.class);
        Mockito.when(mockView.getManifestedComponent()).thenReturn(mockedComponent);
        Mockito.when(mockedComponent.getViewInfos(ViewType.NODE)).thenReturn(Collections.singleton(new ViewInfo(NodeViewManifestation.class, "", ViewType.NODE)));
        Mockito.when(mockedComponent.getComponents()).thenReturn(child == null ? Collections.<AbstractComponent>emptyList() : Collections.<AbstractComponent>singletonList(child));
        Mockito.when(mockedComponent.isLeaf()).thenReturn(child == null);
        
    }
    
    @Test
    public void testConstructor() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        assertNotNull(dirArea);
    }

    @Test
    public void testGetDirectoryTree() {
        dirTree = dirArea.getDirectoryTree();
        assertNotNull(dirTree);
    }
    
    @Test(dependsOnMethods = {"testGetDirectoryTree"})
    public void testGetSelectedNode() {
        dirArea.setSelectedNode(treeNode);
        DefaultMutableTreeNode[] selectedNodes = dirArea.getSelectedNodes();
        assertEquals(selectedNodes[0], treeNode/*model.getRoot()*/);
        assertNotNull(selectedNodes);
        assertEquals(selectedNodes.length, 1);
    }
    
    @Test
    public void testDirectoryPanel() {
        assertNotNull(dirArea.getDirectoryPanel());
    }
    
    @Test(dependsOnMethods = {"testGetDirectoryTree"})
    public void testClearSelection() {
        dirArea.clearCurrentSelections();
        assertEquals(dirTree.getSelectionCount(), 0);
        
        // Test selection API
        DefaultMutableTreeNode[] selectedNodes = dirArea.getSelectedNodes();
        assertNotNull(selectedNodes);
        assertEquals(selectedNodes.length, 0);
    }
    
    @Test
    public void testGetSelectedDirectoryNode() {
        MCTMutableTreeNode selectedDirectoryNode = dirArea.getSelectedDirectoryNode();
        assertNotNull(selectedDirectoryNode);
    }
    
    @Test
    public void testGetHousedViewManifestation() {
        // ensure the housed view manfiestation sets the client property to the Jtree for the directory area
        Assert.assertSame(dirArea.getHousedViewManifestation().getClientProperty(MCTMutableTreeNode.PARENT_CLIENT_PROPERTY_NAME),dirArea.getActiveTree());
    }

    @Test
    public void testTabRoot() {
        assertNotNull(dirArea.getTabRoot());
    }
}
