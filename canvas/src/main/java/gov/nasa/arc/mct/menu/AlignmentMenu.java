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
import gov.nasa.arc.mct.gui.ContextAwareMenu;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class AlignmentMenu extends ContextAwareMenu {
    private static final long serialVersionUID = 1L;

    private Collection<Panel> selectedPanels;
    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                        AlignmentMenu.class.getName().substring(0, 
                                        AlignmentMenu.class.getName().lastIndexOf("."))+".Bundle");
    
    public static final String OBJECTS_ALIGNMENT_ORIENTATION_EXT = "objects/alignment/orientation.ext";

    public AlignmentMenu() {
        super(BUNDLE.getString("AlignmentMenu.Label"));
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        Collection<View> selectedManifestations = context.getSelectedManifestations();
        selectedPanels = MenuUtil.getSelectedPanels(selectedManifestations);
        
        return !selectedPanels.isEmpty();
    }

    @Override
    protected void populate() {
        Collection<MenuItemInfo> infos = new ArrayList<MenuItemInfo>();
        infos.add(new MenuItemInfo(AlignLeftAction.ACTION_KEY, MenuItemType.NORMAL));
        infos.add(new MenuItemInfo(AlignRightAction.ACTION_KEY, MenuItemType.NORMAL));
        infos.add(new MenuItemInfo(AlignTopAction.ACTION_KEY, MenuItemType.NORMAL));
        infos.add(new MenuItemInfo(AlignBottomAction.ACTION_KEY, MenuItemType.NORMAL));
        infos.add(new MenuItemInfo(AlignVerticalCenterAction.ACTION_KEY, MenuItemType.NORMAL));
        infos.add(new MenuItemInfo(AlignHorizontalCenterAction.ACTION_KEY, MenuItemType.NORMAL));
        addMenuItemInfos(OBJECTS_ALIGNMENT_ORIENTATION_EXT, infos);

    }

}
