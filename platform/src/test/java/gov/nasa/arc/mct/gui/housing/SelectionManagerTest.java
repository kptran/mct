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

import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.SelectionProvider;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JLabel;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SelectionManagerTest {
    
    private CapturePropertyChangeEvent capture;
    
    public static class MockRoot extends Component implements SelectionProvider {
        private static final long serialVersionUID = 3979264738948056404L;
        private Collection<View> manifestations;
        public int clears = 0;
        
        @Override
        public Collection<View> getSelectedManifestations() {
            return manifestations;
        }
        
        public void setSelectedManifestations(Collection<View> manifestations) {
            this.manifestations = manifestations;
            firePropertyChange(SelectionProvider.SELECTION_CHANGED_PROP, null, manifestations);
        }
        
        @Override
        public void addSelectionChangeListener(PropertyChangeListener listener) {
            addPropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
        }
        
        @Override
        public void removeSelectionChangeListener(PropertyChangeListener listener) {
            removePropertyChangeListener(SelectionProvider.SELECTION_CHANGED_PROP, listener);
        }
        
        @Override
        public void clearCurrentSelections() {
            clears++;
        }
        
    }
    
    public static class CapturePropertyChangeEvent implements PropertyChangeListener {
        public PropertyChangeEvent event;
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            event = evt;
        }
        
    }
    
    @BeforeMethod
    public void setup() {
        capture = new CapturePropertyChangeEvent();
    }
    
    @Test
    public void testNoSelectionProvider() {
        Object source = new Object();
        SelectionManager sm = new SelectionManager(source);
        sm.manageComponent(new JLabel());
        Assert.assertTrue(sm.getSelectedManifestations().isEmpty());
    }
    
    @Test
    public void testClearSelections() {
        Object source = new Object();
        SelectionManager sm = new SelectionManager(source);
        MockRoot root = new MockRoot();
        sm.manageComponent(root);
        Assert.assertTrue(root.clears == 0);
        sm.clearCurrentSelections();
        Assert.assertTrue(root.clears == 1);
    }
    
    @Test
    public void testPropertyChanges() {
        Object source = new Object();
        MockRoot root = new MockRoot();
        View m1 = Mockito.mock(View.class);
        View m2 = Mockito.mock(View.class);
        MockRoot root2 = new MockRoot();
        
        SelectionManager sm = new SelectionManager(source);
        sm.manageComponent(root);
        sm.manageComponent(root2);
        sm.addSelectionChangeListener(capture);
        
        // fire event in root, should get the selected manifestations in root 1
        root.setSelectedManifestations(Collections.singleton(m1));
        checkEvent(source, Collections.singleton(m1));
        Assert.assertTrue(0 == root.clears);
        Assert.assertTrue(1 == root2.clears);
        
        // now fire event from root 2, change the selected item under that root
        root2.setSelectedManifestations(Collections.singleton(m2));
        checkEvent(source, Collections.singleton(m2));
        Assert.assertTrue(1 == root.clears);
        Assert.assertTrue(1 == root2.clears);
        
        // verify remove listener
        sm.removeSelectionChangeListener(capture);
        capture.event = null;
        root.setSelectedManifestations(Collections.<View>emptyList());
        Assert.assertNull(capture.event);
    }
    
    private void checkEvent(Object expectedSource, Collection<View> expectedManifestations) {
        PropertyChangeEvent event = capture.event;
        Assert.assertNotNull(capture.event);
        Assert.assertEquals(event.getPropertyName(), SelectionProvider.SELECTION_CHANGED_PROP);
        Assert.assertSame(event.getSource(), expectedSource);
        Assert.assertEquals(event.getNewValue(), expectedManifestations);  
    }
    
}
