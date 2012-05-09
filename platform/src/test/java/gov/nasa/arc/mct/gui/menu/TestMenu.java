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
package gov.nasa.arc.mct.gui.menu;

import java.util.Arrays;

import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ContextAwareMenu;
import gov.nasa.arc.mct.gui.MenuItemInfo;
import gov.nasa.arc.mct.gui.MenuItemInfo.MenuItemType;

/**
 * Dynamic menu test. This class creates the following menu:
 * 
 * Test -> Do This
 *         Do That
 *         Don't do this yet
 *         Do Extra
 * 
 * @author nshi
 *
 */
@SuppressWarnings("serial")
public class TestMenu extends ContextAwareMenu {

	private static final String TEST_ALL_EXT = "test/all.ext";
    private static final String TEST_ADDITIONS = "test/additions";

    public TestMenu() {
		super("Test", new String[] {TEST_ADDITIONS});		
	}
	
	/**
	 * For automated GUI testing.
	 */
	@Override
	protected void fireMenuSelected() {
		super.fireMenuSelected();
	}

	/**
	 * For automated GUI testing.
	 */
	@Override
	protected void fireMenuDeselected() {
		super.fireMenuDeselected();
	}

    @Override
    protected void populate() {
        addMenuItemInfos(TEST_ALL_EXT, Arrays.asList(
                new MenuItemInfo("DO_THIS", MenuItemType.NORMAL),
                new MenuItemInfo("DO_THAT", MenuItemType.NORMAL),
                new MenuItemInfo("DO_THESE", MenuItemType.RADIO_GROUP),
                new MenuItemInfo("DONT_DO_THIS_YET", MenuItemType.NORMAL),
                new MenuItemInfo("TEST_SUBMENU", MenuItemType.SUBMENU)));        
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        return true;
    }

}
