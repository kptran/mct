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
package gov.nasa.arc.mct.defaults.view;

import static org.mockito.Mockito.when;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;
import gov.nasa.arc.mct.roles.events.ReloadEvent;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.lang.reflect.Field;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestDefaultNodeView {

    private View nodeViewManifestation;
    @Mock AbstractComponent mockComponent;
    View mockViewManifestation;
    private JTree tree;
    private MCTMutableTreeNode rootNode;
    private DefaultTreeModel model;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockViewManifestation = new View() {
            private static final long serialVersionUID = -3939874904884082433L;
        };

        when(mockComponent.getDisplayName()).thenReturn("XXX");
        when(mockComponent.getExtendedDisplayName()).thenReturn("XXX");
        // Set up JTree for updateMonitoredGUI() calls
        rootNode = new MCTMutableTreeNode(new View() {
            private static final long serialVersionUID = 3318467817844246495L;
            
        });
        model = new DefaultTreeModel(rootNode);
        tree = new JTree(model);
        rootNode.setParentTree(tree);
        // Set up tree's UI containers
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.add(tree);
    }

    @Test
    public void testCreateManifestation() {
        nodeViewManifestation = new NodeViewManifestation(mockComponent, new ViewInfo(NodeViewManifestation.class,"", ViewType.NODE));
        Assert.assertNotNull(nodeViewManifestation);
    }

    @Test(dependsOnMethods="testCreateManifestation")
    public void testAddMonitoredGUI() {
        MCTMutableTreeNode treeNode = new MCTMutableTreeNode();
        treeNode.setAllowsChildren(false);
        nodeViewManifestation.addMonitoredGUI(treeNode);
        MCTMutableTreeNode retrievedNode = ((NodeViewManifestation) nodeViewManifestation).getMCTMutableTreeNode();
        Assert.assertFalse(retrievedNode.getAllowsChildren());

        nodeViewManifestation.addMonitoredGUI(Mockito.mock(MCTMutableTreeNode.class));
        Assert.assertNotNull(((NodeViewManifestation) nodeViewManifestation).getMCTMutableTreeNode());
    }

    /*
     *  The component's name, because it is mocked, is always XXX.
     *  This will outdate the View's label.  Then we refresh the View and we verify
     *  that the View label has been updated to the component's name.
     */
    @Test
    public void testUpdateMonitoredGUI_None() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        nodeViewManifestation = new NodeViewManifestation(mockComponent, new ViewInfo(NodeViewManifestation.class,"", ViewType.NODE));

        Field field = nodeViewManifestation.getClass().getDeclaredField("label");
        field.setAccessible(true);
        JLabel label = (JLabel) field.get(nodeViewManifestation);// get label field of View under test
        String fixedName = "outdatedView";
        label.setText(fixedName);// outdate the label of the view under test
        Assert.assertFalse(label.getText().equals(mockComponent.getExtendedDisplayName()));
       
        MCTMutableTreeNode mockTreeNode = Mockito.mock(MCTMutableTreeNode.class);
        JTree mockTree = Mockito.mock(JTree.class);
        DefaultTreeModel mockModel = Mockito.mock(DefaultTreeModel.class);
        
        Mockito.when(mockTreeNode.getParentTree()).thenReturn(mockTree);
        Mockito.when(mockTree.getModel()).thenReturn(mockModel);
        Mockito.when(mockComponent.getComponents()).thenReturn(Collections.<AbstractComponent>emptyList());
        PlatformAccess access = new PlatformAccess();
        access.setPlatform(new MockPlatform());
        
        nodeViewManifestation.addMonitoredGUI(mockTreeNode);
        nodeViewManifestation.updateMonitoredGUI();
        Assert.assertTrue(label.getText().equals(mockComponent.getExtendedDisplayName()));
    }

    @Test(enabled=true, dependsOnMethods="testCreateManifestation")
    public void testUpdateMonitoredGUI_PropertyChange() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = nodeViewManifestation.getClass().getDeclaredField("label");
        field.setAccessible(true);
        JLabel label = (JLabel) field.get(nodeViewManifestation);

        PropertyChangeEvent event = new PropertyChangeEvent(mockComponent);
        String fixedString = "YYY";
        event.setProperty(PropertyChangeEvent.DISPLAY_NAME, fixedString);
        Mockito.when(mockComponent.getExtendedDisplayName()).thenReturn(fixedString);

        MCTMutableTreeNode mockTreeNode = Mockito.mock(MCTMutableTreeNode.class);
        JTree mockTree = Mockito.mock(JTree.class);
        Mockito.when(mockTreeNode.getParentTree()).thenReturn(mockTree);
        DefaultTreeModel mockModel = Mockito.mock(DefaultTreeModel.class);
        Mockito.when(mockTree.getModel()).thenReturn(mockModel);
        nodeViewManifestation.addMonitoredGUI(mockTreeNode);
        nodeViewManifestation.updateMonitoredGUI(event);
        Assert.assertTrue(label.getText().equals(fixedString));
    }

    @Test(enabled=true, dependsOnMethods="testCreateManifestation")
    public void testUpdateMonitoredGUI_Reload() {
        PlatformAccess access = new PlatformAccess();
        access.setPlatform(new MockPlatform());
        
        ReloadEvent event = new ReloadEvent(mockComponent);
        Assert.assertFalse(rootNode.isProxy());
        nodeViewManifestation.addMonitoredGUI(rootNode);
        nodeViewManifestation.updateMonitoredGUI(event);
        Assert.assertTrue(rootNode.isProxy());
    }

    @Test(dependsOnMethods="testCreateManifestation")
    public void testLockObservable() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        nodeViewManifestation.enterLockedState();
        nodeViewManifestation.processDirtyState();

        Field field = nodeViewManifestation.getClass().getDeclaredField("label");
        field.setAccessible(true);
        JLabel label = (JLabel) field.get(nodeViewManifestation);
        Assert.assertTrue(label.getText().charAt(0) == '*');
        nodeViewManifestation.exitLockedState();
        Assert.assertFalse(label.getText().charAt(0) == '*');
    }

}
