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

import javax.swing.AbstractAction;


/**
 * This class defines an action for a menu item group. This action is swapped during runtime 
 * by a set of dynamically populated actions that are rendered as {@link javax.swing.JRadioButtonMenuItem}s, 
 * in which only one menu item in the group can be selected. This type of action is useful when 
 * the set of actions are context-sensitive and can only be dynamically created. 
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public abstract class GroupAction extends ContextAwareAction {
    private RadioAction[] actions;
    
    /**
     * The constructor that takes a name for this action.
     * @param name of type {@link String}
     */
    protected GroupAction(String name) {
        super(name);
    }
    
    /**
     * This method returns a set of actions to be swapped for this {@link GroupAction}.
     * @return an array of {@link RadioAction}
     */
    public final RadioAction[] getActions() {
        return actions;
    }
    
    /**
     * This method sets the {@link RadioAction}s to be swapped. 
     * @param actions the array of {@link RadioAction}.
     */
    protected final void setActions(RadioAction[] actions) {
        this.actions = actions;
    }
    
    /**
     * This abstract class defines the action contained in a {@link GroupAction}. 
     * @author nija.shi@nasa.gov
     */
    public static abstract class RadioAction extends AbstractAction {
        /**
         * Tests whether this radio action is in the mixed state. The mixed state is shown
         * when multiple objects are selected but they have different boolean values. This 
         * signals to the user they values are different but selecting this action will make them
         * the same. 
         * 
         * @return true if the mixed state should be shown for this radio button, false otherwise;
         */
        public abstract boolean isMixed();
        
        /**
         * Tests whether or not this radio action is selected.
         * 
         * @return true, if this radio action is selected
         */
        public abstract boolean isSelected();
        
    }
}
