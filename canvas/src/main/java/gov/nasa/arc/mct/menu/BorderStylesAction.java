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

import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants;
import gov.nasa.arc.mct.canvas.formatting.ControlAreaFormattingConstants.BorderStyle;
import gov.nasa.arc.mct.canvas.panel.Panel;
import gov.nasa.arc.mct.canvas.panel.PanelBorder;
import gov.nasa.arc.mct.canvas.view.CanvasFormattingController;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.GroupAction;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class BorderStylesAction extends GroupAction {
    private static final long serialVersionUID = 1L;
    public static final String ACTION_KEY = "OBJECTS_BORDERS_STYLES";
    
    public BorderStylesAction() {
        super("Border Styles Action"); // NOI18N
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public boolean canHandle(ActionContext context) {
        List<Panel> selectedPanels = MenuUtil.getSelectedPanels(context.getSelectedManifestations());
        List<RadioAction> actions = new ArrayList<RadioAction>(BorderStyle.values().length);
        for (BorderStyle style : BorderStyle.values()) {
            actions.add(new BorderStyleRadioButtonAction(selectedPanels, style));
        }
        
        setActions(actions.toArray(new RadioAction[actions.size()])); 
        assert getActions().length == ControlAreaFormattingConstants.BorderStyle.values().length : "border styles action not in sync with available border styles";
        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    private static class BorderStyleRadioButtonAction extends GroupAction.RadioAction {
        private static final long serialVersionUID = 1L;
        private static String TEXT = "          ";
        private final List<Panel> selectedPanels;
        private final BorderStyle style;
        private final boolean selected;
        private final boolean mixed;
        
        public BorderStyleRadioButtonAction(List<Panel> selectedPanels, BorderStyle aStyle) {
            putValue(Action.NAME, TEXT);
            this.selectedPanels = selectedPanels;
            style = aStyle;
            int matchingBorders = matchingBorders();
            mixed = matchingBorders > 0 && matchingBorders < selectedPanels.size();
            selected = matchingBorders == selectedPanels.size();
            putValue(ContextAwareAction.BORDER, createCustomBorder());
            putValue(ContextAwareAction.PREFERRED_SIZE, new Dimension(100, 15));
        }
        
        private int matchingBorders() {
            int sameBorder = 0;
            
            for (Panel p: selectedPanels) {
                if (style == BorderStyle.getBorderStyle(p.getBorderStyle())) {
                    sameBorder++;
                }
            }
            
            return sameBorder;
        }
        
        private Border createCustomBorder() {
            PanelBorder border = new PanelBorder(PanelBorder.NORTH_BORDER);
            border.setBorderStyle(style);
            border.setOffset(20);

            return new CompoundBorder(new EmptyBorder(4,4,4,4), border);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            CanvasFormattingController.notifyBorderFormattingStyle(style.ordinal(), selectedPanels);
            MenuUtil.batchSave(selectedPanels);
        }
        
        @Override
        public final boolean isMixed() {
            return mixed;
        }
        
        @Override
        public final boolean isSelected() {
            return selected;
        }
        
        @Override
        public final boolean isEnabled() {
            return true;
        }
        
    }
    
}
