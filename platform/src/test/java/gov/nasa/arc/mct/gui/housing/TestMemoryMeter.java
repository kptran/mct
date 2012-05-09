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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.AncestorListener;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestMemoryMeter {
    private MemoryMeter meter;
    private JPanel parent;

    @BeforeTest
    public void setup() {
        parent = new JPanel();
        meter = MemoryMeter.getInstance();
        meter.setVisible(true);
    }
    
    @Test
    public void testVisible() {
        JComponent widget = meter.getMemoryMeterWidget();
        parent.add(widget);
        meter.setVisible(true);
        assertTrue(meter.isVisible());
        notifyAncestorListeners(widget, true);
        assertTrue(widget.isVisible());
        
        meter.setVisible(false);
        assertFalse(meter.isVisible());
        assertFalse(widget.isVisible());
        
        notifyAncestorListeners(widget,false);
        widget = null;
        parent = null;
        meter.setVisible(true);
    }
    
    private void notifyAncestorListeners(JComponent component, boolean added) {
        for (AncestorListener l : component.getAncestorListeners()) {
            if (added) {
                l.ancestorAdded(null);
            } else {
                l.ancestorRemoved(null);
            }
            l.ancestorMoved(null);
        }
    }
}
