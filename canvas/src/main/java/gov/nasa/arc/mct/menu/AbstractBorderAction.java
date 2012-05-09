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
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;

public abstract class AbstractBorderAction extends ContextAwareAction {
    private static final long serialVersionUID = 4278808231344910629L;
    private List<Panel> selectedPanels;
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                        AbstractBorderAction.class.getName().substring(0, 
                                        AbstractBorderAction.class.getName().lastIndexOf("."))+".Bundle");
    
    public AbstractBorderAction(String bundleKeyName) {
        super(BUNDLE.getString(bundleKeyName));
    }
    
    private boolean isSelected() {
        Object selectedKey = getValue(Action.SELECTED_KEY);
        return selectedKey != null && ((Boolean)selectedKey).booleanValue();
    }
    
    @Override
    public final void actionPerformed(ActionEvent e) {
        boolean state = isSelected();
        setBorderState(selectedPanels, state);
        MenuUtil.batchSave(selectedPanels);
    }

    @Override
    public final boolean canHandle(ActionContext context) {
        Collection<View> selectedManifestations = context.getSelectedManifestations();
        selectedPanels = MenuUtil.getSelectedPanels(selectedManifestations);
        if (!selectedPanels.isEmpty()) {
            Boolean state = getState(selectedPanels);
            putValue(Action.SELECTED_KEY, state);
        }
        return !selectedPanels.isEmpty();
    }

    private Boolean getState(Collection<Panel> panels) {
        Boolean state = isBorderActive(panels.iterator().next());
        for (Panel p:panels) {
            if (state != isBorderActive(p)) {
                state = null;
                break;
            }
        }
        return state;
    }
    
    /**
     * Returns true if the appropriate border is active for the panel
     * @param p
     */
    protected abstract boolean isBorderActive(Panel p);
    
    /**
     * Performs the appropriate border action on this state.
     * @param panels to active state on 
     * @param state to set
     */
    protected abstract void setBorderState(List<Panel> panels, boolean state);
    
    @Override
    public final boolean isEnabled() {
        return true;
    }

}
