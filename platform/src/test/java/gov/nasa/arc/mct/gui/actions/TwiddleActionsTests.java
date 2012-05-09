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
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.TwiddleView;
import gov.nasa.arc.mct.gui.housing.MCTStandardHousing;

import java.util.Collection;
import java.util.Collections;

import javax.swing.JPanel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TwiddleActionsTests {
    
    @Mock private View manifestation;
    @Mock private AbstractComponent component;
    
    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testTwiddleInObjectsMenu() {
        TwiddleAction twiddleAction = new TwiddleAction();        
        ActionContext context = new ActionContext() {
            
            @Override
            public View getWindowManifestation() {
                return null;
            }
            
            @Override
            public Collection<View> getSelectedManifestations() {
                return Collections.singleton(manifestation);
            }
            
            @Override
            public Collection<View> getRootManifestations() {
                return Collections.emptySet();
            }
        };
        
        // manifestation not contained in a TwiddleView
        Assert.assertFalse(twiddleAction.canHandle(context));

        // manifestation contained in a TwiddleView
        MockTwiddleView view = new MockTwiddleView();
        Mockito.when(manifestation.getParent()).thenReturn(view);
        Mockito.when(manifestation.getManifestedComponent()).thenReturn(component);
        Mockito.when(component.isTwiddleEnabled()).thenReturn(true);
        Mockito.when(component.isVersionedComponent()).thenReturn(false);
        Assert.assertTrue(twiddleAction.canHandle(context));
        Assert.assertTrue(twiddleAction.isEnabled());
    }
    
    @Test
    public void testTwiddleInThisMenu() {
        TwiddleWindowAction twiddleWindowAction = new TwiddleWindowAction();
        ActionContext context = new ActionContext() {
            
            @Override
            public View getWindowManifestation() {
                return manifestation;
            }
            
            @Override
            public Collection<View> getSelectedManifestations() {
                return Collections.emptySet();
            }
            
            @Override
            public Collection<View> getRootManifestations() {
                return Collections.emptySet();
            }
        };
        
        // manifestation contained in a TwiddleView
        MCTStandardHousing housing = Mockito.mock(MCTStandardHousing.class);
        Mockito.when(manifestation.getParent()).thenReturn(housing);
        Mockito.when(manifestation.getManifestedComponent()).thenReturn(component);
        Mockito.when(component.isTwiddleEnabled()).thenReturn(true);
        Mockito.when(component.isVersionedComponent()).thenReturn(false);
        Assert.assertTrue(twiddleWindowAction.canHandle(context));
        Assert.assertTrue(twiddleWindowAction.isEnabled());
    }
    
    @SuppressWarnings("serial")
    private static final class MockTwiddleView extends JPanel implements TwiddleView {

        @Override
        public void enterTwiddleMode(AbstractComponent twiddledComponent) {
        }

        @Override
        public void exitTwiddleMode(AbstractComponent originalComponent) {
        }
        
    }
}
