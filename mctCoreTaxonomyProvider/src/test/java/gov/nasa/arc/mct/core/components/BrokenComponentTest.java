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
package gov.nasa.arc.mct.core.components;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.Collections;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BrokenComponentTest extends PersistenceUnitTest {
    
    @Mock private AbstractComponent mockComponent;
    
    @Override
    protected User getUser() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected void postSetup() {
        MockitoAnnotations.initMocks(this);
        (new PlatformAccess()).setPlatform(platform);
        
        Mockito.when(mockComponent.getComponentId()).thenReturn("abc");
    }
    
    @Test
    /**
     * do some simple verifications on Broken Component
     */
    public void verifyBrokenComponent() {
       BrokenComponent bc = new BrokenComponent();
       Assert.assertTrue(bc.isLeaf(),"broken component should be a leaf");
       
       // verify that children cannot be added to the model
       try {
           bc.addDelegateComponent(mockComponent);
           Assert.fail("children cannot be added to the broken component model");
       } catch (UnsupportedOperationException uoe) {
           
       }
       
       // verify that children cannot be added to the model
       try {
           bc.addDelegateComponents(0, Collections.<AbstractComponent> singleton(mockComponent));
           Assert.fail("children cannot be added to the broken component model");
       } catch (UnsupportedOperationException uoe) {
           
       }
   
       // create a new instance of Inspector View, this just verifies the view works, no exceptions
       BrokenInfoPanel panel = new BrokenInfoPanel(bc,new ViewInfo(BrokenInfoPanel.class,"",ViewType.INSPECTOR));
       Assert.assertNotNull(panel);
    }
}
