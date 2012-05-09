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
package gov.nasa.arc.mct.gui.dialogs;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.gui.util.TestUtilities;
import gov.nasa.arc.mct.util.condition.Condition;

import java.awt.GraphicsEnvironment;

import javax.swing.AbstractButton;
import javax.swing.JFrame;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AboutDialogTest {

    JFrame frame;
    AboutDialog dlg;
    
    @BeforeClass
    public void setup() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        frame = new JFrame("test");
        dlg = new AboutDialog(frame);
    }
    
    @AfterClass
    public void cleanup() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        TestUtilities.killWindow(dlg);
    }

    @Test
    public void testInstantiation() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        assertNotNull(dlg);
        assertTrue(!dlg.isVisible());
    }
    
    @Test
    public void testShowAbout() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final AbstractButton button = TestUtilities.findDescendantButtonWithText(dlg, "Close");
        assertNotNull(button);
        
        TestUtilities.setVisible(dlg, true);
        
        Condition.waitForCondition(5000, new Condition() {
            public boolean getValue() {return dlg.isVisible();}
        });
        assertTrue(dlg.isVisible());
        
        TestUtilities.doClick(button);
        
        Condition.waitForCondition(5000, new Condition() {
            public boolean getValue() {return !dlg.isVisible();}
        });
        assertTrue(!dlg.isVisible());
    }
}
