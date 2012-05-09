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
package gov.nasa.arc.mct.gui.menu.housing;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.ContextAwareMenu;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;
import gov.nasa.arc.mct.components.DetectGraphicsDevices;
import gov.nasa.arc.mct.policy.ExecutionResult;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;

import java.util.Arrays;
import java.util.Collections;

/**
 * Objects Menu.
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public class ObjectsMenu extends ContextAwareMenu {
    private static final String OBJECTS_VIEW_EXT = "/objects/view.ext";
    private static final String OBJECTS_OPEN_EXT = "/objects/open.ext";
    private static final String OBJECTS_LOCKING_EXT = "/objects/locking.ext";
    private static final String OBJECTS_DELETION_EXT = "/objects/deletion.ext";
    private static final String OBJECTS_CREATION_EXT = "/objects/creation.ext";
    // Extensible section keys
    private static final String OBJECTS_FORMAT_ZORDER_EXT = "/objects/format.zorder.ext";
    private static final String OBJECTS_FORMAT_PANEL_EXT = "/objects/format.panel.ext";
    private static final String OBJECTS_ADDITIONS = "/objects/additions";
    
    public ObjectsMenu() {
        super("Objects", new String[]{OBJECTS_CREATION_EXT, OBJECTS_ADDITIONS});        
    }
        
    @Override
    public boolean canHandle(ActionContext context) {
        ActionContextImpl actionContext = (ActionContextImpl) context;
        AbstractComponent rootComponent = actionContext.getTargetHousing().getRootComponent();
        PolicyContext policyContext = new PolicyContext();
        policyContext.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), rootComponent);
        policyContext.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'r');
        String inspectionKey = PolicyInfo.CategoryType.OBJECT_INSPECTION_POLICY_CATEGORY.getKey();
        ExecutionResult result = PolicyManagerImpl.getInstance().execute(inspectionKey, policyContext);
        return result.getStatus();
    }

    @Override
    protected void populate() {
        // OPEN
        addMenuItemInfos(OBJECTS_OPEN_EXT, Arrays.asList(
                new MenuItemInfo("OBJECTS_OPEN", MenuItemType.NORMAL),
                new MenuItemInfo(DetectGraphicsDevices.OBJECTS_OPEN_MULTIPLE_MONITORS_MENU, MenuItemType.SUBMENU)
                ));
        
     
        // VIEW OPTIONS
        addMenuItemInfos(OBJECTS_VIEW_EXT, Collections.<MenuItemInfo>singleton(new MenuItemInfo("OBJECTS_CHANGE_VIEWS", MenuItemType.RADIO_GROUP)));
        
        // FORMAT Z-ORDER
        addMenuItemInfos(OBJECTS_FORMAT_ZORDER_EXT, Arrays.asList(
                new MenuItemInfo("OBJECTS_FORMATTING_BRING_TO_FRONT", MenuItemType.NORMAL),
                new MenuItemInfo("OBJECTS_FORMATTING_SEND_TO_BACK", MenuItemType.NORMAL)));
        
        // FORMAT PANEL
        addMenuItemInfos(OBJECTS_FORMAT_PANEL_EXT, Arrays.asList(
                new MenuItemInfo("OBJECTS_ALIGNMENT", MenuItemType.SUBMENU),
                new MenuItemInfo("OBJECTS_BORDERS", MenuItemType.SUBMENU),
                new MenuItemInfo("OBJECTS_BORDER_STYLES", MenuItemType.SUBMENU),
                new MenuItemInfo("OBJECTS_PANEL_TITLE_BAR", MenuItemType.CHECKBOX),
                new MenuItemInfo("OBJECTS_COLUMN_HEADER", MenuItemType.CHECKBOX),
                new MenuItemInfo("OBJECTS_ROW_HEADER", MenuItemType.CHECKBOX)));
        
        // DEPRECATED
        addMenuItemInfos(OBJECTS_FORMAT_PANEL_EXT, Collections.<MenuItemInfo>singleton(new MenuItemInfo("OBJECTS_TOGGLE_BORDERS", MenuItemType.CHECKBOX)));
        
        // CREATION
        addMenuItemInfos(OBJECTS_CREATION_EXT, Arrays.asList(
                new MenuItemInfo("OBJECTS_NEW_MENU", MenuItemType.SUBMENU),
                new MenuItemInfo("OBJECTS_DUPLICATE", MenuItemType.NORMAL),
                new MenuItemInfo("OBJECTS_PLACE_OBJS_IN_COLLECTION", MenuItemType.NORMAL)));
        
        // DELETION
        addMenuItemInfos(OBJECTS_DELETION_EXT, Arrays.asList(
                new MenuItemInfo("OBJECTS_REMOVE_MANIFESTATION", MenuItemType.NORMAL),
                new MenuItemInfo("DELETE_OBJECTS", MenuItemType.NORMAL)));
        
        // LOCKING
        addMenuItemInfos(OBJECTS_LOCKING_EXT, Arrays.asList(
                new MenuItemInfo("OBJECTS_LOCK_MANIFESTATION", MenuItemType.CHECKBOX),
                new MenuItemInfo("OBJECTS_COMMIT", MenuItemType.NORMAL)));        
    }
}
