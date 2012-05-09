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
package gov.nasa.arc.mct.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains a list of MenuItemInfo instances. Note that the order of menuItemInfo instances is preserved.
 * @author nija.shi@nasa.gov 
 */
public final class MenuSection {
    private String menubarPath;
    private List<MenuItemInfo> list;
   
    /**
     * Constructor.
     * @param menubarPath the menubarPath
     */
    public MenuSection(String menubarPath) {
        this.menubarPath = menubarPath;
    }
    
    /**
     * @return the menubarPath
     */
    public String getMenubarPath() {
        return menubarPath;
    }

    /**
     * Adds a MenuItemInfo to this section.
     * @param info MenuItemInfo
     */
    public void addMenuItemInfo(MenuItemInfo info) {
        if (list == null)
            list = new ArrayList<MenuItemInfo>();
        boolean found = false;
        for (MenuItemInfo menuItemInfo : list) {
            String commandKey = info.getCommandKey();
            if (menuItemInfo.getCommandKey().equals(commandKey)) {
                found = true;
                break;
            }
        }
        if (!found)
            list.add(info);
    }
    
    /**
     * @return the list of MenuItemInfo contained in this section
     */
    public List<MenuItemInfo> getMenuItemInfoList() {
        return list == null ? Collections.<MenuItemInfo>emptyList() : Collections.unmodifiableList(list);
    }
}
