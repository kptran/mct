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
package gov.nasa.arc.mct.gui.menu;

import static org.testng.Assert.assertNotNull;
import gov.nasa.arc.mct.gui.util.TestSetupUtilities;

import java.awt.GraphicsEnvironment;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This test class supplements DynamicMenuTest
 *
 */
public class TestMCTMenuFactory {

    @BeforeClass
    public void setup() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
//        TestSetupUtilities.setUpForPersistence();
//        TestSetupUtilities.setUpActiveHousing();
        TestSetupUtilities.setupForMockPolicyManager();
        TestSetupUtilities.setupForMenuBar();
    }

    @Test
    public void testConstructor() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        assertNotNull(MenuFactory.createMCTRadioButtonMenuItem("Hello", "key"));
    }
    
    @Test(expectedExceptions = AssertionError.class)
    public void testFailedOnNull() {
        MenuFactory.createStandardHousingMenuBar(null);
    }

//    @Test
//    // TODO: This test needs an active housing with menu bar and menu items
//    public void testUserObjectPopupMenu() {
//        if(GraphicsEnvironment.isHeadless()) {
//            return;
//        }
//        MCTActionContext context = new MCTActionContext();
//        context.setTargetComponent(null);
//        assertNotNull(MCTMenuFactory.createUserObjectPopupMenu(context));
//    }
}
