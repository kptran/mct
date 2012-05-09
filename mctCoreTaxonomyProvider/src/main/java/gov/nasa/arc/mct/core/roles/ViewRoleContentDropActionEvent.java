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
package gov.nasa.arc.mct.core.roles;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.View;

import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.util.Collection;
import java.util.EventObject;

/**
 * Implements an event which represents a drop action within the
 * content area.
 *
 * @author nshi
 *
 */
@SuppressWarnings("serial")
public class ViewRoleContentDropActionEvent extends EventObject {
    
    private Container container;
    private Window window;
    private AbstractComponent target;
    private Collection<AbstractComponent> sources;
    private Point cursorLocation;
    private View targetViewManifestation;

    /**
     * Creates an event corresponding to a drop on the target
     * component within a container.
     *  
     * @param container the container component for the target
     * @param window the window containing the target component
     * @param target the target component
     * @param sources the source components being dropped
     * @param viewManifestation the view manifestation being dropped upon
     */
    public ViewRoleContentDropActionEvent(Container container, Window window, AbstractComponent target, Collection<AbstractComponent> sources, View viewManifestation) {
        super(container);
        this.container = container;
        this.window = window;
        this.target = target;
        this.sources = sources;
        this.targetViewManifestation = viewManifestation;
    }

    /**
     * Creates an event corresponding to a drop on the target
     * component within a container at a particular location
     * within the target view manifestation.
     *  
     * @param container the container component for the target
     * @param window the window containing the target component
     * @param target the target component
     * @param sources the source components being dropped
     * @param location the location at which the drop occurrred
     * @param viewManifestation the view manifestation being dropped upon
     */
    public ViewRoleContentDropActionEvent(Container container, Window window, AbstractComponent target,
            Collection<AbstractComponent> sources, Point location, View viewManifestation) {
        this(container, window, target, sources, viewManifestation);
        cursorLocation = location;
    }

    /**
     * Gets the container component in which the drop occurs.
     * 
     * @return the container component
     */
    public Container getContainer() {
        return container;
    }
    
    /**
     * Gets the housing window in which the drop occurs.
     * 
     * @return the housing window
     */
    public Window getHousingWindow() {
        return window;
    }

    /**
     * Gets the target component upon which the drop occurs.
     * 
     * @return the target comoponent
     */
    public AbstractComponent getTarget() {
        return target;
    }

    /**
     * Gets the source components that are being dropped.
     * 
     * @return the source components
     */
    public Collection<AbstractComponent> getSources() {
        return sources;
    }

    /**
     * Gets the location within the target view manifestation
     * where the drop occurs.
     * 
     * @return the target location for the drop
     */
    public Point getLocation() {
        return cursorLocation;
    }
    
    /**
     * Gets the target view manifestation onto which the source
     * components are dropped.
     * 
     * @return the target view manifestation
     */
    public View getTargetManifestation() {
        return this.targetViewManifestation;
    }

}
