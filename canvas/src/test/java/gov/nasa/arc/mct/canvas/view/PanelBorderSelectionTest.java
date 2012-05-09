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
package gov.nasa.arc.mct.canvas.view;

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants;
import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfo;
import gov.nasa.arc.mct.gui.MCTViewManifestationInfoImpl;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PanelBorderSelectionTest {
    @Mock
    private AbstractComponent mockComponent;

    private Robot robot;
    
    private static final String TITLE = "Panel Border Selection Test";
    private static final int    PANEL_SIZE = 100;
    
    private JFrame frame; 
    
    private Panel  testPanel;
    private CanvasManifestation canvasManifestation = null;
    private View panelManifestation = null;

    private MCTViewManifestationInfo manifestationInfo;
    
    @Mock Platform                          mockPlatform;
    @Mock PolicyManager                     mockPolicyManager;
    
    ExecutionResult lockedResult   = new ExecutionResult(null, true, null);
    ExecutionResult unlockedResult = new ExecutionResult(null, false, null);
    
    Platform oldPlatform;
    
    @BeforeClass
    public void setupClass() {
        oldPlatform = PlatformAccess.getPlatform();
          
    }
    
    @AfterClass
    public void teardownClass() {
        new PlatformAccess().setPlatform(oldPlatform);      
    }

    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        new PlatformAccess().setPlatform(mockPlatform);    

        robot = BasicRobot.robotWithCurrentAwtHierarchy();     

        manifestationInfo = new MCTViewManifestationInfoImpl();
        manifestationInfo.addInfoProperty(ControlAreaFormattingConstants.PANEL_ORDER,"0");
        
        Mockito.when(mockComponent.getViewInfos(ViewType.TITLE)).thenReturn(Collections.singleton(new ViewInfo(MockTitleManifestation.class,"", ViewType.TITLE)));
        Mockito.when(mockComponent.getDisplayName()).thenReturn("test comp");
        Mockito.when(mockComponent.getComponents()).thenReturn(
                        Collections.<AbstractComponent> emptyList());
        
        Mockito.when(mockPlatform.getPolicyManager())
            .thenReturn(mockPolicyManager);
        Mockito.when(mockPolicyManager.execute(Mockito.anyString(), Mockito.<PolicyContext> any()))
            .thenReturn(new ExecutionResult(null, false, null)); // Nothing is locked

        
        GuiActionRunner.execute(new GuiTask() {
            @SuppressWarnings("serial")
            @Override
            protected void executeInEDT() throws Throwable {
        
                frame = new JFrame(TITLE);
                frame.setName(TITLE);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                final ExtendedProperties viewProps = new ExtendedProperties();
                canvasManifestation = new TestCanvasManifestation(mockComponent, new ViewInfo(CanvasManifestation.class, "", ViewType.CENTER)) {
                    @Override
                    public ExtendedProperties getViewProperties() {
                        return viewProps;
                    }
                };                                
                canvasManifestation.setPreferredSize(new Dimension(PANEL_SIZE * 3, PANEL_SIZE * 3));

                frame.getContentPane().add(canvasManifestation);              
                frame.setLocation(PANEL_SIZE, PANEL_SIZE);
                frame.getContentPane().setSize(new Dimension(PANEL_SIZE * 3, PANEL_SIZE * 3));
                frame.setSize(new Dimension(PANEL_SIZE * 3, PANEL_SIZE * 3));
                
                panelManifestation = new MockManifestation(mockComponent, new ViewInfo(CanvasManifestation.class, "", ViewType.OBJECT));
                MCTViewManifestationInfoImpl info = new MCTViewManifestationInfoImpl();
                panelManifestation.putClientProperty(CanvasManifestation.MANIFEST_INFO, info);

                testPanel = canvasManifestation.createPanel(panelManifestation, 0,
                                canvasManifestation);
                Assert.assertNotNull(CanvasManifestation.getManifestationInfo(testPanel.getWrappedManifestation()));
       
                canvasManifestation.getViewProperties().addProperty(CanvasManifestation.CANVAS_CONTENT_PROPERTY, info);
                canvasManifestation.fireSelectionCloned(Collections.singleton(testPanel));
                testPanel.setSize(PANEL_SIZE, PANEL_SIZE);
                testPanel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
                testPanel.setLocation(PANEL_SIZE, PANEL_SIZE);
                
                frame.pack();
                frame.setVisible(true);

            }
        });
    }

    @AfterMethod
    public void tearDown() {
        robot.cleanUp();
    }

    @Test
    public void testFullSize() {
        testPanel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        testPanel.getWrappedManifestation().setPreferredSize(new Dimension(PANEL_SIZE / 2, PANEL_SIZE / 2));
        canvasManifestation.revalidate();
        
        clickBorders();               
    }

    @Test
    public void testWithScrolls() {
        testPanel.getWrappedManifestation().setPreferredSize(new Dimension(PANEL_SIZE * 2, PANEL_SIZE * 2));
        canvasManifestation.revalidate();
        
        clickBorders();               
    }
    
    @Test
    public void testDeselectClearsFocus() {
        JTextField focusField = new JTextField();
        testPanel.getWrappedManifestation().add(focusField);
        testPanel.revalidate();
        focusField.requestFocusInWindow();
        
        robot.click(focusField);
        Assert.assertTrue(focusField.hasFocus());
        
        Point p = testPanel.getLocationOnScreen();
        robot.click(p, MouseButton.LEFT_BUTTON, 1);
        p.translate(-PANEL_SIZE / 2, -PANEL_SIZE / 2);
        robot.click(p, MouseButton.LEFT_BUTTON, 1);
        
        Assert.assertFalse(focusField.hasFocus());
    }
    
    @Test
    public void testCornerDragging() {
        BreakableMouseDragListener listener = new BreakableMouseDragListener();
        
        // This listener will throw runtime exceptions if drag events are received...
        // ...without preceding press events
        panelManifestation.addMouseListener(listener);
        panelManifestation.addMouseMotionListener(listener);
        
        Point p = testPanel.getLocationOnScreen();
        p.translate(testPanel.getWidth() - 1, testPanel.getHeight() - 1);
        robot.click(p, MouseButton.LEFT_BUTTON, 1); // Should select
        robot.waitForIdle();
        Assert.assertTrue(canvasManifestation.getSelectedManifestations().size() > 0);
        Assert.assertFalse(listener.expectsDragEvent);
        p.translate(-20, -20);
        robot.moveMouse(p);
        robot.pressMouse(MouseButton.LEFT_BUTTON);
        robot.releaseMouse(MouseButton.LEFT_BUTTON);
        p.translate(22, 22); // To bottom-right resizer
        robot.moveMouse(p);
        for (int i = 0; i < 10; i++) robot.moveMouse(p);
        robot.pressMouse(MouseButton.LEFT_BUTTON);
        for (int i = 0; i < 5; i++) {
            p.translate(-4, -4);
            robot.moveMouse(p);
        }
        robot.releaseMouseButtons();
        robot.waitForIdle();

        Assert.assertFalse(listener.receivedUnexpectedDragEvent);
    }
    
    
    public void clickBorders() {
        for (double x = 0; x <= 1.0; x += 0.5) {
            for (double y = 0; y <= 1.0; y += 0.5) {
                int dx = (int) (x * (double) (PANEL_SIZE - 1));
                int dy = (int) (y * (double) (PANEL_SIZE - 1));
                
                // Click on the canvas - clear selections
                Point p = canvasManifestation.getLocationOnScreen();
                p.translate(PANEL_SIZE / 2, PANEL_SIZE / 2);        
                robot.click(p, MouseButton.LEFT_BUTTON, 1);
                robot.waitForIdle();
                
                Assert.assertTrue(canvasManifestation.getSelectedManifestations().size() ==  0);
                
                // Click on the border (or center)
                p = testPanel.getLocationOnScreen();
                p.translate(dx, dy);
                robot.click(p, MouseButton.LEFT_BUTTON, 1);
                robot.waitForIdle();
                
                // We should only be selected if we're on the border
                if (x * y * (1.0 - x) * (1.0 - y) == 0.0) {
                    Assert.assertTrue(canvasManifestation.getSelectedManifestations().size() > 0);
                } else {
                    Assert.assertTrue(canvasManifestation.getSelectedManifestations().size() ==  0);                    
                }
                
                
            }
        }
    }
    
    @SuppressWarnings("serial")
    private static class TestCanvasManifestation extends CanvasManifestation {

        @Override
        public void fireFocusPersist() {
        }

        public TestCanvasManifestation(AbstractComponent component, ViewInfo vi) {
            super(component, vi);

        }
        
    }
    
    @SuppressWarnings("serial")
    public static class MockTitleManifestation extends View {
        public MockTitleManifestation(AbstractComponent component, ViewInfo vi) {
            super(component,vi);
            this.setBackground(Color.RED);
        }
    }
    
    @SuppressWarnings("serial")
    public static class MockManifestation extends View {
        public MockManifestation(AbstractComponent component, ViewInfo vi) {
            super(component,vi);
            this.setBackground(Color.GREEN);
        }
    }

    private class BreakableMouseDragListener implements MouseMotionListener, MouseListener {
        private boolean expectsDragEvent = false;
        private boolean receivedUnexpectedDragEvent = false;
        
        
        
        @Override
        public void mouseDragged(MouseEvent arg0) {
            receivedUnexpectedDragEvent = true;
            
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            expectsDragEvent = true;
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            expectsDragEvent = false;
        }


        
    }
}
