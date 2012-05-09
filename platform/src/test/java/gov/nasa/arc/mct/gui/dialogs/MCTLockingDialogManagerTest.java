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

import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.lock.manager.MockLockManager;
import gov.nasa.arc.mct.platform.spi.MockPlatform;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.roles.gui.MockViewManifestation;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;

import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MCTLockingDialogManagerTest {

    private final static String threadID = Long.toString(System.currentTimeMillis()) + "-TEST";
    private Thread guiThread;
    private JDialog dialog;
    private View targetViewManifestation;

    private final PlatformAccess access = new PlatformAccess();
    private final Platform platform = new MockPlatform();

    @BeforeClass
    public void setup() {
        // Run this test headless.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GlobalContext.getGlobalContext().setLockManager(new MockLockManager() {
            @Override
            public synchronized boolean hasPendingTransaction(String componentId) {
                return true;
            }
        });
    }

    @Test(groups={"heavyGUI"})
    public void tesShowUnlockedConfirmationDialog() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        access.setPlatform(platform);
        MockComponent component = new MockComponent();
        component.getCapability(ComponentInitializer.class).initialize();
        component.setShared(true);
        
        targetViewManifestation = new MockViewManifestation(component, null);
        
        guiThread = new Thread(threadID) {

            @Override
            public void run() {
                AbstractComponent component = Mockito.mock(AbstractComponent.class);
                Mockito.when(component.getId()).thenReturn("test");
                View manifestation = Mockito.mock(View.class);
                Mockito.when(manifestation.getManifestedComponent()).thenReturn(component);
                LockManager lockManager = Mockito.mock(LockManager.class);
                Mockito.when(lockManager.hasPendingTransaction(component.getId())).thenReturn(true);
                GlobalContext.getGlobalContext().setLockManager(lockManager);
                Map<String, Set<View>> lockMap = new HashMap<String, Set<View>>();
                lockMap.put(component.getId(), Collections.singleton(manifestation));
                MCTDialogManager.showUnlockedConfirmationDialog(targetViewManifestation, lockMap, "Close", "Window");
            }
            
        };

        guiThread.start();

        long threadStartTime = System.currentTimeMillis();
        
        JOptionPane optionPane = getJOptionPane();
        while (optionPane == null && (System.currentTimeMillis() - threadStartTime < 5000)) {
            Thread.sleep(500);
            optionPane = getJOptionPane();
        }

        Assert.assertNotNull(optionPane);
        Assert.assertEquals(optionPane.getOptions().length, 3);
        access.releasePlatform();
    }
        
    @AfterClass
    public void teardown() throws Exception {
        // We can't run this test headless.
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
     
        guiThread.interrupt();
        if (dialog != null && dialog.isVisible())
            dialog.dispose();
    }
    
    
    private JOptionPane getJOptionPane(JDialog dialog) {
        LinkedList<java.awt.Component> stack = new LinkedList<java.awt.Component>();
        stack.addFirst(dialog);
        while (!stack.isEmpty()) {
            java.awt.Component widget = stack.removeFirst();
            if (widget instanceof JOptionPane) {
                JOptionPane optionPane = (JOptionPane)widget;
                String message = (String) optionPane.getMessage();
                if (message.indexOf(targetViewManifestation.getManifestedComponent().getDisplayName()) > 0)
                    return optionPane;
            } else if (widget instanceof Container) {
                stack.addAll(Arrays.asList(((Container) widget).getComponents()));
            } 
        }
        return null;
    }
        
    private JOptionPane getJOptionPane() {
        
        for (Window window : Window.getWindows()) {
            if (window instanceof JDialog) {
                JOptionPane optionPane = getJOptionPane((JDialog) window);
                if (optionPane != null) {
                    dialog = (JDialog) window;
                    return optionPane;
                }
            }
        }
        return null;        
    }
}
