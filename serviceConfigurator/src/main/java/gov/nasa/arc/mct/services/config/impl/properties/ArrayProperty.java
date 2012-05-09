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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Implements the persistent form of an array property, a possible
 * part of the configuration for an OSGi service.
 */
@XmlRootElement(name="array")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ArrayProperty {

	private String name;

	private List<SimpleValue> entries = new ArrayList<SimpleValue>();

	private String typeName;

	/**
	 * Creates a new array property with no name or type name.
	 * This constructor is used only by JAXB.
	 */
	public ArrayProperty() {
		// do nothing--used by JAXB
	}
	
	/**
	 * Creates a new array property with the given name.
	 * 
	 * @param propertyName the property name
	 */
	public ArrayProperty(String propertyName) {
		// We use null for the type name so that it won't be
		// serialized in the XML if we write out a SimpleProperty.
		this(propertyName, null);
	}

	/**
	 * Creates a new array property with a given name and type.
	 * 
	 * @param propertyName the name of the property
	 * @param propertyType the type of each array element
	 */
	public ArrayProperty(String propertyName, String propertyType) {
		name = propertyName;
		typeName = propertyType;
		
		// Make sure the type is from the allowed set.
		if (propertyType != null) {
			SimpleType.getInstance().checkTypeName(propertyType);
		}
	}

	/**
	 * Gets the name of the array property.
	 * 
	 * @return the property name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the type of the property, as a string.
	 * 
	 * @return the type name of the property
	 */
	@XmlAttribute(name="type")
	public String getTypeName() {
		return typeName;
	}
	
	/**
	 * Adds a new array element to the property.
	 * 
	 * @param entry the new array element
	 */
	public void addEntry(String entry) {
		entries.add(new SimpleValue(entry));
	}

	@XmlTransient
	private String getConversionTypeName() {
		if (typeName != null) {
			return typeName;
		} 
		return "String";
	}

	/**
	 * Gets an array with the entries of the array property.
	 *  
	 * @return a new array with the entries of the array property
	 */
	public Object[] asArray() {
		Class<? extends Object> clazz = SimpleType.getInstance().getTypeForName(getConversionTypeName());
		
		Object[] result = (Object[]) Array.newInstance(clazz, entries.size());
		for (int i = 0; i < result.length; i++) {
			result[i] = SimpleType.getInstance().convertValue(getConversionTypeName(), entries.get(i).getValue());
		}
		
		return result;
	}

	/**
	 * Gets the array entries as a list.
	 * 
	 * @return a list of the array entries
	 */
	@XmlElement(name="entry")
	public List<SimpleValue> getEntries() {
		return entries;
	}

	/**
	 * Sets the array elements.
	 * 
	 * @param entries a list of values to become the array elements
	 */
	public void setEntries(List<SimpleValue> entries) {
		this.entries = entries;
	}

	/**
	 * Sets the name of the property.
	 * 
	 * @param name the new name of the property
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the name of the array element type.
	 * 
	 * @param typeName the new element type name
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
