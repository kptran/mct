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
import gov.nasa.arc.mct.canvas.view.PanelFocusSelectionProvider;
import gov.nasa.arc.mct.gui.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * Shared implementation for menu actions.
 */
public class MenuUtil {
    private MenuUtil() {}
    
    /**
     * Return the selected panels based on the currently selected manifestations.
     * @return collection of the selected panels may be empty but never null
     */
    public static List<Panel> getSelectedPanels(Collection<View> selectedManifestations) {
        List<Panel> selectedPanels = new ArrayList<Panel>(selectedManifestations.size());
        
        for (View manifestation : selectedManifestations) {
            Panel panel = (Panel) SwingUtilities.getAncestorOfClass(Panel.class, manifestation);
            if (panel != null) {
                selectedPanels.add(panel);
            }
        }
        return selectedPanels;
    }
    
    public static boolean containsCanvasManifestation(Collection<View> selectedManifestations) {
        for (View manifestation : selectedManifestations) {
            if (manifestation instanceof PanelFocusSelectionProvider) { return true; }
        }
        return false;
    }
    
    static void batchSave(Collection<Panel> panels) {
        Map<PanelFocusSelectionProvider, Panel> panelsToBeSaved = new HashMap<PanelFocusSelectionProvider, Panel>();
        for (Panel panel: panels) {
            PanelFocusSelectionProvider focusSelectionProvider = panel.getPanelFocusSelectionProvider();
            if (!panelsToBeSaved.containsKey(focusSelectionProvider)) {
                panelsToBeSaved.put(focusSelectionProvider, panel);
            }
        }
        for (PanelFocusSelectionProvider focusProvider: panelsToBeSaved.keySet()) {
            focusProvider.fireFocusPersist();
        }
    }
    
    static boolean isContainerManifestationLocked(Collection<Panel> panels) {
        Map<PanelFocusSelectionProvider, Panel> panelsToBeSaved = new HashMap<PanelFocusSelectionProvider, Panel>();
        for (Panel panel: panels) {
            PanelFocusSelectionProvider focusSelectionProvider = panel.getPanelFocusSelectionProvider();
            if (!panelsToBeSaved.containsKey(focusSelectionProvider)) {
                panelsToBeSaved.put(focusSelectionProvider, panel);
            }
        }
        for (PanelFocusSelectionProvider focusProvider: panelsToBeSaved.keySet()) {
            if (focusProvider instanceof View) {
                if (!View.class.cast(focusProvider).isLocked()) {
                    return false;
                }
            }
        }
        return true;
    }
}
