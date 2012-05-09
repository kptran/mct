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
package gov.nasa.arc.mct.roles;

import gov.nasa.arc.mct.components.ExtendedProperties;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ViewRolePropertiesTest {
    private ExtendedProperties prop;
    
    @BeforeMethod
    public void setup() {
        prop = new ExtendedProperties();
    }
    
    @Test
    public void testProperties() throws CloneNotSupportedException {
        prop.setProperty("testProp1", "testProp1-value");
        Assert.assertTrue(prop.hasProperty());
        
        Assert.assertEquals(prop.getProperty("testProp1", String.class), "testProp1-value");
        
        ExtendedProperties newProp = new ExtendedProperties();
        newProp.setProperties(prop);
        
        Assert.assertEquals(newProp.getProperty("testProp1", String.class), prop.getProperty("testProp1", String.class));
        
        prop.setProperty("testProp2", "testProp2-value");
        Assert.assertEquals(prop.getProperty("testProp2", String.class), "testProp2-value");
        Assert.assertNull(newProp.getProperty("testProp2", String.class));
        
        Object cloned = prop.clone();
        Assert.assertTrue(ExtendedProperties.class.isAssignableFrom(cloned.getClass()));
        
        ExtendedProperties clonedProperties = ExtendedProperties.class.cast(cloned);
        Assert.assertEquals(clonedProperties.getProperty("testProp1", String.class), "testProp1-value");
        Assert.assertEquals(clonedProperties.getProperty("testProp2", String.class), "testProp2-value");
    }
}
