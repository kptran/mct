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
package gov.nasa.arc.mct.gui.housing;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.Twistie;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestTitleArea {

    @SuppressWarnings("serial")
    /**
     * BoxLayout doesn't like Mockito objects
     */
    public class MyMCTToggleButtonLabel extends Twistie {
        protected MyMCTToggleButtonLabel(Icon offIcon, Icon onIcon) {
            super(offIcon, onIcon);
        }
        public MyMCTToggleButtonLabel() {
            super(null, null);
        }
        @Override
        protected void changeStateAction(boolean state) {
        }
    }

    @Mock MCTInspectionArea mockInspector;
    @Mock AbstractComponent mockComponent;
    @Mock View testView;

    Set<View> myViewManifestations = new HashSet<View>();

    private MCTTitleArea titleArea;

    private static class TestView extends View {
        private static final long serialVersionUID = 1314455759443891633L;
    }
    
    @BeforeMethod
    public void setup() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        MockitoAnnotations.initMocks(this);
        Twistie realToggle = new MyMCTToggleButtonLabel();
        titleArea = new MCTTitleArea(mockInspector, "x", realToggle);
        testView = new TestView();
        testView.setManifestedComponent(mockComponent);
        myViewManifestations.add(testView);
        when(mockComponent.getDisplayName()).thenReturn("XXX");
        when(mockComponent.getAllViewManifestations()).thenReturn(myViewManifestations);
        ViewInfo mockedViewInfo = Mockito.mock(ViewInfo.class);
        when(mockedViewInfo.createView(Mockito.any(AbstractComponent.class))).thenReturn(testView);
        when(mockComponent.getViewInfos(ViewType.NODE)).thenReturn(Collections.singleton(mockedViewInfo));
    }

    @Test
    public void testConstructor() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        assertNotNull(titleArea);
    }

    @Test(dependsOnMethods = "testConstructor")
    public void testSetComponent() {
        if(GraphicsEnvironment.isHeadless()) {
            return;
        }
        titleArea.setComponent(null);
        assertNull(titleArea.getViewManifestation());
    }

    @Test (dependsOnMethods = "testConstructor") 
    public void testRefreshTitle() {
        titleArea.setComponent(mockComponent);
        assertTrue(titleArea.getComponentInTitle().equalsIgnoreCase(mockComponent.getDisplayName()));
        int count = titleArea.getComponentCount();
        titleArea.refreshTitle();
        assertEquals(titleArea.getComponentCount(), count);
    }
  
    @Test (dependsOnMethods  = "testConstructor", expectedExceptions = AssertionError.class) 
    public void testMouseListener() {
        MouseEvent e = new MouseEvent(titleArea, 0, 0, 0, 0, 0, 2, false);
        titleArea.setComponent(mockComponent);
        MouseListener[] listeners = titleArea.getListeners(MouseListener.class);
        assertEquals(listeners.length, 1);
        listeners[0].mouseClicked(e); // will throw an exception on component.open();
    }   
    
    @Test (dependsOnMethods  = "testConstructor") 
    public void testMouseListener1() {
        MouseEvent e = new MouseEvent(titleArea, 0, 0, 0, 0, 0, 1, false);
        titleArea.setComponent(mockComponent);
        MouseListener[] listeners = titleArea.getListeners(MouseListener.class);
        assertEquals(listeners.length, 1);
        listeners[0].mouseClicked(e); // no exception as click count is low.
    } 
    
    @Test (dependsOnMethods  = "testConstructor", expectedExceptions = AssertionError.class) 
    public void testMouseListener2() {
        MouseEvent e = new MouseEvent(titleArea, 0, 0, 0, 0, 0, 2, false);
        titleArea.setComponent(null);
        MouseListener[] listeners = titleArea.getListeners(MouseListener.class);
        assertEquals(listeners.length, 1);
        listeners[0].mouseClicked(e); // throw exception as component is null.
       
    }   
}
