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

import static org.testng.Assert.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SimpleTypeTest {

	private SimpleType simple;
	
	@BeforeClass
	public void init() {
		simple = SimpleType.getInstance();
	}
	
	@Test
	public void checkValidTypes() {
		// We check that we don't get an exception on the valid type names.
		simple.checkTypeName("String");
		simple.checkTypeName("Integer");
		simple.checkTypeName("Long");
		simple.checkTypeName("Float");
		simple.checkTypeName("Double");
		simple.checkTypeName("Byte");
		simple.checkTypeName("Short");
		simple.checkTypeName("Character");
		simple.checkTypeName("Boolean");
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void checkInvalidType() {
		simple.checkTypeName("Vector");
	}
	
	@Test
	public void testGetTypeForName() {
		assertSame(simple.getTypeForName("String"), String.class);
		assertSame(simple.getTypeForName("Integer"), Integer.class);
		assertSame(simple.getTypeForName("Long"), Long.class);
		assertSame(simple.getTypeForName("Float"), Float.class);
		assertSame(simple.getTypeForName("Double"), Double.class);
		assertSame(simple.getTypeForName("Byte"), Byte.class);
		assertSame(simple.getTypeForName("Short"), Short.class);
		assertSame(simple.getTypeForName("Character"), Character.class);
		assertSame(simple.getTypeForName("Boolean"), Boolean.class);
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testGetTypeForNameInvalidType() {
		assertSame(simple.getTypeForName("Vector"), java.util.Vector.class);
	}
	
	@Test
	public void testConvertType() {
		assertEquals(simple.convertValue("String", "hello"), "hello");
		assertEquals(simple.convertValue("Integer", "123"), new Integer(123));
		assertEquals(simple.convertValue("Long", "123"), new Long(123));
		assertEquals(simple.convertValue("Float", "123.0"), new Float(123.0));
		assertEquals(simple.convertValue("Double", "123.0"), new Double(123.0));
		assertEquals(simple.convertValue("Byte", "123"), new Byte((byte) 123));
		assertEquals(simple.convertValue("Short", "123"), new Short((short) 123));
		assertEquals(simple.convertValue("Character", "a"), 'a');
		assertEquals(simple.convertValue("Boolean", "true"), true);
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testConvertTypeInvalidType() {
		assertEquals(simple.convertValue("Garbage", "hello"), "hello");
	}

	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testConvertTypeBadCharacter() {
		assertEquals(simple.convertValue("Character", ""), 'a');
	}
	
	@Test  (dataProvider="numberFormatExceptionTests",expectedExceptions = NumberFormatException.class)
	public void testConvertThrowsRuntimeException(Object dataType) {
		simple.convertValue((String) dataType, "nonConvertableString");
	}
		
	@DataProvider(name="numberFormatExceptionTests")
	public Object[][] getTelemetryValueTests() {
		return new Object[][] {
				new Object[] { "Integer" },
				new Object[] { "Long" },
				new Object[] { "Float" },
				new Object[] { "Double" },
				new Object[] { "Byte" },
				new Object[] { "Short" }
		};
	}
}
