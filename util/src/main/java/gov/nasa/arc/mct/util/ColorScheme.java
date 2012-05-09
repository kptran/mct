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
package gov.nasa.arc.mct.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;


import javax.swing.UIManager;

/**
 * Serves user-specified colors to MCT through the UIManager.
 * 
 * 
 * @author vwoeltje
 *
 */
public class ColorScheme {
    private List<ColorProperty> propertyList 
        = new ArrayList<ColorProperty>();
    
    /**
     * Color scheme constructor.
     */
    public ColorScheme() {
        
    }
    
    /**
     * Create a new color scheme from the provided properties.
     * @param properties a set of key/value pairs indicated name / color
     */
    public ColorScheme( Properties properties) {

        for (Entry<Object,Object> e : properties.entrySet()) {
            Object value = e.getValue();
            if (value instanceof String) {
                String v = ((String) value).trim();
                if (v.startsWith("#")) {
                    value = Color.decode(v);
                }
            }            
            propertyList.add(new ColorProperty((String) e.getKey(), value));
        }
    }
    
    /**
     * Get a color scheme of keys corresponding to the supplied prefix. 
     * If this ColorScheme has an entry "Thing.Panel.background", for instance,  
     * getColorSchemeFor("Thing") will return a scheme containing a corresponding 
     * "Panel.background" entry.
     * @param prefix the base word for the new color scheme
     * @return a new color scheme with prefixed entries promoted to a top level
     */
    public ColorScheme getColorSchemeFor(String prefix) {

    	ColorScheme cProps = new ColorScheme();
        for (ColorProperty cp : propertyList) {
            
            /* 
             * Matching properties go at the end of the list; non-matching properties go 
             * at the beginning. As such, "Label.background" goes before "Thing.Label.background" 
             * for getColorSchemeFor("Thing")
             */
            if (cp.matches(prefix)) {
                cProps.propertyList.add(new ColorProperty(cp.getKey(), cp.getValue()));
            } else {
                cProps.propertyList.add(0, cp);
            }
        }
        return cProps;        
    }
    
    /**
     * Sets color settings in the UIManager and calls a supplied 
     * routine with those settings in place. Then returns the UIManager 
     * to its original state before returning the called items result.
     *  
     * @param call a Callable containing the desired behavior
     * @return T the return value of the supplied call
     * @throws Exception as thrown by Callable
     * @param <T> - T type.
     */
    public <T> T callUnderColorScheme (Callable<T> call) throws Exception {
        Map<String, Object> oldEntries =
            new HashMap<String,Object>();
        T r = null;

        for (ColorProperty p : propertyList) {
            // Only update with non-generic properties
            if (!p.getFullKey().contains("*")) {
                String key = p.getFullKey();
                Object old = UIManager.get(key);
                // Don't overwrite old entries if something's defined twice
                if (!oldEntries.containsKey(key)) {
                    oldEntries.put(key, old);
                }
                UIManager.put(key, p.getValue());
            }
        }      
        

        try {
            r = call.call();
        } finally {        
            for (Entry<String, Object> e : oldEntries.entrySet()) {
                UIManager.put(e.getKey(), e.getValue());
            }
        }
           
        return r;
        
    }
    
    
    /**
     * Inserts all non-wildcard entries into the UIManager. Whereas 
     * callUnderColorScheme is available for short-term color scheme needs, 
     * this action will modify the color scheme for the remainder of execution.
     */
    public void applyColorScheme() {
        for (ColorProperty p : propertyList) {
            // Only update with non-generic properties
            if (!p.getFullKey().contains("*")) {
                String key = p.getFullKey();

                // Don't overwrite old entries if something's defined twice
                UIManager.put(key, p.getValue());
            }
        }          
    }
 
    private class ColorProperty {
        private Object value;
        private String key;
        private String prefix;
        
        public ColorProperty(String key, Object value) {
            if (!key.contains(".")) {
                this.value = value;
                this.key   = key;
                this.prefix = "";
            } else {
                this.value = value;
                int dot = key.indexOf(".");
                this.key   = key.substring(dot + 1);
                this.prefix = key.substring(0, dot);               
            }
        }
        
        public boolean matches(String prefix) {
            String regex = this.prefix.replaceAll("\\*", ".*");           
            return prefix.matches(regex);
        }
        
        public String getKey() {
            return key;
        }
        
        public Object getValue() {
            return value;
        }
        
        public String getFullKey() {
            if (prefix.isEmpty()) {
                return key;
            } else { 
                return prefix + "." + key;
            }
        }
        
    }

}
