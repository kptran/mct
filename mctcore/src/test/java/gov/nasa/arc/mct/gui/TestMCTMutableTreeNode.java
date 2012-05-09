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
package gov.nasa.arc.mct.gui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.components.AbstractComponent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestMCTMutableTreeNode {

    class ComponentFilterable extends AbstractComponent {
        public ComponentFilterable(String id) {
            setId(id);
        }
    }
    @Mock View mockViewManif;
    @Mock View mockViewManif2;
    @Mock ComponentFilterable mockComponent;
    @Mock ComponentFilterable mockComponent2;

    private JTree myJTree;
    private MCTMutableTreeNode myTreeNode;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mockViewManif.getManifestedComponent()).thenReturn(mockComponent);
        
        when(mockViewManif2.getManifestedComponent()).thenReturn(mockComponent2);

        myJTree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
        myTreeNode = new MCTMutableTreeNode(mockViewManif, myJTree);
    }

    @Test
    public void testConstructors() {
        assertFalse(myTreeNode.isProxy());
        assertTrue(myTreeNode.getAllowsChildren());
        
        MCTMutableTreeNode node2 = new MCTMutableTreeNode();
        assertFalse(node2.isProxy());
        assertTrue(node2.getAllowsChildren());

        MCTMutableTreeNode node3 = new MCTMutableTreeNode(mockViewManif);
        assertFalse(node3.isProxy());
        assertTrue(node3.getAllowsChildren());

        MCTMutableTreeNode node4 = new MCTMutableTreeNode(mockViewManif, true);
        assertFalse(node4.isProxy());
        assertTrue(node4.getAllowsChildren());

        MCTMutableTreeNode node5 = new MCTMutableTreeNode(mockViewManif, false);
        assertFalse(node5.isProxy());
        assertFalse(node5.getAllowsChildren());

        MCTMutableTreeNode node6 = new MCTMutableTreeNode(mockViewManif, myJTree, true);
        assertFalse(node6.isProxy());
        assertTrue(node6.getAllowsChildren());

        MCTMutableTreeNode node7 = new MCTMutableTreeNode(mockViewManif, myJTree, false);
        assertFalse(node7.isProxy());
        assertFalse(node7.getAllowsChildren());
    }

    @Test
    public void testShortMethods() {
        myTreeNode.setProxy(true);
        assertTrue(myTreeNode.isProxy());
        assertTrue(myTreeNode.isVisible());
    }

    @Test
    public void testRemoveAllChildren() {
        myTreeNode.removeAllChildren();
        assertEquals(myTreeNode.getChildCount(), 0);
    }

    @Test
    public void testGetSetParentTree() {
        myTreeNode.setParentTree(null);
        assertNull(myTreeNode.getParentTree());
        myTreeNode.setParentTree(new JTree());
        assertNotNull(myTreeNode.getParentTree());
    }

    @Test
    public void testAddRemoveChild() {
        MCTMutableTreeNode child = new MCTMutableTreeNode(mockViewManif2);
        int prev = myTreeNode.getChildCount();
        
        myTreeNode.addChild(-1, child);
        assertEquals(myTreeNode.getChildCount(), prev + 1);
        assertSame(myTreeNode.getChildAt(prev), child);

        myTreeNode.removeChild(child);
        assertEquals(myTreeNode.getChildCount(), prev);

        myTreeNode.addChild(0, child);
        assertEquals(myTreeNode.getChildCount(), prev + 1);
        assertSame(myTreeNode.getChildAt(0), child);

        myTreeNode.removeChild(child);
        assertEquals(myTreeNode.getChildCount(), prev);
    }

    @Test
    public void testGetTreePath() {
        assertNotNull(myTreeNode.getTreePath());
    }

    @Test
    public void testGetComparable() {
        assertNotNull(myTreeNode.getUserObject());
    }

    @Test 
    public void testAddChildrenAllowable() {
        MCTMutableTreeNode aChildNode= new MCTMutableTreeNode(mockViewManif, true);
        
        boolean exceptionThrown = false;
        try {
            MCTMutableTreeNode childrenProhibitedNode = new MCTMutableTreeNode(mockViewManif, false);
           
            childrenProhibitedNode.add(aChildNode);
            
        } catch (IllegalStateException e) {
           exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        
        exceptionThrown = false;
        
        try {
            MCTMutableTreeNode childrenAllowedNode = new MCTMutableTreeNode(mockViewManif, true);
           
            childrenAllowedNode.add(aChildNode);
            
        } catch (IllegalStateException e) {
           exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
              
    }
    
    @Test
    public void testAddChildToEmptyNode() {
        MCTMutableTreeNode child1 = createChild(1);
        
        assertEquals(myTreeNode.getChildCount(), 0);
        myTreeNode.addChild(0, child1);
        assertEquals(myTreeNode.getChildCount(), 1);
        assertSame(myTreeNode.getChildAt(0), child1);
    }
    
    
    @Test(dataProvider="addChildFirstTimeTests")
    public void testAddChildFirstTime(int numExisting, int index) {
        for (int i=0; i<numExisting; ++i) {
            MCTMutableTreeNode child = createChild(i);
            myTreeNode.addChild(0, child);
        }
        
        assertEquals(myTreeNode.getChildCount(), numExisting);
        MCTMutableTreeNode newChild = createChild(numExisting);
        myTreeNode.addChild(index, newChild);
        assertEquals(myTreeNode.getChildCount(), numExisting+1);
        assertSame(myTreeNode.getChildAt(index), newChild);
    }
    
    @DataProvider(name="addChildFirstTimeTests")
    public Object[][] getAddChildFirstTimeTests() {
        return new Object[][] {
                new Object[] { 0, 0 },
                new Object[] { 1, 0 },
                new Object[] { 1, 1 },
                new Object[] { 5, 0 },
                new Object[] { 5, 1 },
                new Object[] { 5, 4 },
                new Object[] { 5, 5 },
        };
    }
    
    @Test(dataProvider="addChildWhenExistingTests")
    public void testAddChildWhenExisting(int numExisting, int indexToAddTwice, int insertIndex) {
        for (int i=0; i<numExisting; ++i) {
            MCTMutableTreeNode child = createChild(i);
            myTreeNode.addChild(0, child);
        }
        
        assertEquals(myTreeNode.getChildCount(), numExisting);
        MCTMutableTreeNode newChild = (MCTMutableTreeNode) myTreeNode.getChildAt(indexToAddTwice);
        myTreeNode.addChild(insertIndex, newChild);
        assertEquals(myTreeNode.getChildCount(), numExisting);
        assertSame(myTreeNode.getChildAt(indexToAddTwice<insertIndex ? insertIndex-1 : insertIndex), newChild);
    }
    
    @DataProvider(name="addChildWhenExistingTests")
    public Object[][] getTestAddChildWhenExistingTests() {
        return new Object[][] {
                new Object[] { 1, 0, 0 },
                new Object[] { 1, 0, 1 },
                new Object[] { 5, 0, 0 },
                new Object[] { 5, 0, 1 },
                new Object[] { 5, 4, 0 },
                new Object[] { 5, 4, 1 },
                new Object[] { 5, 4, 4 },
                new Object[] { 5, 4, 5 },
        };
    }
    private MCTMutableTreeNode createChild(int id) {
        View mockManifestation = mock(View.class);
        AbstractComponent comp = mock(AbstractComponent.class);
        
        when(mockManifestation.getManifestedComponent()).thenReturn(comp);
        when(comp.getId()).thenReturn(String.valueOf(id));
        
        return new MCTMutableTreeNode(mockManifestation);
    }
}
