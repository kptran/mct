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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.housing.MCTStandardHousing;
import gov.nasa.arc.mct.gui.housing.SelectionManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.lang.reflect.Field;

import javax.swing.JFrame;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestHousingViewManifestation {
    
    @Mock private AbstractComponent component;
    @Mock private MCTStandardHousing mockHousing;
    @Mock private SelectionManager mockSelection;
    private ViewInfo vi = new ViewInfo(MCTHousingViewManifestation.class,"housing",ViewType.LAYOUT); 
    private MCTHousingViewManifestation housing;
    
    @BeforeMethod
    void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(component.getId()).thenReturn("7");
        housing = new MCTHousingViewManifestation(component,vi);
        
    }
    
    @DataProvider(name="lockStateTest")
    Object[][] generateLockStateTitleTests() {
        return new Object[][] {
                new Object[] {""},
                new Object[] {"name"}
        };
    }
    
    @Test(dataProvider="lockStateTest")
    public void testLockStateTitleChanges(String originalPanelTitle) throws Exception {
        JFrame frame = new JFrame(originalPanelTitle);
        Mockito.when(mockHousing.getHostedFrame()).thenReturn(frame);
        Field parentHousing = MCTHousingViewManifestation.class.getDeclaredField("parentHousing");
        parentHousing.setAccessible(true);
        parentHousing.set(housing, mockHousing);
        Field selectionManager = MCTHousingViewManifestation.class.getDeclaredField("selectionManager");
        selectionManager.setAccessible(true);
        selectionManager.set(housing, mockSelection);
        
        housing.exitLockedState();
        Assert.assertEquals(frame.getTitle(),originalPanelTitle);
        
        housing.processDirtyState();
        housing.exitLockedState();
        Assert.assertEquals(frame.getTitle(),originalPanelTitle);
        
        
    }
}
