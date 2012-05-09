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
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.lock.manager.LockManager;

import java.awt.event.ActionEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * This action allows users to commit the changes made on a manifestation to 
 * the persistence subsystem and resume its locked state back to <em>locked</em>.
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public class CommitAction extends ContextAwareAction {

    private AbstractComponent targetComponent;
    private Set<View> targetManifestations;
    private ActionContextImpl actionContext;

    public CommitAction() {
        super("Commit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        lockManager.unlock(targetComponent.getId(), targetManifestations);
    }

    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context;
        
        targetComponent = actionContext.getTargetComponent();
        
        Set<JComponent> allManifestationProviders = actionContext.getAllTargetViewComponent();
        targetManifestations = new LinkedHashSet<View>(allManifestationProviders.size());
        for (JComponent jcomp : allManifestationProviders) {
            View view = (jcomp instanceof ViewProvider) 
                    ? ((ViewProvider) jcomp).getHousedViewManifestation()
                    : (View) jcomp;

            // Check if the view is embedded
            View parentView = (View) SwingUtilities.getAncestorOfClass(View.class, view);
            if (parentView != null && parentView.isContentOwner())
                return false;

            targetManifestations.add(view);
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (actionContext.getSelectedManifestations().size() != 1)
            return false;
        
        LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
        if (targetComponent == null)
            return false;
        
        if (lockManager.isManifestationSetLocked(targetComponent.getId(), targetManifestations)) {
            // Commit action is only enabled when the component is locked 
            // (i.e., unlocked in MCT terms) and has pending changes. 
            return lockManager.hasPendingTransaction(targetComponent.getId());
        }
        return false;
    }


}
