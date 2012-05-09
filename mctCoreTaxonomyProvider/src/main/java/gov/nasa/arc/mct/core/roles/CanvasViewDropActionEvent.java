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

/**
 * Implements an event generated when a component is dropped onto the
 * canvas view of another component, through Swing drag-and-drop.
 *
 * @author nshi
 *
 */
@SuppressWarnings("serial")
public class CanvasViewDropActionEvent extends ViewRoleContentDropActionEvent {

    /**
     * Creates a new event for a drop action in a canvas view manifestation.
     * 
     * @param container the container of the canvas view manifestation
     * @param window the containing window of the canvas view
     * @param target the target component where the drop occurred
     * @param sources the source components that have been dropped
     * @param viewManifestation the view manifestation of the taret component
     *   onto which the sources were dropped
     */
    public CanvasViewDropActionEvent(Container container, Window window, AbstractComponent target, Collection<AbstractComponent> sources, View viewManifestation) {
        super(container, window, target, sources, viewManifestation);
    }

    /**
     * Creates a new event for a drop action in a canvas view manifestation.
     * Also specifies the cursor location within the canvas view where the
     * drop occurred.
     * 
     * @param container the container of the canvas view manifestation
     * @param window the containing window of the canvas view
     * @param target the target component where the drop occurred
     * @param sources the source components that have been dropped
     * @param location the location within the canvas view where the drop occurred
     * @param viewManifestation the view manifestation of the taret component
     *   onto which the sources were dropped
     */
    public CanvasViewDropActionEvent(Container container, Window window, AbstractComponent target,
            Collection<AbstractComponent> sources, Point location, View viewManifestation) {
        super(container, window, target, sources, location, viewManifestation);
    }

}
