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
/**
 * MCTPropertiesTest.java Aug 18, 2008
 * 
 * This code is the property of the National Aeronautics and Space
 * Administration and was produced for the Mission Control Technologies (MCT)
 * project.
 * 
 */
package gov.nasa.arc.mct.util.property;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MCTPropertiesTest  {

	private Properties systemProps;
	private MCTProperties mctSystemProps = null;
	private MCTProperties thisPkgProps = null;

	@BeforeMethod
	public void makeOurClass () throws IOException {
		systemProps = (Properties) System.getProperties().clone();
		mctSystemProps = new MCTProperties();
		thisPkgProps = new MCTProperties(MCTPropertiesTest.class);
	}

	@AfterMethod
	public void restoreProperties() {
		System.setProperties(systemProps);
	}

	@Test
	public void basicPositiveTest() throws Exception {
		assertEquals(thisPkgProps.getProperty("test"), "testing789000");
		assertEquals(thisPkgProps.size(), 3);

		assertTrue(mctSystemProps.containsKey("locale"));
	}

	@Test
	public void defaultPropertyTest() throws Exception {  
		String value = null;
		value = thisPkgProps.getProperty("xxx", "fallbackValue");
		assertEquals(value, "fallbackValue");

		value = thisPkgProps.getProperty("xxx");
		Assert.assertNull(value);
	}

	@Test
	public void stringFileNameTest() throws Exception {
		MCTProperties props = new MCTProperties("properties/MCTPropertiesTest.properties");
		assertNotNull(props);
		assertTrue(props.containsKey("test"));
	}

	public static class MyClass {}

	@Test(expectedExceptions={java.io.IOException.class})
	public void testMissingClassProperties() throws Exception {
		@SuppressWarnings("unused")
		MCTProperties props = new MCTProperties(MyClass.class);
	}

	@Test(expectedExceptions={java.io.IOException.class})
	public void testMissingStringProperties() throws Exception {
		@SuppressWarnings("unused")
		MCTProperties props = new MCTProperties("properties/non.existent.properties.file");
	}
	
	@Test
	public void testSystemPropertyOverride() throws Exception {
		System.setProperty("new.property", "new.value");
		assertTrue(!thisPkgProps.containsKey("new.property"));
		assertEquals(thisPkgProps.getProperty("new.property"), "new.value");
		assertEquals(thisPkgProps.getProperty("new.property", "default.value"), "new.value");
	}

	@Test
	public void testTrailingSpaces() {
		assertEquals(thisPkgProps.getProperty("no_trailing_space_property"), "abc");
		assertEquals(thisPkgProps.getProperty("trailing_space_property"), "abc");
		assertNull(thisPkgProps.getProperty("no_such_property"));
		assertEquals(thisPkgProps.getProperty("no_such_property", "abc"), "abc");
		assertEquals(thisPkgProps.getProperty("no_such_property", " abc"), "abc");
		assertEquals(thisPkgProps.getProperty("no_such_property", "abc "), "abc");
		assertEquals(thisPkgProps.getProperty("no_such_property", " abc "), "abc");
	}
	
	@Test
	public void testTrim() {
		assertNull(MCTProperties.trim(null));
		assertEquals(MCTProperties.trim(""), "");
		assertEquals(MCTProperties.trim("abc"), "abc");
		assertEquals(MCTProperties.trim(" abc"), "abc");
		assertEquals(MCTProperties.trim("abc "), "abc");
		assertEquals(MCTProperties.trim(" abc "), "abc");
	}
	
	@Test
	public void testDefaultValue() {
		// Case 1: no system property, we have file property
		assertEquals(thisPkgProps.getProperty("test", "abc"), "testing789000");
		// Case 2: no system property, no file property
		assertEquals(thisPkgProps.getProperty("nonExistentSystemProperty", "abc"), "abc");
		// Case 3: system property, no file property
		System.getProperties().put("nonExistentSystemProperty", "xyz");
		assertEquals(thisPkgProps.getProperty("nonExistentSystemProperty", "abc"), "xyz");
		// Case 4: both system property and file property
		System.getProperties().put("test", "xyz");
		assertEquals(thisPkgProps.getProperty("test", "abc"), "xyz");
	}
}
