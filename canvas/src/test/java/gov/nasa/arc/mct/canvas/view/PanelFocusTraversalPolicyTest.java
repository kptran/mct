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

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants.PANEL_ZORDER;
import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.gui.View;

import java.awt.Container;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PanelFocusTraversalPolicyTest {

    private PanelFocusTraversalPolicy policy;
    private Map<Integer, Panel> renderedPanels;
    
    @BeforeClass
    public void setup() {
        renderedPanels = new HashMap<Integer, Panel>();
    }
    
    @Test
    public void testSinglePanel() {
        MockProvider provider = new MockProvider();
        int panelId = 0;
        Panel panel = Mockito.mock(Panel.class);
        MockManifestation manifestation = new MockManifestation();
        Mockito.when(panel.getPanelFocusSelectionProvider()).thenReturn(provider);
        Mockito.when(panel.getWrappedManifestation()).thenReturn(manifestation);
        renderedPanels.put(Integer.valueOf(panelId), panel);
        policy = new PanelFocusTraversalPolicy(renderedPanels);
        Assert.assertSame(policy.getDefaultComponent(provider), panel);
        Assert.assertSame(policy.getFirstComponent(provider), panel);
        Assert.assertSame(policy.getLastComponent(provider), panel);
        Assert.assertNotSame(policy.getComponentBefore(provider, panel), panel);
        Assert.assertNotSame(policy.getComponentAfter(provider, panel), panel);
    }
    
    @SuppressWarnings("serial")
    private static class MockProvider extends Container implements PanelFocusSelectionProvider {

        @Override
        public void fireFocusPersist() {
            
        }

        @Override
        public void fireFocusSelection(Panel panel) {
            
        }

        @Override
        public void fireManifestationChanged() {
            
        }

        @Override
        public void fireOrderChange(Panel panel, PANEL_ZORDER order) {
            
        }

        @Override
        public void fireSelectionCloned(Collection<Panel> panels) {
            
        }

        @Override
        public void fireSelectionRemoved(Panel panel) {
            
        }
        
    }

    @SuppressWarnings("serial")
    private static class MockManifestation extends View {
        
    }
}
