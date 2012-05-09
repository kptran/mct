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
package plotter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

public class PropertyTester {
	private final Object bean;
	private final BeanInfo beanInfo;


	public PropertyTester(Object bean) throws IntrospectionException {
		this.bean = bean;
		beanInfo = Introspector.getBeanInfo(bean.getClass());
	}


	public void test(String propertyName, Object... values) throws InvocationTargetException, IllegalAccessException {
		PropertyDescriptor pd = null;
		for(PropertyDescriptor p : beanInfo.getPropertyDescriptors()) {
			if(p.getName().equals(propertyName)) {
				pd = p;
				break;
			}
		}
		Assert.assertNotNull("Property not found: " + propertyName, pd);
		Method readMethod = pd.getReadMethod();
		Method writeMethod = pd.getWriteMethod();
		Assert.assertNotNull("Getter not found: " + propertyName, readMethod);
		Assert.assertNotNull("Setter not found: " + propertyName, writeMethod);

		if(values == null || values.length == 0) {
			Class<?> type = pd.getPropertyType();
			if(type == boolean.class) {
				values = new Object[] { true, false };
			} else if(type == Boolean.class) {
				values = new Object[] { true, false, null };
			} else if(type == byte.class) {
				values = new Object[] { (byte) 0, Byte.MAX_VALUE, Byte.MIN_VALUE, (byte) 1, (byte) -1 };
			} else if(type == Byte.class) {
				values = new Object[] { (byte) 0, Byte.MAX_VALUE, Byte.MIN_VALUE, (byte) 1, (byte) -1, null };
			} else if(type == short.class) {
				values = new Object[] { (short) 0, Short.MAX_VALUE, Short.MIN_VALUE, (short) 1, (short) -1 };
			} else if(type == Short.class) {
				values = new Object[] { (short) 0, Short.MAX_VALUE, Short.MIN_VALUE, (short) 1, (short) -1, null };
			} else if(type == char.class) {
				values = new Object[] { (char) 0, Character.MAX_VALUE, 'a', '1', '.', '\\', ' ', '\n' };
			} else if(type == Character.class) {
				values = new Object[] { (char) 0, Character.MAX_VALUE, 'a', '1', '.', '\\', ' ', '\n', null };
			} else if(type == int.class) {
				values = new Object[] { 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 1, -1 };
			} else if(type == Integer.class) {
				values = new Object[] { 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 1, -1, null };
			} else if(type == long.class) {
				values = new Object[] { 0L, Long.MAX_VALUE, Long.MIN_VALUE, 1L, -1L };
			} else if(type == Long.class) {
				values = new Object[] { 0L, Long.MAX_VALUE, Long.MIN_VALUE, 1L, -1L, null };
			} else if(type == float.class) {
				values = new Object[] { 0.0f, 1.0f, -1.0f, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN, Float.MAX_VALUE,
						Float.MIN_VALUE };
			} else if(type == Float.class) {
				values = new Object[] { 0.0f, 1.0f, -1.0f, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN, Float.MAX_VALUE,
						Float.MIN_VALUE, null };
			} else if(type == double.class) {
				values = new Object[] { 0.0, 1.0, -1.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN, Double.MAX_VALUE,
						Double.MIN_VALUE };
			} else if(type == Double.class) {
				values = new Object[] { 0.0, 1.0, -1.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN, Double.MAX_VALUE,
						Double.MIN_VALUE, null };
			} else if(type == String.class) {
				values = new Object[] { null, "", " ", "test", "This is a test." };
			} else {
				Assert.fail("Values must be provided to test a property of type " + type.getName());
			}
		}

		for(Object value : values) {
			writeMethod.invoke(bean, value);
			Object value2 = readMethod.invoke(bean);
			Assert.assertEquals(value, value2);
		}
	}
}
