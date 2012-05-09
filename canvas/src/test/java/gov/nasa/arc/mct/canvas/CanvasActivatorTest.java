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
package gov.nasa.arc.mct.canvas;

import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.MenuManager;
import gov.nasa.arc.mct.services.component.PolicyManager;

import java.io.IOException;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CanvasActivatorTest {
    public static long BUNDLE_ID = 12345;

    @Mock
    private BundleContext bc;
    @Mock
    private ComponentRegistry mockComponentRegistry;
    @Mock
    private PolicyManager mockPolicyManager;
    @Mock
    private MenuManager mockMenuManager;
    @Mock
    private ServiceReference mockCR;
    @Mock
    private ServiceReference mockPM;
    @Mock
    private ServiceReference mockMM;

    @BeforeMethod
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(bc.getServiceReference(ComponentRegistry.class.getName())).thenReturn(mockCR);
        Mockito.when(bc.getServiceReference(PolicyManager.class.getName())).thenReturn(mockPM);
        Mockito.when(bc.getServiceReference(MenuManager.class.getName())).thenReturn(mockMM);
        
        Mockito.when(bc.getService(mockCR)).thenReturn(mockComponentRegistry);
        Mockito.when(bc.getService(mockPM)).thenReturn(mockPolicyManager);
        Mockito.when(bc.getService(mockMM)).thenReturn(mockMenuManager);
    }


    @Test
    public void testStartStop() throws Exception {
        CanvasActivator activator = new CanvasActivator();
        activator.start(bc);
        
        Assert.assertSame(ComponentRegistryAccess.getComponentRegistry(), mockComponentRegistry);
        Assert.assertSame(PolicyManagerAccess.getPolicyManager(), mockPolicyManager);

        activator.stop(bc);
        Assert.assertNull(ComponentRegistryAccess.getComponentRegistry());
        Assert.assertNull(PolicyManagerAccess.getPolicyManager());
    }
}
