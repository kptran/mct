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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public final class PanelInspectorTest {
    
    private PanelInspector panelInspector;
    
    @Mock
    private View view;
    
    Method showHideControllerMethod;
    
    @SuppressWarnings("serial")
    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
                
        AbstractComponent ac = Mockito.mock(AbstractComponent.class);
        ViewInfo vi = Mockito.mock(ViewInfo.class);
        panelInspector = new PanelInspector(ac, vi) {
            @Override
            protected JComponent getViewControls() {
                return new JPanel();
            }
        };
        
        try {
            // set content field
            Field cf = PanelInspector.class.getDeclaredField("content");
            cf.setAccessible(true);
            cf.set(panelInspector, new JPanel());
            
            // set view field
            Field vf = PanelInspector.class.getDeclaredField("view");
            vf.setAccessible(true);
            vf.set(panelInspector, view);
            
            showHideControllerMethod = PanelInspector.class.getDeclaredMethod("showOrHideController", boolean.class);
            showHideControllerMethod.setAccessible(true);
        } catch (SecurityException e) {
            Assert.fail(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            Assert.fail(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Assert.fail(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            Assert.fail(e.getMessage(), e);
        }
    }
    
    @Test
    public void testEnterLockedState() {
        try {
            // set canvas locked state (isLocked) to true
            Field cf = PanelInspector.class.getDeclaredField("isLocked");
            cf.setAccessible(true);
            cf.set(panelInspector, Boolean.TRUE);
            
            showHideControllerMethod.invoke(panelInspector, true);
            
            Mockito.verify(view, Mockito.times(1)).exitLockedState();
        } catch (SecurityException e) {
            Assert.fail(e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            Assert.fail(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            Assert.fail(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Assert.fail(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Assert.fail(e.getMessage(), e);
        }
        
    }

}
