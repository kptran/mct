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
package gov.nasa.arc.mct.platform;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nasa.arc.mct.osgi.platform.OSGIRuntime;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PlatformImplTest {

    @Mock private OSGIRuntime runtime;
    @Mock private BundleContext bc;
    @Mock private ServiceRegistration reg;
    
    private PlatformImpl platform;
    private Service service;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        platform = PlatformImpl.getInstance();
        service = new Service();
        when(runtime.getBundleContext()).thenReturn(bc);
        when(bc.registerService(Service1.class.getName(), service, null)).thenReturn(reg);
    }
    
    @Test
    public void testRegisterOnce() {
        platform.registerService(runtime, Service1.class, service, null);
        platform.unregisterService(service);
        
        verify(bc).registerService(Service1.class.getName(), service, null);
        verify(reg).unregister();
    }
    
    @Test
    public void testRegisterTwice() {
        ServiceRegistration reg2 = mock(ServiceRegistration.class);
        when(bc.registerService(Service2.class.getName(), service, null)).thenReturn(reg2);
        
        platform.registerService(runtime, Service1.class, service, null);
        platform.registerService(runtime, Service2.class, service, null);
        platform.unregisterService(service);
        
        verify(bc).registerService(Service1.class.getName(), service, null);
        verify(bc).registerService(Service2.class.getName(), service, null);
        verify(reg).unregister();
        verify(reg2).unregister();
    }
    
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testBadServiceClass() {
        platform.registerService(runtime, String.class, service, null);
    }
    
    private interface Service1 {}
    
    private interface Service2 {}
    
    private static class Service implements Service1, Service2 {}
    
}
