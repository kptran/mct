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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;

/**
 * This class defines the super class for actions used in MCT. Subclasses of this type of actions 
 * are context-aware, meaning that the availability and visibility of this action depends on the 
 * {@link ActionContext} being passed in.  
 * @author nija.shi@nasa.gov
 */
@SuppressWarnings("serial")
public abstract class ContextAwareAction extends AbstractAction {
    
    /**
     * Used in the client property to indicate border style for this action when appeared as a menu item.
     */
    public static final String BORDER = "Border"; // Store value of type javax.swing.Border

    /**
     * Used in the client property to indicate the preferred size for this action when appeared as a menu item.
     */
    public static final String PREFERRED_SIZE = "Preferred Size"; // Store value of type java.awt.Dimension

    /**
     * The constructor that takes a {@link String} parameter for the name of this action.
     * @param name the name of this action
     */
    protected ContextAwareAction(String name) {
        super();
        putValue(Action.NAME, name);
        putValue(BORDER, BorderFactory.createEmptyBorder());
    }        

    /**
     * Given the {@link ActionContext}, this method determines the availability 
     * (i.e., either shown or hidden) of this action when appearing as a menu item.
     * @param context the {@link ActionContext}
     * @return a boolean that indicates the availability of the action
     */
    public abstract boolean canHandle(ActionContext context);

    /**
     * This method determines the visibility (i.e., is enabled or disabled) of 
     * this action when appeared as a menu item.
     * @return a boolean that indicates the availability of the action
     */
    @Override
    public abstract boolean isEnabled();
    
    /**
     * This method should be overridden with the implementation that performs this action.
     * @param e the {@link ActionEvent}
     */
    @Override
    public abstract void actionPerformed(ActionEvent e);
}
