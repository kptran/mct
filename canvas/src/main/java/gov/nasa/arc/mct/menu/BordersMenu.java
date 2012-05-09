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
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;

import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

public class BordersMenu extends ContextAwareMenu {
    private static final long serialVersionUID = 1L;
    public static final String ACTION_KEY = "CANVAS_BORDERS_MENU";

    private static final ResourceBundle BUNDLE = 
        ResourceBundle.getBundle(
                        BordersMenu.class.getName().substring(0, 
                                        BordersMenu.class.getName().lastIndexOf("."))+".Bundle");
    
    
    public static final String OBJECTS_BORDERS_ALLORNONE_EXT = "objects/borders/allornone.ext";
    public static final String OBJECTS_BORDERS_SIDES_EXT = "objects/borders/sides.ext";

    public BordersMenu() {
        super(BUNDLE.getString("BordersMenu.Label"));      
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        return !MenuUtil.getSelectedPanels(context.getSelectedManifestations()).isEmpty();
    }

    @Override
    protected void populate() {
        addMenuItemInfos(OBJECTS_BORDERS_SIDES_EXT, Arrays.asList(
                new MenuItemInfo(BordersLeftAction.ACTION_KEY, MenuItemType.CHECKBOX),
                new MenuItemInfo(BordersRightAction.ACTION_KEY, MenuItemType.CHECKBOX),
                new MenuItemInfo(BordersTopAction.ACTION_KEY, MenuItemType.CHECKBOX),
                new MenuItemInfo(BordersBottomAction.ACTION_KEY, MenuItemType.CHECKBOX)));
        
        addMenuItemInfos(OBJECTS_BORDERS_ALLORNONE_EXT, Collections.<MenuItemInfo>singleton(new MenuItemInfo("OBJECTS_BORDERS_ALL_NONE", MenuItemType.RADIO_GROUP)));
    }

}
