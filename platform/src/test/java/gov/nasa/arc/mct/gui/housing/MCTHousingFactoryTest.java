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
package gov.nasa.arc.mct.gui.housing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the MCTHousingFactory class.
 * 
 * @author nshi
 *
 */
public class MCTHousingFactoryTest {

    /**
     * This test sets the byte code that enables all areas for constructing a user environment and invokes
     * the private getter methods in the MCTHousingFactory class for verification.  
     */
    @Test
    public void enableAllAreasTest() {
        byte enabledAreas = MCTHousingFactory.ENABLE_ALL_AREA;
        
        try {
            Method isDirectoryAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isDirectoryAreaEnabled", byte.class);
            isDirectoryAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isDirectoryAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));

            Method isControlAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isControlAreaEnabled", byte.class);
            isControlAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isControlAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));
            
            Method isContentAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isContentAreaEnabled", byte.class);
            isContentAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isContentAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));

            Method isInspectionAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isInspectionAreaEnabled", byte.class);
            isInspectionAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isInspectionAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));
        } catch (SecurityException e) {
            Assert.fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            Assert.fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage());
        } catch (IllegalAccessException e) {
            Assert.fail(e.getMessage());
        } catch (InvocationTargetException e) {
            Assert.fail(e.getMessage());
        }   
    }

    /**
     * This test sets the byte code that enables partial areas for constructing a component's window and invokes
     * the private getter methods in the MCTHousingFactory class for verification.
     * 
     * In this case, the area selection byte code is based on the window created for a telemetry element.
     */
    @Test
    public void enablePartialAreasTest() {
        byte enabledAreas = MCTHousingFactory.CONTROL_AREA_ENABLE | MCTHousingFactory.CONTENT_AREA_ENABLE | MCTHousingFactory.INSPECTION_AREA_ENABLE;

        try {
            Method isDirectoryAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isDirectoryAreaEnabled", byte.class);
            isDirectoryAreaEnabled.setAccessible(true);
            Assert.assertFalse((Boolean) isDirectoryAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));

            Method isControlAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isControlAreaEnabled", byte.class);
            isControlAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isControlAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));

            Method isContentAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isContentAreaEnabled", byte.class);
            isContentAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isContentAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));

            Method isInspectionAreaEnabled = MCTHousingFactory.class.getDeclaredMethod("isInspectionAreaEnabled", byte.class);
            isInspectionAreaEnabled.setAccessible(true);
            Assert.assertTrue((Boolean) isInspectionAreaEnabled.invoke(MCTHousingFactory.class, enabledAreas));
        } catch (SecurityException e) {
            Assert.fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            Assert.fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage());
        } catch (IllegalAccessException e) {
            Assert.fail(e.getMessage());
        } catch (InvocationTargetException e) {
            Assert.fail(e.getMessage());
        }
    }
}
