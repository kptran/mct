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
import gov.nasa.arc.mct.gui.GroupAction;
import gov.nasa.arc.mct.gui.View;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ChangeGridSizeAction extends GroupAction {
    private static final long serialVersionUID = -5917294719128741636L;

    private static ResourceBundle bundle = ResourceBundle.getBundle("CanvasResourceBundle");

    public ChangeGridSizeAction() {
        super(bundle.getString("Change_Grid_Size"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Returns list of selected manifestations. The default behavior is to return {@link ActionContext#getSelectedManifestations()}.
     * @param context to use for deriving selected manifestations.
     * @return selected manifestations
     */
    protected Collection<View> getSelectedManifestations(ActionContext context) {
        return context.getSelectedManifestations();
    }
    
    @Override
    public boolean canHandle(ActionContext actionContext) {
        List<GroupAction.RadioAction> actions = new ArrayList<RadioAction>();
        actions.add(new ChangeAction(actionContext, bundle.getString("Fine_Grid"), ControlAreaFormattingConstants.FINE_GRID_SIZE));
        actions.add(new ChangeAction(actionContext, bundle.getString("Small_Grid"), ControlAreaFormattingConstants.SMALL_GRID_SIZE));
        actions.add(new ChangeAction(actionContext, bundle.getString("Medium_Grid"), ControlAreaFormattingConstants.MED_GRID_SIZE));
        actions.add(new ChangeAction(actionContext, bundle.getString("Large_Grid"), ControlAreaFormattingConstants.LARGE_GRID_SIZE));
        actions.add(new ChangeAction(actionContext, bundle.getString("No_Grid"), ControlAreaFormattingConstants.NO_GRID_SIZE));
        setActions(actions.toArray(new RadioAction[5]));
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    private int getNumOfManifestationsOfTargetGridSize(
                    Collection<CanvasManifestation> selectedManifestations, int targetGridSize) {
        int numOfCanvasManifestations = 0;
        int numOfTargetGridSize = 0;
        for (CanvasManifestation viewManifestation : selectedManifestations) {
            numOfCanvasManifestations++;
            int gridSize = viewManifestation.getGridSize();
            if (gridSize == targetGridSize) {
                numOfTargetGridSize++;
            }
        }
        return numOfCanvasManifestations - numOfTargetGridSize;
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
    
    private class ChangeAction extends GroupAction.RadioAction {
        private static final long serialVersionUID = 1710587599649997202L;
        
        private Collection<CanvasManifestation> selectedManifestations;
        private int isFixedOrSelected;
        private int gridSize;

        public ChangeAction(ActionContext context, String gridText, int gridSize) {
            putValue(ChangeAction.NAME, gridText);
            this.gridSize = gridSize;
            selectedManifestations = getCanvasManifestations(getSelectedManifestations(context));
            isFixedOrSelected = getNumOfManifestationsOfTargetGridSize(selectedManifestations, gridSize);
        }

        @Override
        public boolean isSelected() {
            return isFixedOrSelected == 0;
        }

        @Override
        public boolean isMixed() {
            return selectedManifestations.size() > 1 && isFixedOrSelected != 0 && isFixedOrSelected != selectedManifestations.size();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (CanvasManifestation manifestation : selectedManifestations) {
                manifestation.enableGrid(gridSize);
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
        
    }
}
