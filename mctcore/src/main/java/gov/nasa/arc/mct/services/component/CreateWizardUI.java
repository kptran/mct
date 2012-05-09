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
package gov.nasa.arc.mct.services.component;


import gov.nasa.arc.mct.components.AbstractComponent;

import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * This class provides a wizard for setting up a customizable UI to take in necessary
 * parameters to create an instance of a new component.
 * 
 * @author srlin1
 *
 */
public abstract class CreateWizardUI {

    /**
     * Get the UI displayed in the dialog box when creating a new component through the Create menu.
     * @param create button to enable or disable creation according to component naming policies
     * @return JComponent UI to be displayed
     */
    public abstract JComponent getUI (JButton create);
    
    /**
     * Creates new instance of a specified component object.
     * @param comp ComponentRegistry to create new instance of component object
     * @param parentComp parent component of new component
     * @return the new component object
     */
    public abstract AbstractComponent createComp (ComponentRegistry comp, AbstractComponent parentComp);

}
