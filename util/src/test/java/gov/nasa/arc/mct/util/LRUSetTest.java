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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LRUSetTest {

	@Test
	public void emptySetTest() {
		LRUSet<String> hs = new LRUSet<String>(2);
		assertEquals(hs.size(), 0);
	}

	@Test
	public void fromExistingSetTest() {
		LRUSet<String> hs;

		Set<String> s = new HashSet<String>();
		hs = new LRUSet<String>(2, s);
		assertEquals(hs.size(), 0);
		assertTrue(hs.isEmpty());

		s.add("hello");
		hs = new LRUSet<String>(2, s);
		assertEquals(hs.size(), 1);
		assertTrue(!hs.isEmpty());
		assertTrue(hs.contains("hello"));

		Iterator<String> it = hs.iterator();
		boolean found = false;
		while (it.hasNext()) {
			String value = it.next();
			if (value.equals("hello")) {
				found = true;
			}
		}
		assertTrue(found);

		assertTrue(!hs.add("hello"));
		assertEquals(hs.size(), 1);

		assertTrue(hs.add("goodbye"));
		assertEquals(hs.size(), 2);

		assertTrue(!hs.add("goodbye"));
		assertEquals(hs.size(), 2);

		assertTrue(hs.remove("goodbye"));
		assertEquals(hs.size(), 1);

		assertFalse(hs.remove("goodbye"));
		assertEquals(hs.size(), 1);

		hs.clear();
		assertEquals(hs.size(), 0);
	}
	
	@Test
	public void LRUTest() {
		LRUSet<String> hs;

		Set<String> s = new HashSet<String>();
		hs = new LRUSet<String>(2, s);

		hs.add("hello");
		hs.add("goodbye");
		assertEquals(hs.size(), 2);

		hs.add("close");
		assertEquals(hs.size(), 2);
		assertFalse(hs.contains("hello"));
		assertTrue(hs.contains("goodbye"));
		assertTrue(hs.contains("close"));

		hs.add("continue closing");
		assertFalse(hs.contains("hello"));
		assertFalse(hs.contains("goodbye"));
		assertTrue(hs.contains("close"));
		assertTrue(hs.contains("continue closing"));

		hs.clear();
		assertEquals(hs.size(), 0);
	}

}
