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

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.housing.MemoryMeter;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;

/**
 * This class provides an action for showing and hiding the memory meter
 * 
 */
@SuppressWarnings("serial")
public class MemoryMeterAction extends ContextAwareAction {
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                MemoryMeterAction.class.getName().substring(0, 
                        MemoryMeterAction.class.getName().lastIndexOf("."))+".Bundle");
    
    public MemoryMeterAction() {
        super(BUNDLE.getString("MemoryMeterAction.label"));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MemoryMeter meter = MemoryMeter.getInstance();
        meter.setVisible(!meter.isVisible());
        putValue(Action.SELECTED_KEY, meter.isVisible());
    }

    @Override
    public boolean canHandle(ActionContext context) {
        MemoryMeter meter = MemoryMeter.getInstance();
        putValue(Action.SELECTED_KEY, meter.isVisible());
        return true;
    }
}
