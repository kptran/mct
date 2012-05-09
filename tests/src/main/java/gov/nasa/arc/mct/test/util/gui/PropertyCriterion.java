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
package gov.nasa.arc.mct.test.util.gui;

import java.awt.Component;
import java.lang.reflect.Method;

class PropertyCriterion extends Criterion {

	private String propertyName;
	private String expected;
	private boolean isRegularExpression;

	public PropertyCriterion(String propertyName, String expected, boolean isRegularExpression) {
		this.propertyName = propertyName;
		this.expected = expected;
		this.isRegularExpression = isRegularExpression;
	}
	
	private String getMethodName(String propertyName) {
		return "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
	}
	
	@Override
	public boolean satisfies(Component component) {
		return satisfies(getObject(component));
	}
	
	private boolean satisfies(Object object) {
		try {
			Method m = object.getClass().getMethod(getMethodName(propertyName), new Class<?>[0]);
			Object actual = m.invoke(object, new Object[0]);
			if (expected==null && actual==null) {
				return true;
			} else if (expected == null) {
				return false;
			} else {
				return isRegularExpression ? actual.toString().matches(expected) : actual.toString().equals(expected);
			}
		} catch (Throwable e) {
			return false;
		}
	}

	protected Object getObject(Component c) {
		return c;
	}
	
	@Override
	public String toString() {
		return propertyName + (isRegularExpression ? " matches " : "=") + expected;
	}

}
