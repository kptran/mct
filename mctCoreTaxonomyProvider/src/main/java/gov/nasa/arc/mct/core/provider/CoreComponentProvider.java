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
package gov.nasa.arc.mct.core.provider;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.BrokenComponent;
import gov.nasa.arc.mct.core.components.BrokenInfoPanel;
import gov.nasa.arc.mct.core.components.MineTaxonomyComponent;
import gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent;
import gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent;
import gov.nasa.arc.mct.core.components.TelemetryDisciplineComponent;
import gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent;
import gov.nasa.arc.mct.core.policy.AllCannotBeInspectedPolicy;
import gov.nasa.arc.mct.core.policy.AllDropBoxAcceptDelegagteModelPolicy;
import gov.nasa.arc.mct.core.policy.CanDeleteComponentPolicy;
import gov.nasa.arc.mct.core.policy.CanRemoveComponentPolicy;
import gov.nasa.arc.mct.core.policy.CannotDragMySandbox;
import gov.nasa.arc.mct.core.policy.ChangeOwnershipPolicy;
import gov.nasa.arc.mct.core.policy.CheckBuiltinComponentPolicy;
import gov.nasa.arc.mct.core.policy.CheckComponentOwnerIsUserPolicy;
import gov.nasa.arc.mct.core.policy.DefaultViewForTaxonomyNode;
import gov.nasa.arc.mct.core.policy.DisciplineUsersViewControlPolicy;
import gov.nasa.arc.mct.core.policy.DropBoxInspectionPolicy;
import gov.nasa.arc.mct.core.policy.DropboxFilterViewPolicy;
import gov.nasa.arc.mct.core.policy.LeafCannotAddChildDetectionPolicy;
import gov.nasa.arc.mct.core.policy.LockEnablePolicy;
import gov.nasa.arc.mct.core.policy.NeedToLockPolicy;
import gov.nasa.arc.mct.core.policy.ObjectPermissionPolicy;
import gov.nasa.arc.mct.core.policy.PreferredViewPolicy;
import gov.nasa.arc.mct.core.policy.ReservedWordsNamingPolicy;
import gov.nasa.arc.mct.core.policy.SameComponentsCannotBeLinkedPolicy;
import gov.nasa.arc.mct.core.policy.UnlockObjectPolicy;
import gov.nasa.arc.mct.core.roles.DropboxCanvasView;
import gov.nasa.arc.mct.core.roles.UsersManifestation;
import gov.nasa.arc.mct.platform.spi.DefaultComponentProvider;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policy.PolicyInfo.CategoryType;
import gov.nasa.arc.mct.services.component.AbstractComponentProvider;
import gov.nasa.arc.mct.services.component.ComponentTypeInfo;
import gov.nasa.arc.mct.services.component.ProviderDelegate;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class CoreComponentProvider extends AbstractComponentProvider implements DefaultComponentProvider {
    private static final ResourceBundle resource = ResourceBundle.getBundle("CoreTaxonomyResourceBundle"); // NO18N

    @Override
    public Collection<ComponentTypeInfo> getComponentTypes() {
        List<ComponentTypeInfo> compInfos = new ArrayList<ComponentTypeInfo>();
        ComponentTypeInfo typeInfo = new ComponentTypeInfo(resource.getString("all_drop_box_component_display_name"),
                resource.getString("all_drop_box_component_description"), TelemetryAllDropBoxComponent.class, false);
        compInfos.add(typeInfo);
        typeInfo = new ComponentTypeInfo(resource.getString("discipline_component_display_name"), resource
                .getString("discipline_component_description"), TelemetryDisciplineComponent.class, false);
        compInfos.add(typeInfo);
        typeInfo = new ComponentTypeInfo(resource.getString("user_dropbox_component_display_name"), resource
                .getString("user_dropbox_component_description"), TelemetryUserDropBoxComponent.class, false);
        compInfos.add(typeInfo);
        typeInfo = new ComponentTypeInfo(resource.getString("mine_component_display_name"), resource
                .getString("mine_component_description"), MineTaxonomyComponent.class, false);
        compInfos.add(typeInfo);
        typeInfo = new ComponentTypeInfo(resource.getString("broken_component_display_name"), resource
                .getString("broken_component_description"), BrokenComponent.class, false);
        compInfos.add(typeInfo);
        typeInfo = new ComponentTypeInfo(resource.getString("data_taxonomy_component_type_display_name"), resource.getString("data_taxonomy_component_type_description"), TelemetryDataTaxonomyComponent.class, false);
        compInfos.add(typeInfo);

        
        return compInfos;
    }

    @Override
    public Collection<ViewInfo> getViews(String componentTypeId) {
        if (BrokenComponent.class.getName().equals(componentTypeId)) {
            return Collections.singleton(new ViewInfo(BrokenInfoPanel.class, resource.getString("BrokenInspectorViewName"),ViewType.OBJECT)); //NOI18N
        } else if (TelemetryAllDropBoxComponent.class.getName().equals(componentTypeId) ||  
                   TelemetryUserDropBoxComponent.class.getName().equals(componentTypeId)) {
            return Arrays.asList(
                    new ViewInfo(DropboxCanvasView.class, resource.getString("DropBoxViewName"),ViewType.OBJECT),
                    new ViewInfo(DropboxCanvasView.class, resource.getString("DropBoxViewName"),ViewType.CENTER));
        } else if (TelemetryDisciplineComponent.class.getName().equals(componentTypeId)) {
            return Collections.singleton(new ViewInfo(UsersManifestation.class, "Users", ViewType.OBJECT));
        }
        
        return Collections.emptyList();
    }    
            
    @Override
    public Collection<PolicyInfo> getPolicyInfos() {
        return Arrays.asList(
                new PolicyInfo(CategoryType.OBJECT_INSPECTION_POLICY_CATEGORY.getKey(),
                               ObjectPermissionPolicy.class,
                               DropBoxInspectionPolicy.class), 
                new PolicyInfo(CategoryType.ACCEPT_DELEGATE_MODEL_CATEGORY.getKey(),
                               AllDropBoxAcceptDelegagteModelPolicy.class,
                               LeafCannotAddChildDetectionPolicy.class,
                               SameComponentsCannotBeLinkedPolicy.class),
                new PolicyInfo(CategoryType.COMPOSITION_POLICY_CATEGORY.getKey(),
                               UnlockObjectPolicy.class,
                               ObjectPermissionPolicy.class,
                               LeafCannotAddChildDetectionPolicy.class,
                               SameComponentsCannotBeLinkedPolicy.class),
                new PolicyInfo(CategoryType.LOCKING_ENABLE_POLICY_CATEGORY.getKey(),
                               ObjectPermissionPolicy.class,
                               LockEnablePolicy.class),
                new PolicyInfo(CategoryType.NEED_TO_LOCK_CATEGORY.getKey(),
                               NeedToLockPolicy.class),
                new PolicyInfo(CategoryType.PRIVACY_POLICY_CATEGORY.getKey(),
                               ChangeOwnershipPolicy.class),
                new PolicyInfo(CategoryType.OBJECT_VISIBILITY_POLICY_CATEGORY.getKey(),
                               ObjectPermissionPolicy.class),
                new PolicyInfo(CategoryType.COMPONENT_NAMING_POLICY_CATEGORY.getKey(),
                               ReservedWordsNamingPolicy.class),
                new PolicyInfo(CategoryType.ALLOW_COMPONENT_RENAME_POLICY_CATEGORY.getKey(),
                               ChangeOwnershipPolicy.class,
                               CheckBuiltinComponentPolicy.class,
                               ReservedWordsNamingPolicy.class,
                               UnlockObjectPolicy.class,
                               ObjectPermissionPolicy.class),                            
                new PolicyInfo(CategoryType.FILTER_VIEW_ROLE.getKey(),
                               DropboxFilterViewPolicy.class,
                               AllCannotBeInspectedPolicy.class),
                new PolicyInfo(CategoryType.PREFERRED_VIEW.getKey(),
                               PreferredViewPolicy.class),
                new PolicyInfo(CategoryType.CAN_DELETE_COMPONENT_POLICY_CATEGORY.getKey(),
                               CanDeleteComponentPolicy.class),
                new PolicyInfo(CategoryType.CAN_REMOVE_MANIFESTATION_CATEGORY.getKey(),
                        CanRemoveComponentPolicy.class),
                new PolicyInfo(CategoryType.CAN_DUPLICATE_OBJECT.getKey(), 
                               CheckComponentOwnerIsUserPolicy.class),
                new PolicyInfo(CategoryType.CAN_OBJECT_BE_CONTAINED_CATEGORY.getKey(),
                                CannotDragMySandbox.class),
                new PolicyInfo(CategoryType.PREFERRED_VIEW.getKey(),DefaultViewForTaxonomyNode.class),
                new PolicyInfo(CategoryType.SHOW_HIDE_CTRL_MANIFESTATION.getKey(),
                               DisciplineUsersViewControlPolicy.class));
    }
    
    @Override
    public Class<? extends AbstractComponent> getBrokenComponent() {
       return BrokenComponent.class;
    }

    @Override
    public ProviderDelegate getProviderDelegate() {
        return new CoreComponentProviderDelegate();
    }

}
