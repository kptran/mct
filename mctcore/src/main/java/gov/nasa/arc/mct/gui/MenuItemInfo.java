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
/**
 * 
 */
package gov.nasa.arc.mct.gui;


/**
 * This class consists of the information necessary for creating a menu item in MCT. 
 * Note that this class is immutable.
 * @author nija.shi@nasa.gov
 */
public final class MenuItemInfo {
    
    /**
     * MCT-supported menu item types.
     */
    public enum MenuItemType {
        /** Renders the item as a <code>JMenuItem</code>. */
        NORMAL,
        /** Renders the item as a <code>JMenu</code>. */
        SUBMENU,
        /** Renders the item as a <code>JCheckBoxMenuItem</code>. */
        CHECKBOX,
        /** Renders the item as a set of <code>JRadioButtonMenuItems</code>. */
        RADIO_GROUP,
        /** Renders the item as a set of <code>JMenuItems</code>. */
        COMPOSITE;
    }

    private String menubarPath;
    private String commandKey;
    private MenuItemType type;
    private Class<? extends ContextAwareAction> actionClass;
    private Class<? extends ContextAwareMenu> menuClass;

    /**
     * Create menu item information for the given command  key and {@link MenuItemType}.
     * <p>
     * This constructor is used when:
     * <ul>
     * <li> a menubar path is later provided in {@link ContextAwareMenu#addMenuItemInfos(String, java.util.Collection)}
     * <li>the actual {@link Class} instance of the {@link ContextAwareAction} is later associated.
     * </ul>
     * <p>
     * <b>Warning:</b> this method is only internally used. 
     * @param commandKey the action command key
     * @param type the type of the menu item
     */    
    public MenuItemInfo(String commandKey, MenuItemType type) {
        this.commandKey = commandKey;
        this.type = type;
    }
    
    /**
     * The constructor that takes a menubar path, command key, {@link MenuItemType}, 
     * and the {@link Class} of a {@link ContextAwareAction}.
     * @param menubarPath the menubar path of type {@link String}
     * @param commandKey the command key of type {@link String}
     * @param type the menu item type of type {@link MenuItemType}
     * @param actionClass the class instance of a {@link ContextAwareAction}
     */
    public MenuItemInfo(String menubarPath, String commandKey, MenuItemType type, Class<? extends ContextAwareAction> actionClass) {
        this(commandKey, type);
        this.menubarPath = menubarPath;
        this.actionClass = actionClass;
    }
    
    /**
     * The constructor that takes a menubar path, command key,  
     * and the {@link Class} of a {@link ContextAwareMenu}.
     * @param menubarPath the menubar path of type {@link String}
     * @param commandKey the command key of type {@link String}
     * @param menuClass the class instance of a {@link ContextAwareMenu}
     */
    public MenuItemInfo(String menubarPath, String commandKey, Class<? extends ContextAwareMenu> menuClass) {
        this(commandKey, MenuItemType.SUBMENU);
        this.menubarPath = menubarPath;
        this.menuClass = menuClass;
    }

    /**
     * This method returns the menubar path of this {@link MenuItemInfo}.
     * @return the menubar path of type {@link String} 
     */
    public String getMenubarPath() {
        return menubarPath;
    }
    
    /**
     * This method returns the command associated with this {@link MenuItemInfo}.
     * @return the command key of type {@link String}
     */
    public String getCommandKey() {
        return commandKey;            
    }

    /**
     * This method returns the type of this {@link MenuItemInfo}.
     * @return the menu item type of type {@link MenuItemType}
     */
    public MenuItemType getType() {
        return type;
    }
    
    /**
     * This method returns the associated {@link Class} instance of a
     * {@link ContextAwareAction}.
     * @return a {@link Class} instance of the associated {@link ContextAwareAction}
     */
    public Class<? extends ContextAwareAction> getActionClass() {
        return actionClass;        
    }

    /**
     * This method returns the associated {@link Class} instance of a
     * {@link ContextAwareMenu}.
     * @return a {@link Class} instance of the associated {@link ContextAwareMenu}
     */
    public Class<? extends ContextAwareMenu> getMenuClass() {
        return menuClass;        
    }

}