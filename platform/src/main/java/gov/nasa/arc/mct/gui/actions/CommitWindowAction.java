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
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.lock.manager.LockManager;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class CommitWindowAction extends ContextAwareAction {
        
    private ActionContextImpl actionContext;

    public CommitWindowAction() {
        super("Commit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        MCTHousing targetHousing = actionContext.getTargetHousing();
        AbstractComponent rootComponent = targetHousing.getRootComponent();
        Set<View> targetManifestations = new LinkedHashSet<View>();

        Collection<ViewProvider> manifestProviders = targetHousing.getContentArea().getHousedManifestationProviders();
        for (ViewProvider manifestProvider: manifestProviders) {
            targetManifestations.add(manifestProvider.getHousedViewManifestation());
        }
        targetManifestations.add(targetHousing.getHousedViewManifestation());
        
        View inspectionArea = targetHousing.getInspectionArea();
        if (inspectionArea != null) {
            targetManifestations.add(inspectionArea); 
        }

        if (targetHousing.getDirectoryArea() != null) {
            targetManifestations.add(targetHousing.getDirectoryArea().getHousedViewManifestation());
        }
        lockManager.unlock(rootComponent.getId(), targetManifestations);
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
        
        return true;
    }

    @Override
    public boolean isEnabled() {
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        AbstractComponent rootComponent = actionContext.getTargetHousing().getRootComponent();
        return !actionContext.getWindowManifestation().isLocked()
                && lockManager.hasPendingTransaction(rootComponent.getId());
    }

}
