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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;

/**
 * This class defines the super class for menus used in MCT. Subclasses of this type of menus 
 * are context-aware, meaning that the availability this action depends on the 
 * {@link ActionContext} being passed in. A {@link ContextAwareMenu} contains a set of 
 * {@link MenuSection}. Each section contains a set of {@link MenuItemInfo}s, and the sections
 * are separated by a {@link javax.swing.JSeparator JSeparator}.
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public abstract class ContextAwareMenu extends JMenu {
    
    private List<MenuSection> sections;
    private Map<String, MenuSection> map;
    
    private String[] menubarPathAdditions;
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    /**
     * The constructor that takes a {@link String} parameter for the text of this menu. 
     * @param text the text appears in the menu
     */
    public ContextAwareMenu(String text) {
        this(text, EMPTY_STRING_ARRAY);
    }

    /**
     * The constructor that takes a name for this menu and an array of menubar paths.
     * These menubar paths define the extension points of this menu.
     * @param text the text that appears in the menu
     * @param additions an array of menubar paths of type @{link String}
     */
    public ContextAwareMenu(String text, String[] additions) {
        super(text);
        this.menubarPathAdditions = additions;
    }

    /**
     * This method returns an array of menubar paths for extended menus and/or menu items.
     * @return array of strings of the menu path additions that is non-null.
     */
    public final String[] getExtensionMenubarPaths() {
        return menubarPathAdditions;
    }
    
    /**
     * This method returns a list of @{link {@link MenuSection} defined in this {@link ContextAwareMenu}.
     * @return a {@link List} of non-null sections
     */
    public List<MenuSection> getMenuSections() {
        return sections == null ? Collections.<MenuSection>emptyList() :  Collections.unmodifiableList(sections);
    }
    
    /**
     * This method associates a collection of {@link MenuItemInfo} to the menubarPath. 
     * @param menubarPath the menubar path
     * @param infos a {@link Collection} of {@link MenuItemInfo}
     */
    public final void addMenuItemInfos(String menubarPath, Collection<MenuItemInfo> infos) {
        if (map == null)
            map = new HashMap<String, MenuSection>();
        
        if (sections == null)
            sections = new ArrayList<MenuSection>();
        
        MenuSection section = map.get(menubarPath);
        if (section == null) {
            section = new MenuSection(menubarPath);
            map.put(menubarPath, section);
            sections.add(section);
        }

        for (MenuItemInfo info : infos)
            section.addMenuItemInfo(info);
    }

    /**
     * Given an {@link ActionContext}, this method determines the availability 
     * (i.e., either shown or hidden) of this menu.
     * @param context the context
     * @return a boolean that indicates the menu's visibility. Returns false by default.
     */
    public boolean canHandle(ActionContext context) {
        return false;
    }
    
    /**
     * This method initializes the {@link MenuSection}s and {@link MenuItemInfo}s 
     * defined in this {@link ContextAwareMenu} by invoking {@link #populate()}, which is 
     * overridden by subclasses of {@link ContextAwareMenu}.
     */
    public final void initialize() {
        if (sections != null)
            return;
        
        populate();        
    }
    
    /**
     * This method is overridden by subclasses and should invoke 
     * {@link #addMenuItemInfos(String, Collection)} that associates a collection of 
     * {@link MenuItemInfo} to a menubar path.. 
     */
    protected abstract void populate();    
    
}
