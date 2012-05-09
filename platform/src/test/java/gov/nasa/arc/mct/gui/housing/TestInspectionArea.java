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
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.dao.specifications.MCTUser;
import gov.nasa.arc.mct.gui.SelectionProvider;
import gov.nasa.arc.mct.gui.Twistie;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.MCTInspectionArea.InspectorPane;
import gov.nasa.arc.mct.persistence.PersistenceUnitTest;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.User;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestInspectionArea extends PersistenceUnitTest {

    @SuppressWarnings("serial")
    class MyInspectionArea extends MCTInspectionArea {
        public MyInspectionArea() {
            super(Mockito.mock(AbstractComponent.class), Mockito.mock(ViewInfo.class));
        }

        public boolean getControlAreaVisibility() {
            return controlAreaVisible;
        }
    }

    class TestSelectionProvider implements SelectionProvider {
        private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        public View selected;
        
        public void setSelected(View selection) {
            selected = selection;
            pcs.firePropertyChange(SelectionProvider.SELECTION_CHANGED_PROP, null, Collections.singleton(selected));
        }
        
        @Override
        public void addSelectionChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }

        @Override
        public Collection<View> getSelectedManifestations() {
            return Collections.singleton(selected);
        }

        @Override
        public void removeSelectionChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
        @Override
        public void clearCurrentSelections() {
        }
        
    }
    
    private MyInspectionArea inspector;
    
    @Mock private MCTUser user;
    @Mock MCTHousing mockHousing;
    @Mock AbstractComponent mockComponent;
    private TestSelectionProvider selectionProvider;
    
    @Override
    protected User getUser() {
        MockitoAnnotations.initMocks(this);
        
        when(user.getUserId()).thenReturn("asi");
        when(user.getDisciplineId()).thenReturn("CATO");
        return user;
    }
    
    @Override
    protected void postSetup() {
        inspector = new MyInspectionArea();
        selectionProvider = new TestSelectionProvider();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(inspector);
    }


    @Test(dependsOnMethods = "testConstructor")
    public void testTitleAreaVisible() {
      MCTTitleArea titleArea = new MCTTitleArea(inspector, "Title", new MyTwistie()); 
      assertNotNull(titleArea);
    }

    @Test(dependsOnMethods = "testConstructor")
    public void testSetControlAreaVisible() {
        // MODI-659: inspector should not have a control area.
        assertEquals(inspector.getControlAreaVisibility(), false); 
    }

    @Test(dependsOnMethods = "testConstructor")
    public void testAddManifestationWithController() {
        ViewInfo mockInfo = Mockito.mock(ViewInfo.class);
        MCTInspectionArea inspectorArea = new MCTInspectionArea(mockComponent, mockInfo);
        MockManifestation viewManifestation = new MockManifestation(mockComponent, mockInfo);
        viewManifestation.setEnableController(true);
        viewManifestation.setComponent(mockComponent);
        when(mockComponent.getViewInfos(ViewType.NODE)).thenReturn(Collections.singleton(mockInfo));
        when(mockComponent.getViewInfos(ViewType.OBJECT)).thenReturn(Collections.<ViewInfo>singleton(mockInfo));
        Mockito.when(mockInfo.getViewType()).thenReturn(ViewType.OBJECT);
        Mockito.when(mockInfo.getViewName()).thenReturn("Mock");
        Mockito.when(mockInfo.createView(Mockito.any(AbstractComponent.class))).thenReturn(viewManifestation);
        selectionProvider.setSelected(viewManifestation);
        MockInspectionArea mockInspectionArea = new MockInspectionArea(mockComponent,null);
        for (AncestorListener l : inspectorArea.getAncestorListeners()) {
            AncestorEvent ae = new AncestorEvent(new JPanel(), 1, new JFrame(), mockInspectionArea);
            l.ancestorAdded(ae);
        }        

        PropertyChangeListener[] propertyChangeListeners = mockInspectionArea.getPropertyChangeListeners(SelectionProvider.SELECTION_CHANGED_PROP);
        assertEquals(propertyChangeListeners.length, 1);
        PropertyChangeEvent mockEvent = Mockito.mock(PropertyChangeEvent.class);
        Mockito.when(mockEvent.getNewValue()).thenReturn(Collections.singletonList(viewManifestation));
        Mockito.when(mockEvent.getPropertyName()).thenReturn(SelectionProvider.SELECTION_CHANGED_PROP);
        propertyChangeListeners[0].propertyChange(mockEvent);

        assertEquals(inspectorArea.getNumberOfInspectedViews(), 1);
        
        try {
            Field field = MCTInspectionArea.class.getDeclaredField("tabbedPane");
            field.setAccessible(true);
            JTabbedPane tabbedPane = (JTabbedPane) field.get(inspectorArea);
            assertEquals(tabbedPane.getTabCount(), 1);
            Component component = tabbedPane.getComponentAt(0);
            Assert.assertTrue(component instanceof InspectorPane);
            InspectorPane pane = (InspectorPane) component;
            Assert.assertEquals(pane.getViewManifestation(), viewManifestation);
            
            field = pane.getClass().getDeclaredField("splitPane");
            field.setAccessible(true);
            JSplitPane splitter = (JSplitPane) field.get(pane);
            Assert.assertTrue(splitter.getDividerSize() == 0);
            
            pane.setControlVisible(true);
            Assert.assertTrue(splitter.getDividerSize() > 0);
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
        
    @Test(dependsOnMethods = "testConstructor")
    public void testAddManifestationWithNoController() {
        ViewInfo mockInfo = Mockito.mock(ViewInfo.class);
        MCTInspectionArea inspectorArea = new MCTInspectionArea(mockComponent,mockInfo);
        MockManifestation viewManifestation = new MockManifestation(mockComponent,mockInfo);
        viewManifestation.setEnableController(false);
        viewManifestation.setComponent(mockComponent);
        when(mockComponent.getViewInfos(ViewType.NODE)).thenReturn(Collections.singleton(mockInfo));
        when(mockComponent.getViewInfos(ViewType.OBJECT)).thenReturn(Collections.<ViewInfo>singleton(mockInfo));
        Mockito.when(mockInfo.getViewType()).thenReturn(ViewType.OBJECT);
        Mockito.when(mockInfo.getViewName()).thenReturn("Mock");
        Mockito.when(mockInfo.createView(Mockito.any(AbstractComponent.class))).thenReturn(viewManifestation);

        selectionProvider.setSelected(viewManifestation);
        MockInspectionArea mockInspectionArea = new MockInspectionArea(mockComponent,null);
        for (AncestorListener l : inspectorArea.getAncestorListeners()) {
            AncestorEvent ae = new AncestorEvent(new JPanel(), 1, new JFrame(), mockInspectionArea);
            l.ancestorAdded(ae);
        }        
        
        PropertyChangeListener[] propertyChangeListeners = mockInspectionArea.getPropertyChangeListeners(SelectionProvider.SELECTION_CHANGED_PROP);
        assertEquals(propertyChangeListeners.length, 1);
        PropertyChangeEvent mockEvent = Mockito.mock(PropertyChangeEvent.class);
        Mockito.when(mockEvent.getNewValue()).thenReturn(Collections.singletonList(viewManifestation));
        Mockito.when(mockEvent.getPropertyName()).thenReturn(SelectionProvider.SELECTION_CHANGED_PROP);
        propertyChangeListeners[0].propertyChange(mockEvent);
        
        assertEquals(inspectorArea.getNumberOfInspectedViews(), 1);
        
        try {
            Field field = MCTInspectionArea.class.getDeclaredField("tabbedPane");
            field.setAccessible(true);
            JTabbedPane tabbedPane = (JTabbedPane) field.get(inspectorArea);
            assertEquals(tabbedPane.getTabCount(), 1);
            Component component = tabbedPane.getComponentAt(0);
            Assert.assertTrue(component instanceof InspectorPane);
            InspectorPane pane = (InspectorPane) component;
            Assert.assertEquals(pane.getViewManifestation(), viewManifestation);
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
    
    @Test
    public void testSelectionWillChange() {
        AbstractComponent ac = Mockito.mock(AbstractComponent.class);
        ViewInfo vi = Mockito.mock(ViewInfo.class);
        View oldView = Mockito.mock(View.class);
        View newView = Mockito.mock(View.class);
        TestSelectionInspectionArea inspectionArea = new TestSelectionInspectionArea(ac, vi);
        inspectionArea.addSelectionChangeListener(new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                Assert.assertSame(e.getPropertyName(), InspectionArea.SELECTION_WILL_CHANGE_PROP);                
            }
        });
        inspectionArea.testFirePropChange(InspectionArea.SELECTION_WILL_CHANGE_PROP, oldView, newView);
    }
    
    @SuppressWarnings("serial")
    private static final class MockManifestation extends View {
        
        private AbstractComponent component;
        private boolean enableController;
        
        public MockManifestation(AbstractComponent ac, ViewInfo vi) {
            super(ac,vi);
        }
        
        public void setEnableController(boolean enableController) {
            this.enableController = enableController;
        }
        
        @Override
        protected JComponent initializeControlManifestation() {
            return enableController ? new JPanel() : null;
        }
        
        public void setComponent(AbstractComponent ac) {
            component = ac;
        }
        
        @Override
        public AbstractComponent getManifestedComponent() {
            return component;
        }
    }
    
    @SuppressWarnings("serial")
    private class MyTwistie extends Twistie {

        public MyTwistie() {
            super();
        }
        
        @Override
        protected void changeStateAction(boolean state) {
          
        }
        
    }
    
    @SuppressWarnings("serial")
    private class TestSelectionInspectionArea extends InspectionArea {
        public TestSelectionInspectionArea(AbstractComponent ac, ViewInfo vi) {
            super(ac, vi);
        }
        
        public void testFirePropChange(String propName, Object oldValue, Object newValue) {
            firePropertyChange(propName, oldValue, newValue);
        }
    }
    
    @SuppressWarnings("serial")
    private class MockInspectionArea extends InspectionArea {
        public MockInspectionArea(AbstractComponent ac, ViewInfo vi) {
            super(ac,vi);
        }
        
        @Override
        public void addSelectionChangeListener(PropertyChangeListener listener) {
            super.addPropertyChangeListener(SELECTION_CHANGED_PROP, listener);
        }        
    }
}
