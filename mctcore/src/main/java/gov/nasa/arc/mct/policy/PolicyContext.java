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
package gov.nasa.arc.mct.policy;

import java.util.HashMap;
import java.util.Map;


/**
 * Defines the context for policy execution. A <code>PolicyContext</code> contains a 
 * property list that maps a property name to an <code>Object</code>.
 * @author nija.shi@nasa.gov
 */
public final class PolicyContext {
    
    private Map<String, Object> dictionary = new HashMap<String, Object>();
    
    /**
     * Defines the property list for the <code>PolicyContext</code>. 
     * @author nija.shi@nasa.gov
     */
    public enum PropertyName {
        /**
         * The property key to get the target {@link gov.nasa.arc.mct.components.AbstractComponent AbstractComponent}.
         */
        TARGET_COMPONENT ("TARGET"),
        /**
         * The property key to get a {@link java.util.Collection Collection} of {@link gov.nasa.arc.mct.components.AbstractComponent AbstractComponent}s.
         * This property key is typically used validating a drag n' drop of
         * multiple {@link gov.nasa.arc.mct.components.AbstractComponent}s.
         */
        SOURCE_COMPONENTS ("SOURCES"),
        /**
         * The property to get the action code of type {@link Character}.
         */
        ACTION ("ACTION"),
        /**
         * The property to get the {@link gov.nasa.arc.mct.gui.ViewProvider}.
         */
        VIEW_MANIFESTATION_PROVIDER ("VIEW_MANIFESTATION_PROVIDER"),
        /**
         * The property to get the <code>ViewType</code> requested.
         */
        VIEW_TYPE("VIEW_TYPE"),
        /**
         * The property to get the <code>ViewInfo</code>.
         */
        TARGET_VIEW_INFO("TARGET_VIEW_INFO");
        
        private String name;
        private PropertyName(String name) {
            this.name = name;
        }
        
        /**
         * Returns the String of this property key.
         * @return the name of this property key
         */
        public String getName() {
            return name;
        }
    }
    
    /**
     * Searches for the property with the specified key.
     * @param key the property key
     * @return the property value with the specified key value
     */
    public Object getProperty(String key) {
        return dictionary.get(key);
    }

    /**
     * Searches for the property with the specified key. If the key 
     * is found, the method then tries to cast the value to 
     * <code>expectedType</code>.
     * @param <T> the type of the class modeled by this method
     * @param key the property key
     * @param expectedType the expected type for this property value
     * @return the property value; return null if the property is not 
     * found or the property value cannot be cast to 
     * <code>expectedType</code>
     */
    public <T> T getProperty(String key, Class<T> expectedType) {
        return getProperty(key, expectedType, null);
    }

    /**
     * Searches for the property with the specified key. If the key is 
     * not found, the method returns the default value argument.
     * @param <T> the type of the class modeled by this method
     * @param key the property key
     * @param expectedType the expected type
     * @param defaultValue the default value for this property
     * @return the property value with the specified key value
     */
    public <T> T getProperty(String key, Class<T> expectedType, T defaultValue) {
        Object value = dictionary.get(key);
        if (value == null)
            return defaultValue;
        
        T castValue = null;
        if (expectedType.isAssignableFrom(value.getClass())) {
            castValue = expectedType.cast(value);
        }
        return castValue;
    }

    /**
     * Sets the property.
     * @param key the property key
     * @param value the property value
     */
    public void setProperty(String key, Object value) {
        dictionary.put(key, value);
    }
}
