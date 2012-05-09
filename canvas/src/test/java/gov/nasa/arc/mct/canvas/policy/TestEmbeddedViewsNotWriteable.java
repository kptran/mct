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
package gov.nasa.arc.mct.canvas.policy;

import gov.nasa.arc.mct.canvas.view.CanvasManifestation;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.DaoStrategyFactory;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestEmbeddedViewsNotWriteable {
    
    private AbstractComponent mockedComponent = new AbstractComponent(){};
    private Policy embeddedPolicy;
    private PolicyContext context;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        embeddedPolicy = new EmbeddedCanvasViewsAreNotWriteable();
        context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), mockedComponent);
    }
    
    @Test
    public void testEmbeddedView() {
        Assert.assertTrue(embeddedPolicy.execute(context).getStatus(), "strategies which are not delegating should not trigger this policy");
        AbstractComponent owningComponent = new AbstractComponent(){};
        
        DaoStrategyFactory.addAlternateSaveStrategy(mockedComponent, owningComponent, new ViewInfo(CanvasManifestation.class,"Canvas",ViewType.EMBEDDED));
        Assert.assertFalse(embeddedPolicy.execute(context).getStatus(), "delegating dao strategies should not be writeable");
    }
}
