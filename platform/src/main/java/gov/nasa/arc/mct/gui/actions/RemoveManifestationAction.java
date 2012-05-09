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
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.dialogs.MCTDialogManager;
import gov.nasa.arc.mct.gui.housing.MCTDirectoryArea;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.gui.util.GUIUtil;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * This action removes a manifestation in the directory area. Note that 
 * removing a manifestation under "created by me" is not allowed, but
 * it not part of the composition policy category.
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public class RemoveManifestationAction extends ContextAwareAction {

    private static String TEXT = "Remove Manifestation";
    private TreePath[] selectedTreePaths;
    private ActionContextImpl actionContext;
    
    public RemoveManifestationAction() {
        super(TEXT);
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context;
        MCTHousing activeHousing = actionContext.getTargetHousing();
        if (activeHousing == null)
            return false;

        Collection<View> selection = 
            activeHousing.getSelectionProvider().getSelectedManifestations();
        
        if (selection.isEmpty())
            return false;
        
        ViewInfo vi = selection.iterator().next().getInfo();
        
        if (selection.isEmpty() || 
                !(vi != null && vi.getViewType() == ViewType.NODE)){
            return false;
        }

        if (!(activeHousing.getDirectoryArea() instanceof MCTDirectoryArea)) {
            MCTLogger.getLogger(RemoveManifestationAction.class).error("Action only works with MCTDirectoryArea");
            return false;
        }
            
        MCTDirectoryArea directory = MCTDirectoryArea.class.cast(activeHousing.getDirectoryArea());
        MCTMutableTreeNode firstSelectedNode = directory.getSelectedDirectoryNode();
        if (firstSelectedNode == null)
            return false;
        
        JTree tree = firstSelectedNode.getParentTree();
        selectedTreePaths = tree.getSelectionPaths();
        return selectedTreePaths != null && selectedTreePaths.length >  0;
    }
    
    @Override
    public boolean isEnabled() {
    
        for (TreePath path : selectedTreePaths) {
            if (!isRemovable(path))
                return false;
        }
        return true;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Map<String, Set<View>> lockedManifestations = GUIUtil.getLockedManifestations(selectedTreePaths);
        if (!lockedManifestations.isEmpty()) {
            MCTMutableTreeNode firstSelectedNode = (MCTMutableTreeNode) selectedTreePaths[0].getLastPathComponent();
            if (!MCTDialogManager.showUnlockedConfirmationDialog((View) firstSelectedNode.getUserObject(), lockedManifestations, "Remove", "row and/or associated inspector"))
                return;
        }

        for (TreePath path : selectedTreePaths) {
            MCTMutableTreeNode selectedNode = (MCTMutableTreeNode) path.getLastPathComponent();            
            MCTMutableTreeNode parentNode = (MCTMutableTreeNode) selectedNode.getParent();
            
            AbstractComponent parentComponent = ((View) parentNode.getUserObject()).getManifestedComponent();
            AbstractComponent selectedComponent = ((View) selectedNode.getUserObject()).getManifestedComponent();

            // Remove from component model
            parentComponent.removeDelegateComponent(selectedComponent);
        }   
    }
    
    private boolean isRemovable(TreePath path) {
        MCTMutableTreeNode lastPathComponent = (MCTMutableTreeNode) path.getLastPathComponent();
        MCTMutableTreeNode parentNode = (MCTMutableTreeNode) lastPathComponent.getParent();
        if (parentNode == null)
            return false;
        
        AbstractComponent parentComponent = ((View) parentNode.getUserObject()).getManifestedComponent();
        AbstractComponent selectedComponent = View.class.cast(lastPathComponent.getUserObject()).getManifestedComponent();

        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), parentComponent);
        context.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        context.setProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), Collections.singleton(selectedComponent));
        context.setProperty(PolicyContext.PropertyName.VIEW_MANIFESTATION_PROVIDER.getName(), parentNode.getUserObject());
        
        String canRemoveManifestationKey = PolicyInfo.CategoryType.CAN_REMOVE_MANIFESTATION_CATEGORY.getKey();
        boolean canRemoveManifestation = PolicyManagerImpl.getInstance().execute(canRemoveManifestationKey, context).getStatus();
        
        if (canRemoveManifestation) {
            String compositionKey = PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey();
            return PolicyManagerImpl.getInstance().execute(compositionKey, context).getStatus();
        }
        return canRemoveManifestation;
    }
}
