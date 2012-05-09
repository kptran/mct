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
package gov.nasa.arc.mct.canvas.view;

import gov.nasa.arc.mct.canvas.ComponentRegistryAccess;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.roles.events.RemoveChildEvent;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.Collections;
import java.util.Set;

import javax.swing.JPanel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CanvasRemoveComponentUpdateTest {
    
    @Mock private AbstractComponent mockParentComponent;
    @Mock private AbstractComponent mockChildComponent;
    @Mock private MCTViewManifestationInfo mockInfo;
    @Mock private ComponentRegistry mockComponentRegistry;
    private ExtendedProperties extProps; 
    
    private ComponentRegistryAccess access;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        extProps = new ExtendedProperties();

        Mockito.when(mockParentComponent.getComponents()).thenReturn(
                        Collections.<AbstractComponent> singletonList(mockChildComponent));
        
        String childComponentId = "test1";
        Mockito.when(mockChildComponent.getId()).thenReturn(childComponentId);
        Mockito.when(mockInfo.getComponentId()).thenReturn(childComponentId);
        Mockito.when(mockInfo.getInfoProperty("PANEL_ORDER")).thenReturn("0");
        extProps.addProperty("CANVAS CONTENT PROPERTY", mockInfo);
        
        access = new ComponentRegistryAccess();
        Mockito.when(mockComponentRegistry.getComponent(childComponentId)).thenReturn(mockChildComponent);
        access.setRegistry(mockComponentRegistry);        
    }
    
    @AfterMethod
    public void tearDown() {
        access.releaseRegistry(mockComponentRegistry);
    }
    
    @SuppressWarnings("serial")
    @Test
    public void test() {
        View manif = new CanvasManifestation(mockParentComponent, new ViewInfo(CanvasManifestation.class,"",ViewType.OBJECT)) {
            @Override
            public ExtendedProperties getViewProperties() {
                return extProps;
            }
        };
        Mockito.when(mockParentComponent.getComponents()).thenReturn(
                        Collections.<AbstractComponent> emptyList());
        RemoveChildEvent event = new RemoveChildEvent(new JPanel(), mockChildComponent);
        manif.updateMonitoredGUI(event);
        Set<Object> canvasContents = manif.getViewProperties().getProperty("CANVAS CONTENT PROPERTY");
        Assert.assertFalse(canvasContents.iterator().hasNext());
        
    }

}
