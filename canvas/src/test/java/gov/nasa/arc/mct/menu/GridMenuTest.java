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
package gov.nasa.arc.mct.menu;

import gov.nasa.arc.mct.canvas.view.CanvasManifestation;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GridMenuTest {
    @Mock
    private AbstractComponent mockComponent;
    
    private GridMenu gridMenu;
    private WindowGridMenu windowGridMenu;
    
    @Mock
    private ActionContext actionContext;
    @Mock private View mockView;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(mockComponent.getComponents()).thenReturn(
                        Collections.<AbstractComponent> emptyList());
        Mockito.when(mockView.getManifestedComponent()).thenReturn(mockComponent);
        
        gridMenu = new GridMenu();
        windowGridMenu = new WindowGridMenu();
    }
    
    
    @SuppressWarnings("serial")
    @Test
    public void canHandleTest() {
        Collection<View> selectedManifestations = new ArrayList<View>();
        selectedManifestations.add(mockView);
        Mockito.when(actionContext.getSelectedManifestations()).thenReturn(selectedManifestations);
        
        Assert.assertFalse(gridMenu.canHandle(actionContext));
        Assert.assertFalse(windowGridMenu.canHandle(actionContext));
        
        selectedManifestations = new ArrayList<View>();
        CanvasManifestation canvasManifestation = new CanvasManifestation(mockComponent, new ViewInfo(CanvasManifestation.class,"",ViewType.CENTER)) {
            @Override
            public ExtendedProperties getViewProperties() {
                return new ExtendedProperties();
            }
        };
        selectedManifestations.add(canvasManifestation);
        Mockito.when(actionContext.getSelectedManifestations()).thenReturn(selectedManifestations);
        Mockito.when(actionContext.getRootManifestations()).thenReturn(selectedManifestations);
        
        Assert.assertTrue(gridMenu.canHandle(actionContext));
        Assert.assertTrue(windowGridMenu.canHandle(actionContext));
        
    }
}
