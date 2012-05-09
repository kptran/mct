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
import gov.nasa.arc.mct.canvas.panel.PanelBorder;
import gov.nasa.arc.mct.canvas.view.CanvasFormattingController;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.GroupAction;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;

public class BordersAllOrNoneAction extends GroupAction {
    private static final long serialVersionUID = 1771407772285064350L;
    public static final String ACTION_KEY = "OBJECTS_BORDERS_ALL_NONE";
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                        BordersAllOrNoneAction.class.getName().substring(0, 
                                        BordersAllOrNoneAction.class.getName().lastIndexOf("."))+".Bundle");
   
    
    public BordersAllOrNoneAction() {
        super("Borders Options <all/none>"); // NOI18N
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public boolean canHandle(ActionContext context) {
        List<Panel> selectedPanels = MenuUtil.getSelectedPanels(context.getSelectedManifestations());
        setActions(new RadioAction[] {
                        new ShowAllBordersAction(selectedPanels),
                        new HideAllBordersAction(selectedPanels)
        }); 
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    private static class ShowAllBordersAction extends GroupAction.RadioAction {
        private static final long serialVersionUID = 1L;
        private List<Panel> selectedPanels;
        
        public ShowAllBordersAction(List<Panel> selectedPanels) {
            this.selectedPanels = selectedPanels;
            putValue(Action.NAME, BUNDLE.getString("BordersAllSides.Label"));
        }
        
        private boolean panelHasAllBorders(Panel p) {
            byte borders = p.getBorderState();
            return PanelBorder.hasEastBorder(borders) &&
                   PanelBorder.hasWestBorder(borders) &&
                   PanelBorder.hasNorthBorder(borders) &&
                   PanelBorder.hasSouthBorder(borders);
        }
        
        private boolean allSidesSelectedOnAllPanels() {
            boolean allSelected = true;
            
            for (Panel p:selectedPanels) {
                if (!panelHasAllBorders(p)) {
                    return false;
                }
            }
            
            return allSelected;
        }
        
        @Override
        public boolean isMixed() {
            return false;
        }
        
        @Override
        public boolean isSelected() {
            return allSidesSelectedOnAllPanels();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CanvasFormattingController.notifyAllBorderStatus(true, selectedPanels);
            MenuUtil.batchSave(selectedPanels);
        }
     
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
    
    private static class HideAllBordersAction extends GroupAction.RadioAction {
        private static final long serialVersionUID = -329998820728054153L;
        private List<Panel> selectedPanels;
        
        public HideAllBordersAction(List<Panel> selectedPanels) {
            this.selectedPanels = selectedPanels;
            putValue(Action.NAME, BUNDLE.getString("BordersNoSides.Label"));
        }

        private boolean panelHasNoBorders(Panel p) {
            byte borders = p.getBorderState();
            return !PanelBorder.hasEastBorder(borders) &&
                   !PanelBorder.hasWestBorder(borders) &&
                   !PanelBorder.hasNorthBorder(borders) &&
                   !PanelBorder.hasSouthBorder(borders);
        }
        
        private boolean noSidesSelectedOnAllPanels() {
            boolean allSelected = true;
            
            for (Panel p:selectedPanels) {
                if (!panelHasNoBorders(p)) {
                    return false;
                }
            }
            
            return allSelected;
        }
        
        @Override
        public boolean isSelected() {
            return noSidesSelectedOnAllPanels();
        }
        
        @Override
        public boolean isMixed() {
            return false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CanvasFormattingController.notifyAllBorderStatus(false, selectedPanels);
            MenuUtil.batchSave(selectedPanels);
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
    
}
