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
package gov.nasa.arc.mct.components;

import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

public class TestDetectGraphicsDevices {

    private DetectGraphicsDevices detectGraphicsDevice;
    private static final String DISPLAY_0 = "Monitor0";
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        detectGraphicsDevice = DetectGraphicsDevices.getInstance();
    }
    
    @Test
    public void testIsGraphicsEnvHeadless() {
        if (detectGraphicsDevice.isGraphicsEnvHeadless()) {
            Assert.assertEquals(detectGraphicsDevice.isGraphicsEnvHeadless(), true);
        } else {
            Assert.assertEquals(detectGraphicsDevice.isGraphicsEnvHeadless(), false);
        }
    }
    
    @Test
    public void testGetNumberGraphicsDevices() {
        Assert.assertTrue(detectGraphicsDevice.getNumberGraphicsDevices() > 0);
    }
    
    @Test
    public void testGetGraphicsDevice() {
        Assert.assertTrue(detectGraphicsDevice.getGraphicsDevice().length > 0);
    }
    
    @Test(enabled=false)
    public void testGetSingleGraphicDeviceConfig() {
        if (!detectGraphicsDevice.isGraphicsEnvHeadless() && DetectGraphicsDevices.isMac()) {
            Assert.assertNotNull(detectGraphicsDevice.getSingleGraphicDeviceConfig(DISPLAY_0));
        }
    }
    
    @Test
    public void testGetGraphicDeviceNames() {
        Assert.assertTrue(detectGraphicsDevice.getGraphicDeviceNames().size() >= 0);
    }
}
