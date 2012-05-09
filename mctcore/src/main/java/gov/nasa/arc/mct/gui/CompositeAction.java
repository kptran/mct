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

import javax.swing.Action;

/**
 * This class defines a composite action, which will be swapped during 
 * runtime by a set of actions that are dynamically populated. This type
 * of action is useful when the set of actions are context-sensitive
 * and can only be dynamically created. 
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public abstract class CompositeAction extends ContextAwareAction {
    
    private Action[] actions;

    /**
     * The constructor that takes a {@link String} paramater for the name of this action.
     * @param name the name of this action.
     */
    protected CompositeAction(String name) {
        super(name);
    }

    /**
     * This method returns a set of actions that will replace this {@link CompositeAction}.
     * @return an array of {@link Action}s.
     */
    public final Action[] getActions() {
        return actions;
    }
    
    /**
     * This method sets the set of actions to be replaced for this {@link CompositeAction}.
     * @param actions an array of {@link Action}s.
     */
    protected final void setActions(Action[] actions) {
        this.actions = actions;
    }
}
