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
package gov.nasa.arc.mct.menu;

import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.Action;

@SuppressWarnings("serial")
public class PanelTitleBarAction extends ContextAwareAction {
    private Collection<Panel> selectedPanels;
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                        PanelTitleBarAction.class.getName().substring(0, 
                                        PanelTitleBarAction.class.getName().lastIndexOf("."))+".Bundle");
    
    public PanelTitleBarAction() {
        super(BUNDLE.getString("PanelTitleBarAction.Label"));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object selectedKey = getValue(Action.SELECTED_KEY);
        boolean titleState = selectedKey == null || ((Boolean)selectedKey).booleanValue();
        for (Panel p:selectedPanels) {
            p.hideTitle(titleState);
        }
        MenuUtil.batchSave(selectedPanels);
    }

    private Boolean getState(Collection<Panel> panels) {
        Boolean state = panels.iterator().next().hasTitle();
        for (Panel p:panels) {
            if (state != p.hasTitle()) {
                state = null;
                break;
            }
        }
        return state;
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        Collection<View> selectedManifestations = context.getSelectedManifestations();
        selectedPanels = MenuUtil.getSelectedPanels(selectedManifestations);
        if (!selectedPanels.isEmpty()) {
            Boolean state = getState(selectedPanels);
            putValue(Action.SELECTED_KEY, state);
        }
        return !selectedPanels.isEmpty();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
