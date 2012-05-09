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
/**
 * MCTMutableTreeNode.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * A DefaultMutableTreeNode which contains a flag to indicate if a node is just
 * a proxy to its children or actually contains real children.
 * 
 */
@SuppressWarnings("serial")
public class MCTMutableTreeNode extends DefaultMutableTreeNode {
    /**
     * A property name to indicate the parent of a tree node.
     */
    public static final String PARENT_CLIENT_PROPERTY_NAME = "parent";
    
    private JTree parentTree;
    private boolean isProxy = false;
    private boolean isVisible = true;

    /**
     * Creates a new tree node without attaching it to a
     * parent tree.
     */
    public MCTMutableTreeNode() {
        super();
    }

    /**
     * Creates a new tree node that represents the given
     * user object.
     * 
     * @param userObject a Swing component that this tree node represents
     */
    public MCTMutableTreeNode(JComponent userObject) {
        super(userObject);
        
        // Add default monitored gui, which is this tree node.
        if (userObject instanceof View) {
            ((View) userObject).addMonitoredGUI(this);
        }
    }

    /**
     * Creates a new tree node within a parent tree, that
     * represents a specified user object.
     * 
     * @param userObject a Swing component that this tree node represents
     * @param parentTree the parent tree for this tree node
     */
     public MCTMutableTreeNode(JComponent userObject, JTree parentTree) {
        this(userObject);
        this.parentTree = parentTree;
     }

    /**
     * Creates a new tree node that represents the given
     * user object. Further, the caller may specify whether
     * this tree node allows children.
     * 
     * @param userObject a Swing component that this tree node represents
     * @param allowsChildren true, if the tree node should allow child nodes
     */
    public MCTMutableTreeNode(JComponent userObject, boolean allowsChildren) {
        this(userObject);
        this.setAllowsChildren(allowsChildren);
    }
    
    /**
     * Creates a new tree node within a parent tree, that
     * represents a specified user object.
     * 
     * @param userObject a Swing component that this tree node represents
     * @param parentTree the parent tree for this tree node
     * @param allowsChildren true, if the tree node should allow child nodes
     */
    public MCTMutableTreeNode(JComponent userObject, JTree parentTree, boolean allowsChildren) {
        this(userObject, parentTree);
        this.setAllowsChildren(allowsChildren);
    }
    
    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        View viewManifestation = View.class.cast(DefaultMutableTreeNode.class.cast(newChild).getUserObject());
        if (viewManifestation != View.NULL_VIEW_MANIFESTATION)
            viewManifestation.putClientProperty(PARENT_CLIENT_PROPERTY_NAME, getParentTree());
    }
    
    /**
     * Sets whether this tree node is a proxy for another component.
     *  
     * @param isProxy true, if this is a proxy node
     */
    public void setProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    /**
     * Tests whether this is a proxy node.
     * 
     * @return true, if this is a proxy node
     */
    public boolean isProxy() {
        return this.isProxy;
    }

    /**
     * Tests whether this tree node should be visible within the tree.
     * 
     * @return true, if this node should be shown
     */
    public boolean isVisible() {
        return this.isVisible;
    }
    
    @Override
    public TreeNode getChildAt(int index) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        
        return (TreeNode) children.get(index);
    }

    @Override
    public int getChildCount() {
        if (!isVisible) {
            return 0;
        }
        if (children == null) {
            return 0;
        }
        
        return children.size();
    }
    
    //Calls our overridden remove method
    @Override
    public void removeAllChildren() {
        for (int i = super.getChildCount() - 1; i >= 0; i--) {
            remove(i);
        }
    }

    /**
     * Gets the parent tree in which this tree node is located.
     * 
     * @return the parent tree
     */
    public JTree getParentTree() {
        // When a directory area is created in a new window, only the root node
        // is associated with the parent tree. Here we use lazy assignment to
        // to associate the parent tree to the child nodes.
        if (this.parentTree == null) {
            this.parentTree = ((MCTMutableTreeNode) this.getRoot()).parentTree;
        }
        return this.parentTree;
    }

    /**
     * Sets the parent tree in which the tree node is located.
     * 
     * @param parentTree the parent tree
     */
    public void setParentTree(JTree parentTree) {
        this.parentTree = parentTree;
    }

    /**
     * Remove a child from the tree, if it exists, and return the position
     * at which it was found.
     * 
     * @param childNode the child node to delete
     * @return the index where the child was found, or -1 if not present
     */
    private int removeChildIfExist(MCTMutableTreeNode childNode) {
        if (isProxy()) { return -1; }
        
        int position = -1;
        
        AbstractComponent targetComponent = ((View) childNode.getUserObject()).getManifestedComponent();

        for (int i = 0; i < getChildCount(); i++) {
            MCTMutableTreeNode node = (MCTMutableTreeNode) getChildAt(i);
            AbstractComponent childComponent = ((View) node.getUserObject()).getManifestedComponent();
            if (childComponent.getId().equals(targetComponent.getId())) {
                position = i;
                removeChild(node);
                break;
            }
        }
        
        View viewManifestation = View.class.cast(childNode.getUserObject());
        viewManifestation.putClientProperty(PARENT_CLIENT_PROPERTY_NAME, null);
        
        return position;
    }

    /**
     * Adds a new child of this node to the data model.
     * 
     * @param childIndex the index at which to add the new child, or -1 to add at the end
     * @param childNode the new child node
     */
    public void addChild(int childIndex, MCTMutableTreeNode childNode) {
        if (childIndex < 0) {
            childIndex = getChildCount();
        }

        int oldIndex = removeChildIfExist(childNode);
        
        // Adjust the position at which to insert the child, if it
        // already existed at a position to the left of where we're
        // going to insert.
        if (0<=oldIndex && oldIndex<childIndex) {
            --childIndex;
        }
        
        childNode.setParentTree(getParentTree());
        DefaultTreeModel treeModel = (DefaultTreeModel) getParentTree().getModel();
        treeModel.insertNodeInto(childNode, this, childIndex);

        View viewManifestation = View.class.cast(childNode.getUserObject());
        viewManifestation.putClientProperty(PARENT_CLIENT_PROPERTY_NAME, getParentTree());
    }
    
    /**
     * Refresh the tree display because of a change in a child node.
     * 
     * @param childNode the child node that changed
     */
    public void refresh(MCTMutableTreeNode childNode) {
        childNode.setParentTree(getParentTree());
        DefaultTreeModel treeModel = (DefaultTreeModel) parentTree.getModel();
        TreePath path = getParentTree().getSelectionPath();
        treeModel.insertNodeInto(childNode, this, getChildCount());
        if (path != null) {
            // if the parent node is in the selection path then the refresh can cause the
            // selected node to be removed from the tree, so reset the selection to the 
            if (Arrays.asList(path).contains(this.getParent())) {
                getParentTree().setSelectionPath(new TreePath(treeModel.getPathToRoot(this.getParent())));
            } else {
                getParentTree().setSelectionPath(path);
            }
        } else {
            path = getPath(this);
        }
        getParentTree().collapsePath(path);
        getParentTree().expandPath(path);
    }
    
    private TreePath getPath(MCTMutableTreeNode node) {
        List<MCTMutableTreeNode> list = new LinkedList<MCTMutableTreeNode>();

        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = (MCTMutableTreeNode)node.getParent();
        }
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }

    
    /**
     * Refresh the tree display of this node.
     */
    public void refresh() {
        DefaultTreeModel treeModel = (DefaultTreeModel) parentTree.getModel();
        treeModel.nodeChanged(this);
    }

    /**
     * Remove a child node from the data model.
     * 
     * @param childNode the child node to remove
     */
    public void removeChild(MCTMutableTreeNode childNode) {
        DefaultTreeModel treeModel = (DefaultTreeModel) childNode.getParentTree().getModel();
        treeModel.removeNodeFromParent(childNode);
        View viewManifestation = View.class.cast(childNode.getUserObject());
        viewManifestation.putClientProperty(PARENT_CLIENT_PROPERTY_NAME, null);
    }

    /**
     * Gets the path to this node in the parent tree.
     * 
     * @return the path to this node
     */
    public TreePath getTreePath() {
        return new TreePath(getPath());
    }
    
}
