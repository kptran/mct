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
package gov.nasa.arc.mct.canvas.panel;

import gov.nasa.arc.mct.canvas.view.CanvasManifestation;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.DaoStrategyFactory;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.util.Set;

/**
 * This class encapsulates the mechanisms used to do canvas owned views. This class was added as there are multiple places 
 * that need to create and access the canvas owned views, so this strategy can be shared. A second consideration is that 
 * the ability to support non canvas owned views via a configuration could also be achieved using this approach. 
 *
 */
public enum CanvasViewStrategy {
    
    CANVAS_OWNED() {
        public ExtendedProperties getExistingProperties(MCTViewManifestationInfo info, ViewInfo desiredView) {
            ExtendedProperties ep = null;
            for (ExtendedProperties p : info.getOwnedProperties()) {
                String viewType = p.getProperty(OWNED_TYPE_PROPERTY_NAME, String.class);
                if (desiredView.getType().equals(viewType)) {
                    ep = p;
                    break;
                }
            }
            
            return ep;
        }
        
        private void addAllExtendedProperties(MCTViewManifestationInfo manifestInfo, Set<ViewInfo> infos, ComponentInitializer ci) {
            for (ViewInfo info:infos) {
                // if manifest info contains the view then add the persisted properties to the component
                ExtendedProperties savedProperties = getExistingProperties(manifestInfo, info);
                if (savedProperties != null) {
                    ci.setViewRoleProperty(info.getType(), savedProperties);
                } else {
                    ExtendedProperties propertiesFromComponent = ci.getViewRoleProperties(info.getType());
                    if (propertiesFromComponent == null) {
                        propertiesFromComponent = new ExtendedProperties();
                    }
                    assert propertiesFromComponent != null : "properties should not be null";
                    propertiesFromComponent.addProperty(OWNED_TYPE_PROPERTY_NAME, info.getType());
                    manifestInfo.getOwnedProperties().add(propertiesFromComponent);
                }
            }
        }
        
        @Override
        public View createViewFromManifestInfo(ViewInfo info, AbstractComponent comp, AbstractComponent canvas, MCTViewManifestationInfo canvasContent) {
            assert !DaoStrategyFactory.isAlternativeSaveStrategyInUse(comp);
            AbstractComponent clonedComponent = comp.clone();
            ComponentInitializer ci = clonedComponent.getCapability(ComponentInitializer.class);
            addAllExtendedProperties(canvasContent, comp.getViewInfos(ViewType.EMBEDDED), ci);
            ci.setId(canvas.getComponentId());
            ci.setMasterComponent(comp);
            
            ViewInfo canvasViewInfo = new ViewInfo(CanvasManifestation.class, "Canvas", "gov.nasa.arc.mct.canvas.view.CanvasView", ViewType.CENTER);
            DaoStrategyFactory.addAlternateSaveStrategy(clonedComponent, canvas, canvasViewInfo);
            
            return info.createView(clonedComponent);
        }
    };
    
    
    public abstract View createViewFromManifestInfo(ViewInfo info, AbstractComponent component,  AbstractComponent canvas, MCTViewManifestationInfo canvasContent);
    public abstract ExtendedProperties getExistingProperties(MCTViewManifestationInfo info, ViewInfo desiredView);
    public static final String OWNED_TYPE_PROPERTY_NAME = "gov.nasa.arc.mct.canvas.view.Canvas.OwnedPropertiesType";

}
