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
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.tree.TreePath;

/**
 * GUI utility implementation. 
 *
 */
                
public class GUIUtil {

    
    /**
     * Clones the node from the view role's model role by lazily loading only
     * its first level children.
     * 
     * @param component - The AbstractComponent to be cloned.
     * @param viewInfo - root view info be cloned.
     * @return cloned subtree
     */
    public static final MCTMutableTreeNode cloneTreeNode(AbstractComponent component, ViewInfo viewInfo) {
        return cloneFirstLevelViewRoles(component,viewInfo);
    }
    
    /**
     * Clones the 1st level children.
     * @param component - The AbstractComponent to be cloned.
     * @param viewInfo
     * @return cloned subtree rooted from view role's model role.
     */
    private static final MCTMutableTreeNode cloneFirstLevelViewRoles(AbstractComponent component, ViewInfo info) {
        MCTMutableTreeNode cloned = new MCTMutableTreeNode(info.createView(component));
        if (!component.isLeaf()) {
            cloned.setProxy(true);
            MCTMutableTreeNode proxyChildNode = new MCTMutableTreeNode(View.NULL_VIEW_MANIFESTATION);
            cloned.add(proxyChildNode);
        }

        return cloned;
    }
    
    /**
     * Gets the locked manifestation based on the array of tree paths.
     * 
     * @param treePaths - array of TreePaths.
     * @return associatedLockedManifestations - map of associated lock manifestations.
     */
    public static Map<String, Set<View>> getLockedManifestations(TreePath[] treePaths) {
        Map<String, Set<View>> associatedLockedManifestations = new LinkedHashMap<String, Set<View>>();
        Map<String, Set<View>> allLockedManifestations = GlobalContext.getGlobalContext()
                .getLockManager().getAllLockedManifestations();
        if (!allLockedManifestations.isEmpty()) {
            for (TreePath treePath : treePaths) {
                MCTMutableTreeNode treeNode = (MCTMutableTreeNode) treePath.getLastPathComponent();
                addAssociatedLockedManifestations(treeNode, associatedLockedManifestations, allLockedManifestations);
                AbstractComponent component = ((View) treeNode.getUserObject())
                        .getManifestedComponent();
                Set<View> lockedManifestationsById = allLockedManifestations.get(component.getId());
                if (lockedManifestationsById != null && !lockedManifestationsById.isEmpty())
                    associatedLockedManifestations.put(component.getId(), lockedManifestationsById);
            }
        }
        return associatedLockedManifestations;
    }

    /**
     * Returns the set of <code>MCTViewManifestation</code>s that are
     * hierarchically linked to the <code>Containers</code>s.
     * 
     * @param containers
     *            a <code>Collection</code> of <code>Container</code>s
     * @return a <code>Map</code> of <code>MCTViewManifestation</code>s, where
     *         the map maps from a component id to the corresponding set of
     *         MCTViewManifestations that are currently locked.
     */
    public static Map<String, Set<View>> getLockedManifestations(
            Collection<? extends Container> containers) {
        Map<String, Set<View>> associatedLockedManifestations = new LinkedHashMap<String, Set<View>>();
        Map<String, Set<View>> allLockedManifestations = GlobalContext.getGlobalContext()
                .getLockManager().getAllLockedManifestations();
        if (!allLockedManifestations.isEmpty()) {
            for (Entry<String, Set<View>> entry : allLockedManifestations.entrySet()) {
                for (View manifestation : entry.getValue()) {
                    for (Container container : containers) {
                        if (container instanceof ViewProvider) {
                            // E.g., the inspector area is not an MCTViewManifestation,
                            // but it is a provider that provides an MCTViewManifestation.                           
                            View housedViewManifestation = ((ViewProvider) container)
                                    .getHousedViewManifestation();
                            Set<View> lockedViews = allLockedManifestations.get(housedViewManifestation.getManifestedComponent().getComponentId());
                            if (lockedViews != null && lockedViews.contains(housedViewManifestation))
                                associatedLockedManifestations.put(housedViewManifestation.getManifestedComponent().getComponentId(), Collections.singleton(housedViewManifestation));                            
                        }
                        // Check if manifestation exists in this container
                        if (isConnectedAtSwingLevel(container, manifestation)) {
                            associatedLockedManifestations.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }
        return associatedLockedManifestations;
    }

    private static void addAssociatedLockedManifestations(MCTMutableTreeNode treeNode,
            Map<String, Set<View>> associatedLockedManifestations,
            Map<String, Set<View>> allLockedManifestations) {
        View manifestation = (View) treeNode.getUserObject();
        for (Entry<String, Set<View>> entry : allLockedManifestations.entrySet()) {
            if (entry.getValue().contains(manifestation))
                associatedLockedManifestations.put(entry.getKey(), entry.getValue());
            break;
        }
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            addAssociatedLockedManifestations((MCTMutableTreeNode) treeNode.getChildAt(i),
                    associatedLockedManifestations, allLockedManifestations);
        }
    }

    private static boolean isConnectedAtSwingLevel(Component parent, Component widget) {
        if (widget == null)
            return false;

        if (parent == widget)
            return true;

        Container widgetParent = widget.getParent();

        if (widgetParent == null && widget instanceof JComponent)
            widgetParent = (Container) ((JComponent) widget)
                    .getClientProperty(MCTMutableTreeNode.PARENT_CLIENT_PROPERTY_NAME);

        return isConnectedAtSwingLevel(parent, widgetParent);
    }

}
