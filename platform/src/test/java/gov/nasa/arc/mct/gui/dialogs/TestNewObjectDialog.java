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

import gov.nasa.arc.mct.components.AbstractComponent;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestNewObjectDialog {
    private JFrame frame = new JFrame();
    private NewObjectDialog dialog;
    
    @BeforeClass
    public void setup() {
        if (GraphicsEnvironment.isHeadless()) return;

        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                dialog = new NewObjectDialog(frame, "A test String", new DefaultWizardUI(MockComponent.class));
            }
        });
    }
    
    
    @Test
    public void test() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        long threadStartTime = System.currentTimeMillis();
        while (dialog == null && System.currentTimeMillis() - threadStartTime < 5000) {
            for (Window window : frame.getOwnedWindows()) {
                if (window instanceof NewObjectDialog) {
                    dialog = (NewObjectDialog) window;
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }                    
                }
            }
        }

        if  (dialog == null)
            Assert.fail("Dialog not created.");

        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                try {
                    Class<? extends NewObjectDialog> dialogClass = dialog.getClass();
                    Field wiz = dialogClass.getDeclaredField("wizard");
                    wiz.setAccessible(true);
                    DefaultWizardUI wizUI = (DefaultWizardUI) wiz.get(dialog);                 
                    
                    Field createButtonField = dialogClass.getDeclaredField("create");
                    createButtonField.setAccessible(true);
                    JButton createButton = (JButton) createButtonField.get(dialog);
                    createButton.setEnabled(true);
                    createButton.doClick();
                    
                    Field confirmed = dialogClass.getDeclaredField("confirm");
                    confirmed.setAccessible(true);
                    boolean c = confirmed.getBoolean(dialog);
                    
                    Assert.assertTrue(c);
          //          Assert.assertEquals(dialog.getName(), "test");
                } catch (SecurityException e) {
                    Assert.fail(e.getMessage(), e);
                } catch (NoSuchFieldException e) {
                    Assert.fail(e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    Assert.fail(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    Assert.fail(e.getMessage(), e);
                }
            }
        });
    }
    
    @AfterClass
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;

        if (dialog != null && dialog.isVisible())
            dialog.dispose();
        if (frame != null && frame.isVisible())
            frame.dispose();
    }
    public static class MockComponent extends AbstractComponent {
    }
}
