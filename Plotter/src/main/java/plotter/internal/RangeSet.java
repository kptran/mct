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

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Set of closed ranges.  (Each range contains its endpoints.)
 * Behavior is undefined if NaN is used.
 * This class is optimized for the common case where the set contains exactly one contiguous range.
 * @author Adam Crume
 */
public class RangeSet {
	/** Holds the minimum value if there is exactly one range, infinity if none, and is undefined otherwise. */
	private double min = Double.POSITIVE_INFINITY;

	/** Holds the maximum value if there is exactly one range, negative infinity if none, and is undefined otherwise. */
	private double max = Double.NEGATIVE_INFINITY;

	/** If there is more than one range, this maps the minimum to the maximum for each range.  Otherwise, this is null. */
	private NavigableMap<Double, Double> data;


	/**
	 * Unions the range set with the range (min, max).
	 * Min and max are inclusive.
	 * @param min minimum endpoint of the range
	 * @param max maximum endpoint of the range
	 */
	public void add(double min, double max) {
		if(data != null) {
			Entry<Double, Double> e = data.floorEntry(min);
			if(e != null) {
				double key = e.getKey();
				double value = e.getValue();
				if(min <= value && max >= key) {
					min = Math.min(key, min);
					max = Math.max(value, max);
					data.remove(key);
				}
			}
			do {
				Entry<Double, Double> e2 = data.higherEntry(min);
				if(e2 == null) {
					break;
				}
				double key = e2.getKey();
				double value = e2.getValue();
				if(!(min <= value && max >= key)) {
					break;
				}
				min = Math.min(key, min);
				max = Math.max(value, max);
				data.remove(key);
			} while(true);
			if(data.isEmpty()) {
				data = null;
				this.min = min;
				this.max = max;
			} else {
				data.put(min, max);
			}
		} else if((this.min == Double.POSITIVE_INFINITY && this.max == Double.NEGATIVE_INFINITY)
				|| (min <= this.max && max >= this.min)) {
			this.min = Math.min(this.min, min);
			this.max = Math.max(this.max, max);
		} else {
			data = new TreeMap<Double, Double>();
			data.put(this.min, this.max);
			data.put(min, max);
		}
	}


	/**
	 * Resets this to the empty set.
	 */
	public void clear() {
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
		data = null;
	}


	/**
	 * Returns the minimum value if there is exactly one range, infinity if none, and an unspecified value otherwise.
	 * @return the minimum value
	 */
	public double getMin() {
		return min;
	}


	/**
	 * Returns the maximum value if there is exactly one range, infinity if none, and an unspecified value otherwise.
	 * @return the maximum value
	 */
	public double getMax() {
		return max;
	}


	/**
	 * Returns a map of the minimum to the maximum for each range, or null if there are zero or one ranges.
	 * @return a map of the minimum to the maximum for each range, may be null
	 */
	public NavigableMap<Double, Double> getData() {
		return data;
	}


	@Override
	public String toString() {
		if(data == null) {
			if(min == Double.POSITIVE_INFINITY) {
				return "{}";
			} else {
				return "{(" + min + ":" + max + ")}";
			}
		} else {
			StringBuffer b = new StringBuffer();
			b.append("{");
			for(Iterator<Entry<Double, Double>> itr = data.entrySet().iterator(); itr.hasNext();) {
				Entry<Double, Double> e = itr.next();
				double min = e.getKey();
				double max = e.getValue();
				b.append("(");
				b.append(min);
				b.append(":");
				b.append(max);
				b.append(")");
				if(itr.hasNext()) {
					b.append(",");
				}
			}
			b.append("}");
			return b.toString();
		}
	}
}
