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

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareMenu;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class GridMenu extends ContextAwareMenu {
    private static final long serialVersionUID = -567011915276411769L;

    private static ResourceBundle bundle = ResourceBundle.getBundle(GridMenu.class.getName().substring(0, 
                    GridMenu.class.getName().lastIndexOf("."))+".Bundle");

    private static final String GRID_TEXT = bundle.getString("Grids");
    
    private static final String VIEW_GRID_SIZE_SUBMENU_EXT = "/objects/grid.size/sizes.ext";

    public GridMenu() {
        super(GRID_TEXT);
    }

    @Override
    protected void populate() {
        Collection<MenuItemInfo> infos = new ArrayList<MenuItemInfo>();
        infos.add(new MenuItemInfo("PANEL_CHANGE_GRID_SIZE", MenuItemType.RADIO_GROUP));
        addMenuItemInfos(VIEW_GRID_SIZE_SUBMENU_EXT, infos);
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        Collection<View> selectedManifestations = context.getSelectedManifestations();
        return MenuUtil.containsCanvasManifestation(selectedManifestations);
    }

}
