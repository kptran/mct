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

import gov.nasa.arc.mct.gui.util.TestUtilities;
import gov.nasa.arc.mct.util.condition.Condition;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MCTDialogManagerTest {

    JFrame frame;
    JDialog dlg;

    @BeforeClass
    public void setup() {
        // We can't run this test headless.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        frame = new JFrame("test");
    }

    @Test(groups={"heavyGUI"})
    public void testInstantiation() throws Exception {
        // We can't run this test headless.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MCTDialogManager.showAboutMCTDialog(frame);
            }
        });
        
        Condition.waitForCondition(5000L, new Condition() {
            public boolean getValue() {
                return (frame.getOwnedWindows().length > 0);
            }
        });
        assertEquals(frame.getOwnedWindows().length, 1);
        dlg = (JDialog) frame.getOwnedWindows()[0];
        
        Condition.waitForCondition(5000, new Condition() {
            public boolean getValue() {return dlg.isVisible();}
        });
        assertTrue(dlg.isVisible());
        
        final AbstractButton button = TestUtilities.findDescendantButtonWithText(dlg, "Close");
        assertNotNull(button);
        TestUtilities.doClick(button);
        
        Condition.waitForCondition(5000, new Condition() {
            public boolean getValue() {return !dlg.isVisible();}
        });
        assertTrue(!dlg.isVisible());

    }
    
    @AfterClass
    public void teardown() throws Exception {
        // We can't run this test headless.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        TestUtilities.killWindow(dlg);
    }

}
