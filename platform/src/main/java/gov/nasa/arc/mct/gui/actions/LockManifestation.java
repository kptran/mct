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
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.gui.dialogs.MCTDialogManager;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * This action is used to lock and unlock a manifestation. This action
 * is manifested in the UI as a checkbox menu item.
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public class LockManifestation extends ContextAwareAction {

    private boolean isLocked; 
    // Note: locking here is based on MCT's terminology.
    // The lock manager uses the standard computer science definition.

    private ActionContextImpl actionContext;

    public LockManifestation() {
        super("Locked Manifestation");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractComponent targetComponent = actionContext.getTargetComponent();

        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        Set<JComponent> allManifestationProviders = actionContext.getAllTargetViewComponent();
        Set<View> targetManifestations = new LinkedHashSet<View>(allManifestationProviders.size());
        for (JComponent jcomp : allManifestationProviders) {
            if (jcomp instanceof ViewProvider)
                targetManifestations.add(((ViewProvider) jcomp).getHousedViewManifestation());
            else
                targetManifestations.add((View) jcomp);
        }
        if (isLocked) {
            // Acquire lock for the component.
            if (!lockManager.lock(targetComponent.getId(), targetManifestations)) {
                ResourceBundle bundle = ResourceBundle.getBundle("LockAction");
                String lockedBy = lockManager.getLockOwner(targetComponent.getId());
                if (!lockedBy.isEmpty())
                    OptionBox.showMessageDialog(null, MessageFormat.format(bundle.getString("LockedComponentMessage"), lockedBy));
            } else {
                isLocked = !isLocked;
                // update component from database to handle the case where the update has not yet been pulled from database
                PlatformAccess.getPlatform().getPersistenceService().updateComponentFromDatabase(targetComponent);
            }
        } else {
            // Release lock for the component.
            if (!lockManager.hasPendingTransaction(targetComponent.getId())) {
                isLocked = !isLocked;
                lockManager.unlock(targetComponent.getId(), targetManifestations);
            }
            else if (MCTDialogManager.showCommitConfirmationDialog(targetManifestations))
                isLocked = !isLocked;
        }
        putValue(Action.SELECTED_KEY, isLocked);
    }

    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context; 

        AbstractComponent targetComponent = actionContext.getTargetComponent();

        Collection<View> selectedManifestations = actionContext.getSelectedManifestations();
        if (selectedManifestations.size() > 0) {
            ViewProvider viewManifestationProvider = (ViewProvider) actionContext.getTargetViewComponent();
            
            // Check if the view is embedded
            View parentView = (View) SwingUtilities.getAncestorOfClass(View.class, viewManifestationProvider.getHousedViewManifestation());
            if (parentView != null && parentView.isContentOwner())
                return false;
            
            LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
            isLocked = !(lockManager.isManifestationLocked(targetComponent.getId(), viewManifestationProvider.getHousedViewManifestation()) || lockManager.isLockedForAllUsers(targetComponent.getId()));
            putValue(Action.SELECTED_KEY, isLocked);
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (actionContext.getSelectedManifestations().size() != 1)
            return false;
        
        AbstractComponent targetComponent = actionContext.getTargetComponent();
        if (targetComponent == null)
            return false;

        if (targetComponent.equals(actionContext.getTargetHousing().getRootComponent()))
            return false;
        
        if (!targetComponent.isShared() && !targetComponent.isVersionedComponent())
            return false;
        
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        if (lockManager.isManifestationSetLocked(targetComponent.getId(), Collections.singleton(actionContext.getSelectedManifestations().iterator().next()))) {
            return true;
        }
        
        ViewProvider viewManifestationProvider = (ViewProvider) actionContext.getTargetViewComponent();
        PolicyContext policyContext = new PolicyContext();
        policyContext.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), targetComponent);
        policyContext.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        policyContext.setProperty(PolicyContext.PropertyName.VIEW_MANIFESTATION_PROVIDER.getName(), viewManifestationProvider);
        String lockingKey = PolicyInfo.CategoryType.LOCKING_ENABLE_POLICY_CATEGORY.getKey();
        ExecutionResult result = PolicyManagerImpl.getInstance().execute(lockingKey, policyContext);
        return result.getStatus();
    }
}
