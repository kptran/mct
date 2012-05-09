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
package gov.nasa.arc.mct.gui.monitors;

import java.util.Collection;
import java.util.Collections;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.View;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestOpenMultipleMonitorsObjectsAction {

    private OpenMultipleMonitorsObjectsAction openMultiMonitorsObjectsAction;
    
    @Mock 
    private View manifestation;
    
    @Mock 
    private AbstractComponent abstractComp;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        openMultiMonitorsObjectsAction = new OpenMultipleMonitorsObjectsAction();
    }
    
    private final ActionContext mockActionContextObject() {
        ActionContext context = new ActionContext() {
            
             @Override
             public View getWindowManifestation() {
                 return View.NULL_VIEW_MANIFESTATION;
             }
                
             @Override
             public Collection<View> getSelectedManifestations() {
                 return Collections.singleton(manifestation);
             }
                
             @Override
             public Collection<View> getRootManifestations() {
                 return Collections.emptySet();
             }
         };
         
         return context;
    }
    
    @Test
    public void testCanHandle() {
         
         ActionContext context = mockActionContextObject();
         Assert.assertTrue(context.getSelectedManifestations().size() == 1);
         Assert.assertNotNull(context);
         
         Mockito.when(manifestation.getManifestedComponent()).thenReturn(abstractComp);
         Assert.assertNotNull(abstractComp);
    }
    
    @Test
    public void testIsEnabled() {
       Assert.assertNotNull(openMultiMonitorsObjectsAction);
    }
    
}
