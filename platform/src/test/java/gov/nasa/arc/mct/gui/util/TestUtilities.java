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
package gov.nasa.arc.mct.gui.util;

import gov.nasa.arc.mct.util.condition.Condition;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractButton;

/**
 * Utility methods useful for testing GUI objects.
 * 
 * @author mrose
 *
 */
public class TestUtilities {

    /**
     * Find a descendant button in the container <code>root</code> that has
     * a <code>getText()</code> method that returns the given text.
     *
     * @param root the root of the hierarchy to search
     * @param desiredText the text of the button we're looking fore
     * @return the descendant found, or null
     */
    public static AbstractButton findDescendantButtonWithText(Container root, String desiredText) {
        Component[] children = root.getComponents();
        
        // Try to find a direct child first.
        for (int i=0; i<children.length; ++i) {
            if (children[i] instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) children[i];
                if (button.getText().equals(desiredText)) {
                    return button;
                }
            }
        }
        
        // OK, try child containers, too.
        for (int i=0; i<children.length; ++i) {
            if (children[i] instanceof Container) {
                AbstractButton button = findDescendantButtonWithText((Container) children[i], desiredText);
                if (button != null) {
                    return button;
                }
            }
        }
        
        // Not found.
        return null;
    }
    
    /**
     * A simple class to hold a single value. This can be used for testing
     * code that requires an object (for a model, e.g.) with a mutable
     * value.
     * 
     * @author mrose
     *
     */
    public static class Value {
        private boolean value;
        
        public Value(boolean value) {
            this.value = value;
        }
        
        public void setValue(boolean value) {
            this.value = value;
        }
        
        public boolean getValue() {
            return this.value;
        }
    }

    /**
     * Change a component's visibility, making the modification in the
     * GUI event thread.
     * 
     * @param component the component to make visible or invisible
     * @param flag true, if the component should be made visible
     */
    public static void setVisible(final Component component, boolean flag) {
        EventQueue.invokeLater(new Thread() {
            public void run() {
                component.setVisible(true);
            }
        });
    }

    /**
     * Click a button, doing the click in the GUI event thread.
     * 
     * @param button the button to click
     * @throws InvocationTargetException if the target is not a button
     * @throws InterruptedException if the thread was interrupted before the button could be clicked
     */
    public static void doClick(final AbstractButton button) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Thread() {
            public void run() {
                button.doClick();
            }
        });
    }
    
    /**
     * Make sure a window is completely killed, so that it won't inhibit
     * Java from exiting. Use this method to ensure that any frames
     * created by test methods are destroyed at the end of testing.
     * (Perhaps use @BeforeMethod and @AfterMethod, e.g.)
     * 
     * @param frame the top-level window to kill
     * @throws InvocationTargetException if the frame is not top-level window
     * @throws InterruptedException if the thread was interrupted before the frame could be killed
     */
    public static void killWindow(final Window frame) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                frame.setVisible(false);
            }
        });
        Condition.waitForCondition(5000, new Condition() {
            public boolean getValue() {return !frame.isVisible();}
        });
        
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                frame.dispose();
            }
        });
        Condition.waitForCondition(5000, new Condition() {
            public boolean getValue() {return !frame.isDisplayable();}
        });

    }

    /**
     * Check to see if a test is being run with xvfb. We use the string ":1" as a test for its presence,
     * since this is recommended practice with xvfb usage.
     * @return
     */
    public static boolean isXvfbTest() {
        String displayVar = System.getenv("DISPLAY");
        if (displayVar != null && displayVar.equals(":1"))
            return true;
        else
            return false;
    }

}
