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
package gov.nasa.arc.mct.services.config.impl.properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Implements a property value loaded from a configuration file.
 */
@XmlRootElement(name="entry")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SimpleValue {

	private String value;

	/**
	 * Creates a new value object with an empty value. This
	 * method should only be called by JAXB.
	 */
	public SimpleValue() {
		// do nothing--used by JAXB
	}
	
	/**
	 * Creates a new property value with the given string representation.
	 * 
	 * @param value the string form of the property value
	 */
	public SimpleValue(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the string form of the property value.
	 * 
	 * @return the property value, as a string
	 */
	@XmlAttribute
	public String getValue() {
		return value;
	}

	/**
	 * Sets the string form of the property value.
	 * 
	 * @param value the new property value, as a string
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
