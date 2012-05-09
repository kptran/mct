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
package gov.nasa.arc.mct.subscribe;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import gov.nasa.arc.mct.subscribe.manager.config.ConfigurationService;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ActivatorTest {
    @Mock private BundleContext bc;

    @BeforeMethod
    public void init() throws IOException {
		MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testStartStop() throws Exception {
        Dictionary<String,String> props = new Hashtable<String,String>();
        props.put(Constants.SERVICE_PID, ConfigurationService.PID);
        
        Activator activator = new Activator();
        activator.start(bc);
        
        verify(bc).registerService(eq(ManagedService.class.getName()), eq(ConfigurationService.getInstance()), eq(props));
    }
}
