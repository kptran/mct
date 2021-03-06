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
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.DetectGraphicsDevices;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.Policy;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentProvider;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PlaceObjectsInCollectionActionTest {
    @Mock
    private AbstractComponent componentA, componentB, rootComponent;
    @Mock
    private AbstractComponent collection;
    @Mock
    private View viewManifestation1, viewManifestation2, viewManifestation3,
                                 viewManifestationRootComponent;
    
    @Mock
    private ExtendedComponentProvider provider;
    
    public static class TestPolicy implements Policy {
        public ExecutionResult execute(PolicyContext context) {
            return new ExecutionResult(context,true,"");
         }
    }
    
    private Policy policy = new TestPolicy();
    
    private PlaceObjectsInCollectionAction action, failAction;
    
    private int successCt, failCt;
    private Collection<AbstractComponent> sourceComponents;
    private boolean setResult;
    

    @SuppressWarnings({ "serial"})
    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(provider.getPolicyInfos()).thenReturn(Collections.singleton(new PolicyInfo(PolicyInfo.CategoryType.CAN_OBJECT_BE_CONTAINED_CATEGORY.getKey(),policy.getClass())));
        PolicyManagerImpl.getInstance().refreshExtendedPolicies(Collections.singletonList(provider));
        action = new PlaceObjectsInCollectionAction() {
            @Override
            protected String getNewCollection(Collection<AbstractComponent> sourceComponents) {
                PlaceObjectsInCollectionActionTest.this.sourceComponents = sourceComponents;
                return "test";
            }
            
            @Override
            AbstractComponent createNewCollection(Collection<AbstractComponent> sourceComponents) {
                return setResult ? collection : null;
            }
            
            @Override
            void openNewCollection(String name, AbstractComponent collection) {
                successCt++;
            }
            
            @Override
            void showErrorInCreateCollection() {
                failCt++;
            }
        };
        
        
        failAction = new PlaceObjectsInCollectionAction() {
            @Override
            protected String getNewCollection(Collection<AbstractComponent> sourceComponents) {
                PlaceObjectsInCollectionActionTest.this.sourceComponents = sourceComponents;
                return "test";
            }
            
            @Override
            AbstractComponent createNewCollection(Collection<AbstractComponent> sourceComponents) {
                return setResult ? collection : null;
            }
            
            @Override
            void openNewCollection(String name, AbstractComponent collection) {
                successCt++;
            }
            
            @Override
            void showErrorInCreateCollection() {
                failCt++;
            }
        };
  
        Mockito.when(viewManifestation1.getManifestedComponent()).thenReturn(componentA);
        Mockito.when(viewManifestation2.getManifestedComponent()).thenReturn(componentA);
        Mockito.when(viewManifestation3.getManifestedComponent()).thenReturn(componentB);
        Mockito.when(viewManifestationRootComponent.getManifestedComponent()).thenReturn(rootComponent);
   
        GlobalComponentRegistry.ROOT_COMPONENT_ID = "0";
     
        Mockito.when(rootComponent.getId()).thenReturn("0");
        Mockito.when(componentA.getId()).thenReturn("1");
        Mockito.when(componentB.getId()).thenReturn("2");
                
        reset();
    }
    
    @Test
    public void testCanHandle() {
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() == DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) { 
            ActionContextImpl context = new ActionContextImpl();
            context.addTargetViewComponent(viewManifestation1);
            context.addTargetViewComponent(viewManifestation2);
            context.addTargetViewComponent(viewManifestation3);
            Assert.assertTrue(action.canHandle(context));       
        }
    }
    
    @Test
    // Test that the action cannot be applied to a ModelRole that
    // does not have a parent. Created a new "failAction" for this as
    // "action" is used elsewhere in this test. 
    public void testCannotHandle() {  
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() == DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) {
            ActionContextImpl failContext = new ActionContextImpl();
            failContext.addTargetViewComponent(viewManifestation1); 
            Assert.assertTrue(failAction.canHandle(failContext));        
            // Add a component with a model role that does not have a parent. 
            failContext.addTargetViewComponent(viewManifestationRootComponent);
            // Action cannot now handle this context. 
            Assert.assertFalse(failAction.canHandle(failContext)); 
        }
    }
    
    @Test(dependsOnMethods = {"testCanHandle"})
    public void testIsEnabled() {
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() == DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) { 
            Assert.assertTrue(action.isEnabled());
        }
    }
    
    @Test(dependsOnMethods = {"testIsEnabled"})
    public void testActionPerformedSuccessfulCase() {
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() == DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) { 
        reset();
        
        // Test case 1: successfully returns a new collection 
        setResult = true;
        action.actionPerformed(new ActionEvent(viewManifestation1, 0, ""));
        Assert.assertEquals(successCt, 1);
        Assert.assertEquals(failCt, 0);
        Assert.assertNotNull(sourceComponents);
        Assert.assertEquals(sourceComponents.size(), 2);
        Assert.assertTrue(sourceComponents.contains(componentA));
        Assert.assertTrue(sourceComponents.contains(componentB));
        }
    }

    @Test(dependsOnMethods = {"testIsEnabled"})
    public void testActionPerformedFailedCase() {
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() == DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) { 
        reset();
        
        // Test case 2: fails to return a new collection (returns null) 
        setResult = false;
        action.actionPerformed(new ActionEvent(viewManifestation1, 0, ""));
        Assert.assertEquals(successCt, 0);
        Assert.assertEquals(failCt, 1);
        Assert.assertEquals(sourceComponents.size(), 2);
        }
    }
    
    @AfterClass
    public void tearDown() {
        PolicyManagerImpl.getInstance().refreshExtendedPolicies(Collections.<ExtendedComponentProvider>emptyList());
    }
    
    private void reset() {
        successCt = failCt = 0;
        sourceComponents = null;
    }
    
}
