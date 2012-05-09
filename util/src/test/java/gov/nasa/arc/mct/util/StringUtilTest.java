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
package gov.nasa.arc.mct.util;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StringUtilTest {

	@Test
	public void testInstantiation() {
		Object o = new StringUtil();
		assertNotNull(o);
	}
	
	@Test
	public void testNull() {
		assertTrue(StringUtil.isEmpty(null));
	}
	
	@Test
	public void testEmpty() {
		assertTrue(StringUtil.isEmpty(""));
	}
	
	@Test
	public void testLengthOne() {
		assertFalse(StringUtil.isEmpty("x"));
	}
	
	@Test
	public void testCompare() {
		assertTrue(StringUtil.compare(null, null));
		assertFalse(StringUtil.compare(null, "hello"));
		assertFalse(StringUtil.compare("hello", null));
		
		assertTrue(StringUtil.compare("hello", "hello"));
		assertTrue(StringUtil.compare("", ""));
		assertFalse(StringUtil.compare("HelLo", "hello"));
		assertFalse(StringUtil.compare("hello", "hello there"));
	}
}
