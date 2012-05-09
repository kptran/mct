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
package gov.nasa.arc.mct.version.manager;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.util.CloneUtil;
import gov.nasa.arc.mct.components.util.ComponentModelUtil;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.persistence.strategy.ComponentSpecificationDaoStrategy;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.util.StringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MCTComponentVersionManager {
    private static AbstractComponent newVersion(AbstractComponent origComponent) {
        AbstractComponent clonedComponent = CloneUtil.VERSION.clone(origComponent);
        clonedComponent.setDaoStrategy(new ComponentSpecificationDaoStrategy(clonedComponent));
        return clonedComponent;
    }

    public static void versionized(View viewManifestation) {
        versionized(Collections.singleton(viewManifestation));
    }

    public static void versionized(Set<View> viewManifestation) {
        assert viewManifestation != null;
        assert !viewManifestation.isEmpty();

        AbstractComponent origComponent = viewManifestation.iterator().next().getManifestedComponent();
        if (!origComponent.isShared()) {
            return;
        }
        AbstractComponent versionedComponent = newVersion(origComponent);
        for (View manifest : viewManifestation) {
            manifest.setManifestedComponent(versionedComponent);
            versionedComponent.addViewManifestation(manifest);
        }
        GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().startSession(origComponent.getId());
    }

    public static void mergeVersionAndUpdate(Set<View> viewManifestations) {
        assert viewManifestations != null;
        assert !viewManifestations.isEmpty();
        
        AbstractComponent versionedComponent = viewManifestations.iterator().next().getManifestedComponent();
        AbstractComponent masterComponent = versionedComponent.getMasterComponent();

        if (masterComponent == null) {
            return;
        }

        GlobalContext.getGlobalContext().getSynchronousPersistenceBroker().closeSession(masterComponent.getId());        
        assert masterComponent.isShared();
        
        for (View manifest : viewManifestations) {           
            // check for view info
            if (manifest.getInfo() != null) {
                ExtendedProperties properties = manifest.getViewProperties();
                masterComponent.getCapability(ComponentInitializer.class).setViewRoleProperty(manifest.getInfo().getType(), properties);
            }
            manifest.setManifestedComponent(masterComponent);
        }
        
        mergeComponents(masterComponent, versionedComponent);
    }

    private static void mergeComponents(AbstractComponent masterComponent, AbstractComponent versionedComponent) {        
        if (!StringUtil.compare(masterComponent.getDisplayName(), versionedComponent.getDisplayName()))
            masterComponent.setDisplayName(versionedComponent.getDisplayName());
        if (!StringUtil.compare(masterComponent.getOwner(), versionedComponent.getOwner()))
            masterComponent.setOwner(versionedComponent.getOwner());
        ModelStatePersistence versionedModelState = versionedComponent.getCapability(ModelStatePersistence.class);
        if (versionedModelState != null) {
            ModelStatePersistence masterModelState = masterComponent.getCapability(ModelStatePersistence.class);
            masterModelState.setModelState(versionedModelState.getModelState());
        }

        List<AbstractComponent> masterDelegateComponents = masterComponent.getComponents();
        List<AbstractComponent> versionedDelegateComponents = versionedComponent.getComponents();
        Set<AbstractComponent> componentsToBeAdded = new HashSet<AbstractComponent>();
        Set<AbstractComponent> componentsToBeRemoved = new HashSet<AbstractComponent>();
        ComponentModelUtil.computeAsymmetricSetDifferences(versionedDelegateComponents, masterDelegateComponents, componentsToBeAdded, componentsToBeRemoved);
        
        if (componentsToBeAdded.isEmpty() && componentsToBeRemoved.isEmpty()) {
            masterComponent.refreshViewManifestations();
            return;
        }
        
        if (!componentsToBeAdded.isEmpty()) {
            masterComponent.addDelegateComponents(componentsToBeAdded);
        }
        if (!componentsToBeRemoved.isEmpty()) {
            masterComponent.removeDelegateComponents(componentsToBeRemoved);
        }
    }
    
}
