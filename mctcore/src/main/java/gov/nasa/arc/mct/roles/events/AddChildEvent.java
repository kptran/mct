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
 * UpdateEvent.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.roles.events;

import gov.nasa.arc.mct.components.AbstractComponent;

import java.awt.Point;
import java.util.EventObject;

/**
 * Implements an event that is generated when a child component is added.
 */
@SuppressWarnings("serial")
public class AddChildEvent extends EventObject {
    private AbstractComponent child;
    private int childIndex;
    private Point location = null;

    /**
     * Creates an add child event with the given component as the event source,
     * and the newly added child component.
     * 
     * @param source the component that triggered the event
     * @param child the new child component
     */
    public AddChildEvent(Object source, AbstractComponent child) {
        this(source, child, -1);
    }

    /**
     * Creates an add child event with the component that is the event source,
     * the new child component, and the point within the parent canvas area
     * where the child manifestation should reside.
     *  
     * @param source the component that is the source of the event
     * @param child the new child component that is being added
     * @param loc the location within the canvas area of the parent component
     */
    public AddChildEvent(Object source, AbstractComponent child, Point loc) {
        this(source, child, -1);
        location = loc;
    }

    /**
     * Creates an add child event with the component that is the event source,
     * the new child component, and the point within the parent canvas area
     * where the child manifestation should reside.
     *  
     * @param source the component that is the source of the event
     * @param child the new child component that is being added
     * @param childIndex the location within the children where the child was added
     */
    public AddChildEvent(Object source, AbstractComponent child, int childIndex) {
        super(source);
        this.child = child;
        this.childIndex = childIndex;
    }

    /**
     * Gets the new child component that as been added.
     * 
     * @return the new child component
     */
    public final AbstractComponent getChildComponent() {
        return this.child;
    }

    /**
     * Get the location in the parent canvas area here the new child
     * component should be added.
     * 
     * @return the point at which to add the new child
     */
    public final Point getLocation() {
        return this.location ;
    }

    /**
     * Gets the index within existing children where the child was added,
     * or -1 to add at the end.
     * 
     * @return the index within the children where to add the new child
     */
    public int getChildIndex() {
        return childIndex;
    }
    
}
