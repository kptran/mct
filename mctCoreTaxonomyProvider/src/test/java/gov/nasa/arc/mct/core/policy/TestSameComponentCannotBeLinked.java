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
package gov.nasa.arc.mct.core.policy;

import java.util.Collections;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.policy.PolicyContext;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSameComponentCannotBeLinked {
    
    private SameComponentsCannotBeLinkedPolicy policy = new SameComponentsCannotBeLinkedPolicy();
    
    @Test
    public void test() {
        AbstractComponent componentA = Mockito.mock(AbstractComponent.class);
        AbstractComponent componentB = Mockito.mock(AbstractComponent.class);
        
        Mockito.when(componentA.getId()).thenReturn("A");
        Mockito.when(componentB.getId()).thenReturn("B");
        
        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), componentA);
        context.setProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(), Collections.singletonList(componentB));
        Assert.assertTrue(policy.execute(context).getStatus());
    }

}
