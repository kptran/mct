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
package gov.nasa.jsc.mct.executable.buttons;

import gov.nasa.arc.mct.components.ExtendedProperties;
import gov.nasa.arc.mct.util.StandardComboBoxColors;
import gov.nasa.jsc.mct.executable.buttons.view.ExecutableButtonManifestation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExecutableButtonSettings {

	private ExecutableButtonManifestation manifestation;
	private Map<String, Color> colorMap;
	private Map<Object, String> reverseMap;	
	private Map<String, String> defaultSettingsMap;
	private StandardComboBoxColors stdComboBoxColors;
	
	public static final int LABEL_TEXT_SIZE = 20;
	
	public ExecutableButtonSettings (ExecutableButtonManifestation manifestation) {
		this.manifestation = manifestation;
		initializeStandardComboBoxColors();
		reverseMap = new HashMap<Object, String>();
		addToReverseMap(colorMap);
	}	

	public void initializeStandardComboBoxColors() {
		stdComboBoxColors = new StandardComboBoxColors();
		colorMap = stdComboBoxColors.getColorMap();
		defaultSettingsMap = stdComboBoxColors.getDefaultSettingsMap();
	}
	
	private void addToReverseMap(Map<String, ?> map) {
		for (Entry<String, ?> e : map.entrySet()) {
			reverseMap.put(e.getValue(), e.getKey());
		}		
	}
	
	public List<Color> getSavedColors() {
		List<Color> colors = new ArrayList<Color>();
		colors.add((Color) getSetting(StandardComboBoxColors.BACKGROUND_COLOR));
		colors.add((Color) getSetting(StandardComboBoxColors.FOREGROUND_COLOR));
		return colors;
	}
	
	public void updateManifestation() {
		manifestation.buildFromSettings();
		manifestation.getManifestedComponent().save(manifestation.getInfo());
	}		

	public Object getSetting(String name) {
        String choice = getProps(name);
        if (colorMap.containsKey(choice)) {
            return colorMap.get(choice);
        } else { 
            return choice;
        }
    }
	
	public boolean isValidKey(String key) {
		return defaultSettingsMap.containsKey(key);
	}
	
	public String getProps(String key) {
		String value = manifestation.getViewProperties().getProperty(key, String.class);
		if (value == null) {
			if (!stdComboBoxColors.isValidKey(key)) { 
				return null;
			}
			setProps(key, defaultSettingsMap.get(key));
			value = defaultSettingsMap.get(key);
		}
		return value;
	}
	
	public void setProps(String key, String value) {
		ExtendedProperties viewProperties = manifestation.getViewProperties();
		viewProperties.setProperty(key, value);
	}


	public void setByObject (String key, Object value) {
		if (reverseMap.containsKey(value)) {
			setProps(key, reverseMap.get(value));
		} else if (value instanceof String) {
			setProps(key, (String) value);
		} 
	}
}
