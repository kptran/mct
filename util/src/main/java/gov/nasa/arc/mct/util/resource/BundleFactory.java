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
package gov.nasa.arc.mct.util.resource;

import java.util.ResourceBundle;

/**
 * A factory for getting resource bundles that will return either a plain
 * bundle or a pseudotranslated bundle, depending on the system configuration.
 * 
 * @author mrose
 *
 */
public class BundleFactory {
	
	/**
	 * Return a resource bundle of the given name, either unchanged or
	 * pseudotranslated, depending on the system configuration.
	 * 
	 * @param name the name of the bundle to find
	 * @return the bundle
	 * @throws MissingResourceException if the bundle cannot be found
	 */
	public static ResourceBundle getBundle(String name) {
		ResourceBundle baseBundle = ResourceBundle.getBundle(name);
		
		if (PseudoBundle.isEnabled()) {
			return new PseudoBundle(baseBundle);
		} else {
			return baseBundle;
		}
	}
}
