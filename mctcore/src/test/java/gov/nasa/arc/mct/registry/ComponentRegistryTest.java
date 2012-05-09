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
package gov.nasa.arc.mct.registry;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ComponentRegistryTest {

    public static final String COMPONENT_ID = "123";
    public static final String COMPONENT_TYPE_ID = "gov.nasa.arc.mct.unittest.component";
    public static final String VIEW_ID = "gov.nasa.arc.mct.unittest.view";
    
    @Mock private AbstractComponent component;
    @Mock private Platform mockPlatform;
    @Mock private LockManager mockLockManager;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        
        when(component.getId()).thenReturn(COMPONENT_ID);
        when(mockPlatform.getLockManager()).thenReturn(mockLockManager);
        (new PlatformAccess()).setPlatform(mockPlatform);
        GlobalComponentRegistry.clearRegistry();
    }
    
    @Test
    public void testRegisterComponent() {
        assertNull(GlobalComponentRegistry.getComponent(COMPONENT_ID));
        
        GlobalComponentRegistry.registerComponent(component);
        assertTrue(GlobalComponentRegistry.getComponent(COMPONENT_ID) != null);
        
        GlobalComponentRegistry.registerComponent(component);
        assertSame(GlobalComponentRegistry.getComponent(COMPONENT_ID), component);
        
        assertNull(GlobalComponentRegistry.getComponent("badID"));
        
        GlobalComponentRegistry.removeComponent(COMPONENT_ID);
        assertNull(GlobalComponentRegistry.getComponent(COMPONENT_ID));
    }
    
    @Test
    public void testRegisterComponentTypeByClass() throws ClassNotFoundException {
        // If we haven't registered the component, the registry will use
        // Class.forName().
        assertSame(GlobalComponentRegistry.getComponentType(MyComponent.class.getName()), MyComponent.class);

        // This will cause lookup in the registry.
        GlobalComponentRegistry.registerComponentType(MyComponent.class);
        assertSame(GlobalComponentRegistry.getComponentType(MyComponent.class.getName()), MyComponent.class);
        
    }

    // Disabled until we can have ComponentRegistry.clearRegistry() clear everything.
    @Test(enabled=false,expectedExceptions={ClassNotFoundException.class})
    public void testGetNonexistentComponentTypeByID() throws ClassNotFoundException {
        @SuppressWarnings("unused")
        Class<?> t = GlobalComponentRegistry.getComponentType(COMPONENT_TYPE_ID);
    }
    
    @Test
    public void testRegisterComponentTypeByID() throws ClassNotFoundException {
        GlobalComponentRegistry.registerComponentType(COMPONENT_TYPE_ID, MyComponent.class);
        assertSame(GlobalComponentRegistry.getComponentType(COMPONENT_TYPE_ID), MyComponent.class);
        assertSame(GlobalComponentRegistry.getComponentType(MyComponent.class.getName()), MyComponent.class);
    }
    
    
    protected static class MyComponent extends AbstractComponent {
        private String componentTypeID;
        public MyComponent(String typeID) {
            componentTypeID = typeID;
        }
        public MyComponent() {
            this(MyComponent.class.getName());
        }
        @Override
        public String getComponentTypeID() {
            return componentTypeID;
        }
    }
    
}
