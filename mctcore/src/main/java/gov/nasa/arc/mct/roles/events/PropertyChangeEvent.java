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

import java.util.HashMap;
import java.util.Map;

/**
 * Implements an event indicating that a property has
 * changed value.
 */
public class PropertyChangeEvent {

    /**
     * The event property indicating the display name of the property
     * that has changed.
     */
    public static final String DISPLAY_NAME = "DISPLAY_NAME";

    /**
     * Indicates that the title of a panel containing the component has changed.
     */
    public static final String PANEL_TITLE = "PANEL_TITLE";

    /** The owner property has changed. */
    public static final String OWNER = null;
    
    private AbstractComponent component;
    private Map<String, Object> properties = new HashMap<String, Object>();
    
    /**
     * Creates a property change event for the given component.
     * 
     * @param component the component for which the property change event has occurred
     */
    public PropertyChangeEvent(AbstractComponent component) {
        this.component = component;
    }
    
    /**
     * Gets the component whose property has changed value.
     * 
     * @return the component with the property change
     */
    public AbstractComponent getComponent() {
        return component;
    }
    
    /**
     * Sets a property on the event.
     * 
     * @param key the property name
     * @param value the new property value
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * Gets a property on the event.
     * 
     * @param key the property name
     * @return the value of the property
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

}
