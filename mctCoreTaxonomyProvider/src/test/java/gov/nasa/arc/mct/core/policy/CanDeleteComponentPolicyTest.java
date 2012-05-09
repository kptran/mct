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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.core.components.MineTaxonomyComponent;
import gov.nasa.arc.mct.platform.core.access.PlatformAccess;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.internal.component.User;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CanDeleteComponentPolicyTest {
    
    private static final String USER = "asi";
 
    private CanDeleteComponentPolicy policy = new CanDeleteComponentPolicy();
    @Mock
    private AbstractComponent mockShareComponent;
    @Mock
    private AbstractComponent mockPrivateComponent;
    @Mock
    private AbstractComponent mockPrivateComponentWithDifferentOwner;
    @Mock
    private Platform mockPlatform;
    @Mock
    private User user;
    
    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(user.getUserId()).thenReturn(USER);
        Mockito.when(mockShareComponent.isShared()).thenReturn(true);
        Mockito.when(mockShareComponent.getOwner()).thenReturn(USER);
        Mockito.when(mockPrivateComponent.isShared()).thenReturn(false);
        Mockito.when(mockPrivateComponent.getOwner()).thenReturn(USER);
        Mockito.when(mockPrivateComponentWithDifferentOwner.isShared()).thenReturn(false);
        Mockito.when(mockPrivateComponentWithDifferentOwner.getOwner()).thenReturn("some other user");

        
        Mockito.when(mockPlatform.getCurrentUser()).thenReturn(user);
        (new PlatformAccess()).setPlatform(mockPlatform);
    }
    
 
    
    @AfterTest
    public void shutDown() {
   }
    
    @Test
    public void testCanDeleteComponentPolicy() {
        PolicyContext context = new  PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), mockShareComponent);
        ExecutionResult exResult = policy.execute(context);
        
        Assert.assertFalse(exResult.getStatus());
        
        context = new  PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), mockPrivateComponent);
        exResult = policy.execute(context);
        
        Assert.assertTrue(exResult.getStatus());
        
        context = new  PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), mockPrivateComponentWithDifferentOwner);
        exResult = policy.execute(context);
        
        Assert.assertFalse(exResult.getStatus());
        
        MineTaxonomyComponent mockMine = Mockito.mock(MineTaxonomyComponent.class);
        context = new  PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), mockMine);
        exResult = policy.execute(context);
        
        Assert.assertFalse(exResult.getStatus());
    }
}
