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
package plotter.internal;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class JUnitRangeSet extends TestCase {
	public void testGeneral() {
		RangeSet r = new RangeSet();
		assertNull(r.getData());
		assertEquals(Double.POSITIVE_INFINITY, r.getMin());
		assertEquals(Double.NEGATIVE_INFINITY, r.getMax());

		r.add(1, 2);
		assertNull(r.getData());
		assertEquals(1.0, r.getMin());
		assertEquals(2.0, r.getMax());

		r.add(3, 4);
		Map<Double, Double> m = new HashMap<Double, Double>();
		m.put(1.0, 2.0);
		m.put(3.0, 4.0);
		assertEquals(m, r.getData());
	}


	public void testOverlap1() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(.5, 1.5);
		Map<Double, Double> m = new HashMap<Double, Double>();
		m.put(.5, 2.0);
		m.put(3.0, 4.0);
		assertEquals(m, r.getData());
	}


	public void testOverlap2() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(.5, 2.5);
		Map<Double, Double> m = new HashMap<Double, Double>();
		m.put(.5, 2.5);
		m.put(3.0, 4.0);
		assertEquals(m, r.getData());
	}


	public void testOverlap3() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(.5, 3.5);
		assertNull(r.getData());
		assertEquals(.5, r.getMin());
		assertEquals(4.0, r.getMax());
	}


	public void testOverlap4() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(.5, 4.5);
		assertNull(r.getData());
		assertEquals(.5, r.getMin());
		assertEquals(4.5, r.getMax());
	}


	public void testOverlap5() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(1.5, 1.75);
		Map<Double, Double> m = new HashMap<Double, Double>();
		m.put(1.0, 2.0);
		m.put(3.0, 4.0);
		assertEquals(m, r.getData());
	}


	public void testOverlap6() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(1.5, 2.5);
		Map<Double, Double> m = new HashMap<Double, Double>();
		m.put(1.0, 2.5);
		m.put(3.0, 4.0);
		assertEquals(m, r.getData());
	}


	public void testOverlap7() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(1.5, 3.5);
		assertNull(r.getData());
		assertEquals(1.0, r.getMin());
		assertEquals(4.0, r.getMax());
	}


	public void testOverlap8() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(1.5, 4.5);
		assertNull(r.getData());
		assertEquals(1.0, r.getMin());
		assertEquals(4.5, r.getMax());
	}


	public void testOverlapMultiple1() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(5, 6);
		r.add(1.5, 5.5);
		assertNull(r.getData());
		assertEquals(1.0, r.getMin());
		assertEquals(6.0, r.getMax());
	}


	public void testOverlapMultiple2() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(5, 6);
		r.add(7, 8);
		r.add(1.5, 5.5);
		Map<Double, Double> m = new HashMap<Double, Double>();
		m.put(1.0, 6.0);
		m.put(7.0, 8.0);
		assertEquals(m, r.getData());
	}


	public void testToStringEmpty() {
		RangeSet r = new RangeSet();
		assertEquals("{}", r.toString());
	}


	public void testToStringOne() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		assertEquals("{(1.0:2.0)}", r.toString());
	}


	public void testToStringMany() {
		RangeSet r = new RangeSet();
		r.add(1, 2);
		r.add(3, 4);
		r.add(5, 6);
		assertEquals("{(1.0:2.0),(3.0:4.0),(5.0:6.0)}", r.toString());
	}
}
