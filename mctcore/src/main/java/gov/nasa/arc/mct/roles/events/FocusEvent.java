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
package gov.nasa.arc.mct.roles.events;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.util.Collection;
import java.util.EventObject;

/**
 * This event targets extended MCTGUIComponent for tree nodes.
 * Typically, an MCTGUIComponent (that encapsulates a tree node) responds to this event
 * and provides implementation to set selection on its child tree nodes.
 * 
 * @author nshi
 *
 */
@SuppressWarnings("serial")
public class FocusEvent extends EventObject {

    private Collection<AbstractComponent> focusCompoents;
    
    /**
     * Creates a new focus event with the given source object and the
     * collection of components that are gaining the focus.
     * 
     * @param source the source object, usually the object that has been clicked on
     * @param focusComponents the set of components gaining the focus
     */
    public FocusEvent(Object source, Collection<AbstractComponent> focusComponents) {
        super(source);
        this.focusCompoents = focusComponents;
    }
    
    /**
     * Gets the collection of components gaining the focus.
     * 
     * @return the components gaining the focus
     */
    public Collection<AbstractComponent> getFocusComponents() {
        return focusCompoents;
    }
}
