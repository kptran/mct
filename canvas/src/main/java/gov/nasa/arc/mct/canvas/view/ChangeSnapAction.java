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

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;

public class ChangeSnapAction extends ContextAwareAction {
    private static final long serialVersionUID = -5917294719128741636L;

    private static ResourceBundle bundle = ResourceBundle.getBundle("CanvasResourceBundle");
    
    private Collection<CanvasManifestation> selectedManifestations;
    
    public ChangeSnapAction() {
        super(bundle.getString("Snap_to_Grid"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object selectedKey = getValue(Action.SELECTED_KEY);
        boolean state = selectedKey == null || ((Boolean)selectedKey).booleanValue();
        
        for (CanvasManifestation manifestation : selectedManifestations) {
            manifestation.enableSnap(state);
        }
    }
    
    private Boolean getState() {
        Boolean state = selectedManifestations.iterator().next().isSnapEnable();
        for (CanvasManifestation manifestation: selectedManifestations) {
            if (manifestation.isSnapEnable() != state.booleanValue()) {
                return null;
            }
        }
        return state;
    }

    @Override
    public boolean canHandle(ActionContext actionContext) {
        selectedManifestations = getCanvasManifestations(getSelectedManifestations(actionContext));
        if (!selectedManifestations.isEmpty()) {
            Boolean state = getState();
            putValue(Action.SELECTED_KEY, state);
        }
        return !selectedManifestations.isEmpty();
    }
    
    protected Collection<View> getSelectedManifestations(ActionContext context) {
        return context.getSelectedManifestations();
    }

    @Override
    public boolean isEnabled() {
        for (CanvasManifestation manifestation : selectedManifestations) {
            if (manifestation.getGridSize() != ControlAreaFormattingConstants.NO_GRID_SIZE) {
                return true;
            }
        }
        return false;
    }

    private Collection<CanvasManifestation> getCanvasManifestations(
                    Collection<View> selectedManifestations) {
        List<CanvasManifestation> selectedCanvasManifestations = new LinkedList<CanvasManifestation>();

        for (View viewManifestation : selectedManifestations) {
            if (viewManifestation instanceof CanvasManifestation) {
                selectedCanvasManifestations.add((CanvasManifestation) viewManifestation);
            }
        }
        return selectedCanvasManifestations;
    }
}
