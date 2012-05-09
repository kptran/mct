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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Implements a data type that can appear in an OSGi service configuration.
 * 
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
enum SimpleType {

	/** The singleton instance. */
	INSTANCE;
	
	private static abstract class AllowedType {

		private Class<? extends Object> clazz;

		public AllowedType(Class<? extends Object> clazz) {
			this.clazz = clazz;
		}

		public Class<? extends Object> getTypeClass() {
			return clazz;
		}

		public abstract Object convert(String s);

	}
	
	private static Map<String, AllowedType> allowedTypes = new HashMap<String, AllowedType>();
	static {
		allowedTypes.put("String", new AllowedType(String.class) {
			@Override
			public Object convert(String s) {
				return s;
			}
		});
		allowedTypes.put("Integer", new AllowedType(java.lang.Integer.class) {
			@Override
			public Object convert(String s) {
				return Integer.valueOf(s);
			}
		});
		allowedTypes.put("Long", new AllowedType(java.lang.Long.class) {
			@Override
			public Object convert(String s) {
				return Long.valueOf(s);
			}
		});
		allowedTypes.put("Float", new AllowedType(java.lang.Float.class) {
			@Override
			public Object convert(String s) {
				return Float.valueOf(s);
			}
		});
		allowedTypes.put("Double", new AllowedType(java.lang.Double.class) {
			@Override
			public Object convert(String s) {
				return Double.valueOf(s);
			}
		});
		allowedTypes.put("Byte", new AllowedType(java.lang.Byte.class) {
			@Override
			public Object convert(String s) {
				return Byte.valueOf(s);
			}
		});
		allowedTypes.put("Short", new AllowedType(java.lang.Short.class) {
			@Override
			public Object convert(String s) {
				return Short.valueOf(s);
			}
		});
		allowedTypes.put("Character", new AllowedType(java.lang.Character.class) {
			@Override
			public Object convert(String s) {
				if (s.length() < 1) {
					throw new IllegalArgumentException("Missing Character value");
				}
				return Character.valueOf(s.charAt(0));
			}
		});
		allowedTypes.put("Boolean", new AllowedType(java.lang.Boolean.class) {
			@Override
			public Object convert(String s) {
				return Boolean.valueOf(s);
			}
		});
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the instance
	 */
	public static SimpleType getInstance() {
		return INSTANCE;
	}

	/**
	 * Tests whether a type name is among the allowed type names.
	 * 
	 * @param typeName the type name to check
	 * @throws IllegalArgumentException if the type is not among the the allowed set
	 */
	public void checkTypeName(String typeName) {
		if (!allowedTypes.containsKey(typeName)) {
			throw new IllegalArgumentException(
					"Type name must be one of String, Integer, Long, Float, Double, Byte, Short, Character, or Boolean"
			);
		}
	}

	/**
	 * Gets the class for a a specified type name.
	 * 
	 * @param typeName the type name
	 * @return the class for the specified type name
	 * @throws IllegalArgumentException if the type name is not among the supported types
	 */
	public Class<? extends Object> getTypeForName(String typeName) {
		if (allowedTypes.containsKey(typeName)) {
			return allowedTypes.get(typeName).getTypeClass();
		} 
		throw new IllegalArgumentException(
				"Type name must be one of String, Integer, Long, Float, Double, Byte, Short, Character, or Boolean"
		);
	}

	/**
	 * Returns a property value converted to a specified type.
	 * 
	 * @param typeName the type desired
	 * @param value the value to convert
	 * @return the converted value
	 */
	public Object convertValue(String typeName, String value) {
		checkTypeName(typeName);

		// OK, type name is OK.
		return allowedTypes.get(typeName).convert(value);
	}
}
