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

import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.actions.TwiddleAction;
import gov.nasa.arc.mct.gui.actions.TwiddleWindowAction;
import gov.nasa.arc.mct.policy.DisallowModelChangeForTwiddledComponent;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class provides policies for filtering default views. This is currently a separate class from the DefaultViewProvider as the 
 * default view provider is currently injected directly into the ExternalComponentRegistry. Combine this class with the DefaultViewProvider
 * once this capability becomes available. 
 *
 */
public class DefaultViewPolicyProvider extends AbstractComponentProvider {

    @Override
    public Collection<MenuItemInfo> getMenuItemInfos() {
        return Arrays.asList(
                new MenuItemInfo(
                        "/objects/additions",
                        "TWIDDLE_MODE_ACTION",
                        MenuItemInfo.MenuItemType.CHECKBOX,
                        TwiddleAction.class),
                new MenuItemInfo(
                        "/this/additions",
                        "TWIDDE_MODE_WINDOW_ACTION",
                        MenuItemInfo.MenuItemType.CHECKBOX,
                        TwiddleWindowAction.class));
    }
    
    @Override
    public Collection<PolicyInfo> getPolicyInfos() {
        return Arrays.asList(
                new PolicyInfo(PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey(), 
                        DisallowModelChangeForTwiddledComponent.class));
    }

}
