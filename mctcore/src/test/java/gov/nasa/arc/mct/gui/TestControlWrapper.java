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
package gov.nasa.arc.mct.gui;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View.ControlWrapper;
import gov.nasa.arc.mct.gui.View.GlassPanel;
import gov.nasa.arc.mct.gui.View.GlassPanelRepaintManager;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.services.component.PolicyManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class TestControlWrapper {

    private PlatformAccess access;
    @Mock private Platform platform;
    @Mock private PolicyManager policyManager;
    
    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
    @BeforeMethod
    public void setupBeforeTestMethod() {
        access = new PlatformAccess();
        Mockito.when(platform.getPolicyManager()).thenReturn(policyManager);
        access.setPlatform(platform);
    }
    
    @AfterMethod
    public void tearDownAfterTestMethod() {
        access.setPlatform(null);
    }

    @Test
    public void testWrapper() {
        
        // Manifestation is currently locked
        Mockito.doAnswer(new Answer<ExecutionResult>() {

            @Override
            public ExecutionResult answer(InvocationOnMock invocation) throws Throwable {
                ExecutionResult result = new ExecutionResult(new PolicyContext(), true, null);
                return result;
            }

        }).when(policyManager).execute(Mockito.anyString(), Mockito.any(PolicyContext.class));

        MockManifestation manifestation = new MockManifestation();
        
        // Verify control manifestation is wrapped
        JComponent controlManifestation = manifestation.getControlManifestation();
        Assert.assertTrue(controlManifestation instanceof ControlWrapper);
        ControlWrapper controlWrapper = (ControlWrapper) controlManifestation;
        GlassPanel controlGlass = controlWrapper.getGlassPanel();
        Assert.assertTrue(controlGlass.getMouseListeners().length > 0);
        Assert.assertTrue(controlGlass.getKeyListeners().length > 0);
        
        // Verify that the input listeners are removed when the glass panel is turned off
        manifestation.enterLockedState();
        Assert.assertEquals(controlGlass.getMouseListeners().length, 0);
        Assert.assertEquals(controlGlass.getKeyListeners().length, 0);

        manifestation.exitLockedState();
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        Rectangle rectangle = new Rectangle();
        Mockito.when(mockGraphics.getClipBounds()).thenReturn(rectangle);
        controlGlass.paintComponent(mockGraphics);
        Mockito.verify(mockGraphics).setColor(Mockito.any(Color.class));
        Mockito.verify(mockGraphics).fillRect(0, 0, 0, 0);
        
        // now check that we don't use the graphics context when we are in transparent mode
        manifestation.enterLockedState();
        Graphics mockGraphics2 = Mockito.mock(Graphics.class);
        controlGlass.paintComponent(mockGraphics2);
        Mockito.verifyNoMoreInteractions(mockGraphics2);
    }

    @SuppressWarnings("serial")
    @Test
    public void testRepaintManager() {
        final AtomicReference<JComponent> reference = new AtomicReference<JComponent>();
        
        GlassPanelRepaintManager repaintManager = new GlassPanelRepaintManager() {
            @Override
            void addDirtyRegionToSuperRepaintManager(JComponent c, int x, int y, int w, int h) {
                reference.set(c);
            }
        };
        
        // Test control manifestation and its glass panel
        JPanel controlPanel = new JPanel();
        ControlWrapper controlWrapper = new ControlWrapper(controlPanel);
        repaintManager.addDirtyRegion(controlPanel, 0, 0, 0, 0);
        Assert.assertSame(reference.get(), controlWrapper.getGlassPanel());

        // Test view manifestation and its glass panel
        View manifestation = new View() {
            @Override
            public AbstractComponent getManifestedComponent() {
                return Mockito.mock(AbstractComponent.class);
            }  
        };
        
        repaintManager.addDirtyRegion(manifestation, 0, 0, 0, 0);
        Assert.assertSame(reference.get(), manifestation);
        
        // Test other unrelated JComponent
        JComponent widget = Mockito.mock(JComponent.class);
        repaintManager.addDirtyRegion(widget, 0, 0, 0, 0);
        Assert.assertSame(reference.get(), widget);
    }

    @SuppressWarnings("serial")
    private class MockManifestation extends View {
        
        public MockManifestation() {
        }
        
        @Override
        protected JComponent initializeControlManifestation() {
            return new ControlPanel();
        }
        
        @Override
        public AbstractComponent getManifestedComponent() {
            return Mockito.mock(AbstractComponent.class);
        }
        
        private class ControlPanel extends JPanel {
            
        }
    }
    
}
