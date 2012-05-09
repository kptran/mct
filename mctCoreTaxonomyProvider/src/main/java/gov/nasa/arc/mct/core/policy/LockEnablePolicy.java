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
package gov.nasa.arc.mct.core.policy;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewProvider;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;

public class LockEnablePolicy implements Policy {

    @Override
    public ExecutionResult execute(PolicyContext context) {
        ExecutionResult result = new ExecutionResult(context);

        char action = context.getProperty(PolicyContext.PropertyName.ACTION.getName(), Character.class);
        ViewProvider targetManifestationProvider = (ViewProvider) context
                .getProperty(PolicyContext.PropertyName.VIEW_MANIFESTATION_PROVIDER.getName());
        if (targetManifestationProvider == null) {
            targetManifestationProvider = View.NULL_VIEW_MANIFESTATION;
        }
        AbstractComponent component = (AbstractComponent) context.getProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName());
        LockManager lockManager = PlatformAccess.getPlatform().getLockManager();

        if (action != 'w' || !requiresUnlocking(component)) {
            result.setStatus(false);
        } else if (!isSharedComponent(component) || !lockManager.isLocked(component.getId())) {
            result.setStatus(true);
        } else if (lockManager.isManifestationLocked(component.getId(), targetManifestationProvider.getHousedViewManifestation())) {
            result.setStatus(true);
            result.setMessage(component.getDisplayName() + " is currently unlocked by its another manifestation.");
        } else {
            result.setStatus(false);
        }

        return result;
    }
    
    private boolean isSharedComponent(AbstractComponent component) {
        if (component.isVersionedComponent()) {
            component = component.getMasterComponent();
        }
        return component.isShared();
    }

    private boolean requiresUnlocking(AbstractComponent component) {
        return component.isShared() || component.isVersionedComponent();
    }
}
