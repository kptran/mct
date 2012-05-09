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
 * MCTMenuFactory.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.gui.menu;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.ActionManager;
import gov.nasa.arc.mct.gui.CompositeAction;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.ContextAwareMenu;
import gov.nasa.arc.mct.gui.GroupAction;
import gov.nasa.arc.mct.gui.GroupAction.RadioAction;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;
import gov.nasa.arc.mct.gui.MenuSection;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.gui.housing.MCTStandardHousing;
import gov.nasa.arc.mct.util.internal.ElapsedTimer;
import gov.nasa.arc.mct.util.logging.MCTLogger;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory class for creating menus based on the MCT look and feel.
 * 
 * @author nija.shi@nasa.gov
 */

public final class MenuFactory {
    private final static MCTLogger logger = MCTLogger.getLogger(MenuFactory.class);
    private static final MCTLogger PERF_LOGGER = MCTLogger.getLogger("gov.nasa.arc.mct.performance.menus");
    private static final Logger MENU_TOOLTIP_LOGGER = LoggerFactory.getLogger("gov.nasa.arc.mct.gui.menus");

    private MenuFactory() {
    }

    public static MCTStandardHousingMenuBar createStandardHousingMenuBar(MCTStandardHousing parentHousing) {
        assert parentHousing != null : "Should associate the menu bar with a non-null MCTStandardHousing";
        return new MCTStandardHousingMenuBar(parentHousing);
    }
    
    public static JPopupMenu createUserObjectPopupMenu(ActionContextImpl context) {
        
        MCTHousing activeHousing = context.getTargetHousing();
        if (activeHousing == null) {
            logMenuDiagnostics(context);
            return null;
        }
        MCTStandardHousingMenuBar mBar = createStandardHousingMenuBar((MCTStandardHousing) activeHousing);
        List<ContextAwareMenu> userObjectMenus = mBar.getUserObjectMenus(context);
        return createPopupMenu(context, userObjectMenus);
    }
    
   public static JPopupMenu createIconPopupMenu(ActionContextImpl context) {        
       ContextAwareMenu iconMenu = ActionManager.getMenu("Icon", context);
       return createPopupMenu(context, Collections.singletonList(iconMenu));
    }
   
    public static JPopupMenu createViewPopupMenu(ActionContextImpl context) {
        ContextAwareMenu viewMenu = ActionManager.getMenu("VIEW_MENU", context);
        return createPopupMenu(context, Collections.singletonList(viewMenu));
    }
    
    public static JPopupMenu createPopupMenu(ActionContextImpl context, List<ContextAwareMenu> menus) {
        JPopupMenu popupMenu = new JPopupMenu();
        for (int menuIndex = 0; menuIndex < menus.size(); menuIndex++) {
            ContextAwareMenu menu = menus.get(menuIndex);
            
            menu.initialize();

            if (menuIndex > 0)
                popupMenu.addSeparator();
            
            if (menu.canHandle(context)) {
                int lastPopulatedIndex = -1;
                List<MenuSection> sections = menu.getMenuSections();
                for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
                    MenuSection section = sections.get(sectionIndex);
                    List<MenuItemInfo> menuItemInfoList = section.getMenuItemInfoList();
                    
                    for (MenuItemInfo info : menuItemInfoList) {
                        MenuItemType type = info.getType();
                        if (type == MenuItemType.SUBMENU) {
                            ContextAwareMenu submenu = ActionManager.getMenu(info.getCommandKey(), context);
                            if (submenu == null) continue;
                            
                            // Add a separator between sections
                            if (lastPopulatedIndex >= 0 && lastPopulatedIndex < sectionIndex)
                                popupMenu.addSeparator();
                            lastPopulatedIndex = sectionIndex;
                            
                            popupMenu.add(submenu);
                        } else {
                            ContextAwareAction action = ActionManager.getAction(info.getCommandKey(), context);

                            if (action == null) continue;

                            action.putValue(Action.ACTION_COMMAND_KEY, info.getCommandKey());
                            
                            // Add a separator between sections
                            if (lastPopulatedIndex >= 0 && lastPopulatedIndex < sectionIndex)
                                popupMenu.addSeparator();
                            lastPopulatedIndex = sectionIndex;

                            switch (type) {
                            case NORMAL:
                                popupMenu.add(MenuFactory.createMCTMenuItem(action));
                                break;
                            case CHECKBOX:
                                popupMenu.add(MenuFactory.createMCTCheckBoxMenuItem(action));
                                break;
                            case RADIO_GROUP:
                                assert action instanceof GroupAction;

                                RadioAction[] radioButtonActions = ((GroupAction) action).getActions();
                                assert radioButtonActions != null;
                                
                                for (RadioAction radioButtonAction : radioButtonActions)
                                    popupMenu.add(MenuFactory.createMCTRadioButtonMenuItem(radioButtonAction));                                                                
                                break;
                            case COMPOSITE:
                                assert action instanceof CompositeAction;
                                Action[] subActions = ((CompositeAction) action).getActions();
                                assert subActions != null;
                                
                                for (Action subAction : subActions)
                                    popupMenu.add(MenuFactory.createMCTMenuItem(subAction));                                                                
                                break;                                
                            default:
                                break;
                            }
                        }
                    }
                }
            }
        }

        return popupMenu;
    	
    }
    
    private static void logMenuDiagnostics(ActionContextImpl context) {
        Set<JComponent> views = context.getAllTargetViewComponent();
        StringBuffer supplementInfo = new StringBuffer();
        for (JComponent view : views) {
            View viewManif = (View) view;
            AbstractComponent comp = viewManif.getManifestedComponent();
            supplementInfo.append("Component name = " + comp.getDisplayName() + ", id = "
                    + comp.getId() + ".  ");
        }
        if (context.getTargetHousing() == null) {
            supplementInfo.append("\n\n --- Context's housing object is NULL.");
        }
        Exception exc = new Exception();
        logger.warn("Could not create a menu. Related context info = " + supplementInfo + "\n\n", exc);
    }
    
    public static JMenuItem createMCTMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void fireActionPerformed(ActionEvent event) {
                final ElapsedTimer timer = new ElapsedTimer();
                timer.startInterval();
                super.fireActionPerformed(event);
                timer.stopInterval();
                PERF_LOGGER.debug("time to execute menu item {1} {0}", timer.getIntervalInMillis(), getText());
            }
            
        };
        item.putClientProperty("MODE", "DYNAMIC");
        setActionGUIProperties(item, action);
        return item;        
    }
    
    public static JMenuItem createMCTCheckboxMenuItem(String text, String commandKey) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(text);
        item.putClientProperty("MODE", "DYNAMIC");
        item.setActionCommand(commandKey);
        item.setEnabled(false);
        return item;
    }
    
    /**
     * 
     * @return true if the boolean action is mixed mode
     */
    private static boolean isMixedMode(Action action) {
        return action.getValue(Action.SELECTED_KEY) == null;
    }
    
    public static JMenuItem createMCTCheckBoxMenuItem(Action action) {
        JMenuItem item;
        if (isMixedMode(action)) {
            // mixed mode, use the mixed mode indicator
            item = new JMenuItem(action);
            item.setIcon(new MixedModeCheckBoxIcon());
        } else {
            item = new JCheckBoxMenuItem(action);
        }
        item.putClientProperty("MODE", "DYNAMIC");
        setActionGUIProperties(item, action);
        return item;
    }

    public static JMenuItem createMCTRadioButtonMenuItem(String text, String commandKey) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);
        item.putClientProperty("MODE", "DYNAMIC");
        item.setActionCommand(commandKey);
        item.setEnabled(false);
        return item;
    }

    public static JMenuItem createMCTRadioButtonMenuItem(GroupAction.RadioAction action) {
        JMenuItem item;
        if (action.isMixed()) {
            // mixed mode, use the mixed mode indicator
            item = new JMenuItem(action);
            item.setIcon(new MixedModeRadioButtonIcon());
        } else {
            item = new JRadioButtonMenuItem(action);
        }
        item.setSelected(action.isSelected());
        setActionGUIProperties(item, action);
        return item;
    }

    public static JMenuItem createMCTDynamicMenuItem(String text, String commandKey) {
        JMenuItem item = new JMenuItem(text);
        item.putClientProperty("MODE", "DYNAMIC");
        item.setActionCommand(commandKey);
        item.setEnabled(false);
        return item;        
    }
    
    public static void setActionGUIProperties(JMenuItem menuItem, Action action) {
        if (MENU_TOOLTIP_LOGGER.isDebugEnabled())
            menuItem.setToolTipText(action.getClass().getName());
        Object border = action.getValue(ContextAwareAction.BORDER);
        if (border != null && border instanceof Border) {
            menuItem.setBorder((Border) border);
            menuItem.setBorderPainted(true);
        }
        
        Object preferredSize = action.getValue(ContextAwareAction.PREFERRED_SIZE);
        if (preferredSize != null && preferredSize instanceof Dimension)
            menuItem.setPreferredSize((Dimension) preferredSize);   
    }
    
    /**
     * 
     * @param menu
     * @return a menu listener
     */
    public static MenuListener createMenuListener(final ContextAwareMenu menu) {
        return new MenuListener() {
            private int lastPopulatedIndex = -1;
            
            @Override
            public void menuCanceled(MenuEvent e) {
                menu.removeAll();
                lastPopulatedIndex = -1;
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                menu.removeAll();
                lastPopulatedIndex = -1;
            }

            private void addSeparatorIfNecessary(int currentSectionIndex) {
                // Add a separator between sections
                if (lastPopulatedIndex >= 0 && lastPopulatedIndex < currentSectionIndex)
                    menu.addSeparator();
                lastPopulatedIndex = currentSectionIndex;                
            }
            
            @Override
            public void menuSelected(MenuEvent e) {
                // Set action context.
                ActionContextImpl context = new ActionContextImpl();
                MCTHousing activeHousing = (MCTHousing) SwingUtilities.getAncestorOfClass(MCTHousing.class, menu);                
                if (activeHousing != null) {
                    // An active housing may be missing in headless mode.
                    View currentActiveManifestation = activeHousing.getCurrentManifestation();                    
                    context.setTargetComponent(currentActiveManifestation.getManifestedComponent());
                    context.setTargetHousing(activeHousing);
                    
                    // Add all selected manifestations to context.
                    for (View manifestation : activeHousing.getSelectionProvider().getSelectedManifestations())
                        context.addTargetViewComponent(manifestation);    
                }

                lastPopulatedIndex = -1;
                List<MenuSection> sections = menu.getMenuSections();
                for (int index = 0; index < sections.size(); index++) {
                    MenuSection section = sections.get(index);
                    List<MenuItemInfo> menuItemInfoList = section.getMenuItemInfoList();
                    
                    for (MenuItemInfo info : menuItemInfoList) {
                        MenuItemType type = info.getType();
                        if (type == MenuItemType.SUBMENU) {
                            ContextAwareMenu submenu = ActionManager.getMenu(info.getCommandKey(), context);
                            if (submenu == null) continue;

                            addSeparatorIfNecessary(index);
                            menu.add(submenu);
                        } else {
                            ContextAwareAction action = ActionManager.getAction(info.getCommandKey(), context);

                            if (action == null) continue;
                            
                            action.putValue(Action.ACTION_COMMAND_KEY, info.getCommandKey());
                            
                            addSeparatorIfNecessary(index);
                            
                            // Add menu item(s) by type
                            switch (type) {
                            case NORMAL:
                                menu.add(MenuFactory.createMCTMenuItem(action));
                                break;
                            case CHECKBOX:
                                menu.add(MenuFactory.createMCTCheckBoxMenuItem(action));
                                break;
                            case RADIO_GROUP:
                                assert action instanceof GroupAction;

                                RadioAction[] radioButtonActions = ((GroupAction) action).getActions();
                                assert radioButtonActions != null;
                                
                                for (RadioAction radioButtonAction : radioButtonActions)
                                    menu.add(MenuFactory.createMCTRadioButtonMenuItem(radioButtonAction));                                                                
                                break;
                            case COMPOSITE:
                                assert action instanceof CompositeAction;
                                Action[] subActions = ((CompositeAction) action).getActions();
                                assert subActions != null:"";
                                
                                for (Action subAction : subActions)
                                    menu.add(MenuFactory.createMCTMenuItem(subAction));                                                                
                                break;                                
                            default:
                                break;
                            }
                        }
                    }
                }
            }
        };
    }
}
