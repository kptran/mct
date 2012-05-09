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
import javax.xml.bind.annotation.XmlTransient;

/**
 * Implements a property for an OSGi service configuration.
 */
@XmlRootElement(name="property")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SimpleProperty {

	private String name;

	private String value;

	private String typeName;

	/**
	 * Creates a simple property with empty name, value, and type
	 * name. This method should only be called by JAXB.
	 */
	public SimpleProperty() {
		// do nothing--used by JAXB
	}
	
	/**
	 * Creates a simple property with a specified name and string value.
	 * 
	 * @param name the property name
	 * @param value the property value
	 */
	public SimpleProperty(String name, String value) {
		// We use null for the type name so that it won't be
		// serialized in the XML if we write out a SimpleProperty.
		this(name, value, null);
	}

	/**
	 * Creates a simple property with a specified name and string form
	 * of the value, with a specified value type. The string form of the
	 * value will be coverted to the indicated type.
	 * 
	 * @param name the property name
	 * @param value the string form of the property value
	 * @param type the property type name
	 */
	public SimpleProperty(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.typeName = type;
		
		// Make sure the type is from the allowed set.
		if (typeName != null) {
			SimpleType.getInstance().checkTypeName(type);
		}
	}

	/**
	 * Gets the name of the property.
	 * 
	 * @return the property name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the property.
	 * 
	 * @param name the new property name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the property type.
	 * 
	 * @return the property type
	 */
	@XmlAttribute(name="type")
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Sets the property type name. This method should
	 * only be called by JAXB.
	 * 
	 * @param typeName the new property type
	 */
	public void setTypeName(String typeName) {
		if (typeName != null) {
			SimpleType.getInstance().checkTypeName(typeName);
		}
		this.typeName = typeName;
	}
	
	/**
	 * Gets the string value of the property.
	 * 
	 * @return the property value, as a string
	 */
	@XmlAttribute
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the property value, as a string.
	 * 
	 * @param value the string form of the property value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlTransient
	private String getConversionTypeName() {
		if (typeName != null) {
			return typeName;
		} 
		return "String";
	}

	/**
	 * Gets the value of the property, converted to the
	 * property type.
	 * 
	 * @return the converted property value
	 */
	@XmlTransient
	public Object getConvertedValue() {
		return SimpleType.getInstance().convertValue(getConversionTypeName(), value);
	}

}
