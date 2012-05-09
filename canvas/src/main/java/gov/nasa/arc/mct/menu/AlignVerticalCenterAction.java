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
import gov.nasa.arc.mct.canvas.view.CanvasFormattingController;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.View;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class AlignVerticalCenterAction extends ContextAwareAction {
    public static final String ACTION_KEY = "CANVAS_OBJECTS_VERTICAL_ALIGNMENT_CENTER";
    private static final long serialVersionUID = 1L;

    private List<Panel> selectedPanels;
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                        AlignHorizontalCenterAction.class.getName().substring(0, 
                                        AlignHorizontalCenterAction.class.getName().lastIndexOf("."))+".Bundle");
    
    
    public AlignVerticalCenterAction() {
        super(BUNDLE.getString("AlignVerticalCenterAction.Label"));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) { 
        CanvasFormattingController.notifyAlignVCenterSelected(selectedPanels);
        MenuUtil.batchSave(selectedPanels);
    }


    @Override
    public boolean canHandle(ActionContext context) {
        Collection<View> selectedManifestations = context.getSelectedManifestations();
        selectedPanels = MenuUtil.getSelectedPanels(selectedManifestations);
        
        return !selectedPanels.isEmpty();
    }

    @Override
    public boolean isEnabled() {
        return selectedPanels.size() > 1;
    }

}
