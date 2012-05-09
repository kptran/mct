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
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.RoleAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Action;

@SuppressWarnings("serial")
public class LockWindowAction extends ContextAwareAction {

    private boolean isLocked;
    // Note: locking here is based on MCT's terminology.
    // The lock manager uses the standard computer science definition.

    private ActionContextImpl actionContext;

    public LockWindowAction() {
        super("Locked Window");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MCTHousing targetHousing = actionContext.getTargetHousing();
        AbstractComponent rootComponent = targetHousing.getRootComponent();
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        Set<View> targetManifestations = new LinkedHashSet<View>();
        
        Collection<ViewProvider> manifestProviders = targetHousing.getContentArea().getHousedManifestationProviders();
        for (ViewProvider manifestProvider: manifestProviders) {
            targetManifestations.add(manifestProvider.getHousedViewManifestation());
        }
        
        View inspectionArea = targetHousing.getInspectionArea();
        if (inspectionArea != null) {
            targetManifestations.add(inspectionArea);     
        }
        
        targetManifestations.add(targetHousing.getHousedViewManifestation());
        if (targetHousing.getDirectoryArea() != null) {
            targetManifestations.add(targetHousing.getDirectoryArea().getHousedViewManifestation());
        }
        if (isLocked) {
            // Acquire lock for the component.
            if (!lockManager.lock(rootComponent.getId(), targetManifestations)) {
                ResourceBundle bundle = ResourceBundle.getBundle("LockAction");
                String lockedBy = lockManager.getLockOwner(rootComponent.getId());
                if (!lockedBy.isEmpty())
                    OptionBox.showMessageDialog(null, MessageFormat.format(bundle.getString("LockedComponentMessage"), lockedBy));
            } else {
                isLocked = !isLocked;
                // update component from database to handle the case where the update has not yet been pulled from database
                PlatformAccess.getPlatform().getPersistenceService().updateComponentFromDatabase(rootComponent);
            }
            
        } else {
            // Release lock for the component.
            if (!lockManager.hasPendingTransaction(rootComponent.getId())) {
                isLocked = !isLocked;
                lockManager.unlock(rootComponent.getId(), targetManifestations);
            }
            else if (MCTDialogManager.showCommitConfirmationDialog(targetManifestations))
                isLocked = !isLocked;
        }
        putValue(Action.SELECTED_KEY, isLocked);
    }

    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context;

        MCTHousing targetHousing = actionContext.getTargetHousing();
        if (targetHousing == null)
            return false;

        AbstractComponent rootComponent = targetHousing.getRootComponent();
        if (rootComponent == null)
            return false;
        
        User u = GlobalContext.getGlobalContext().getUser();
        if (!rootComponent.getOwner().equals(u.getUserId()) &&
            !RoleAccess.hasRole(u, rootComponent.getOwner())) {
            return false;
        }

        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        isLocked = !lockManager.isManifestationLocked(rootComponent.getId(), targetHousing.getHousedViewManifestation());
        putValue(Action.SELECTED_KEY, isLocked);

        return true;
    }

    @Override
    public boolean isEnabled() {
        MCTHousing targetHousing = actionContext.getTargetHousing();
        AbstractComponent rootComponent = targetHousing.getRootComponent();

        PolicyContext policyContext = new PolicyContext();
        policyContext.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), rootComponent);
        policyContext.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        policyContext.setProperty(PolicyContext.PropertyName.VIEW_MANIFESTATION_PROVIDER.getName(), targetHousing.getHousedViewManifestation());
        String lockingKey = PolicyInfo.CategoryType.LOCKING_ENABLE_POLICY_CATEGORY.getKey();
        ExecutionResult result = PolicyManagerImpl.getInstance().execute(lockingKey, policyContext);
        return result.getStatus();
    }

}
