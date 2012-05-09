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
 * WeakHashSet.java Aug 29, 2008
 * 
 * This code is the property of the National Aeronautics and Space
 * Administration and was produced for the Mission Control Technologies (MCT)
 * project.
 * 
 */
package gov.nasa.arc.mct.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class uses {@link java.lang.ref.WeakReference} so that the contained
 * objects could be garbage collected.
 * 
 * @author asi
 * 
 * @param <T>
 */
public class LRUSet<T> extends AbstractSet<T> implements Cloneable {
	private final LRUMap<T, T> map;
	private T lastElement;

	/**
	 * Constructor with max capacity.
	 * @param maxCapacity - number.
	 */
	public LRUSet(int maxCapacity) {
		map = new LRUMap<T, T>(maxCapacity);
	}

	/**
	 * Constructor with max capacity and set.
	 * @param maxCapacity - number.
	 * @param set - the set.
	 */
	public LRUSet(int maxCapacity, Set<T> set) {
		map = new LRUMap<T, T>(maxCapacity);
		for (T t : set) {
			add(t);
		}
	}
	
	/**
	 * Sets the max capacity.
	 * @param maxCapacity - number.
	 */
	public void setMaxCapacity(int maxCapacity) {
		if (map.getMaxCapacity() != maxCapacity) {
			this.map.setMaxCapacity(maxCapacity);
		}
	}
	
	/**
	 * Gets the last element.
	 * @return T - the last element.
	 */
	public T last() {
		return lastElement;
	}

	/**
	 * Iterates through the key set.
	 * @return <T> iterator key set.
	 */
	public Iterator<T> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * Gets the size.
	 * @return size - number.
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Checks whether the set is empty or not.
	 * @return boolean - checks whether map is empty or not.
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Checks whether object is contained in the set.
	 * @param o - the object to compare to.
	 * @return boolean - flag.
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * Adds to the set.
	 * @return boolean - flag. 
	 * @param o - The object to add.
	 */
	public boolean add(T o) {
		this.lastElement = o;
		return map.put(o, o) == null;
	}

	/**
	 * Removes the object from the set.
	 * @param o - the object to remove.
	 * @return boolean - flag.
	 */
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	/**
	 * Clears the set.
	 */
	public void clear() {
		map.clear();
	}
	
	@Override
	public String toString() {
		return this.lastElement.toString();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		LRUSet<T> cloneSet = new LRUSet<T>(this.map.getMaxCapacity(), this);
		cloneSet.lastElement = this.lastElement;
		return cloneSet;
	}

	@SuppressWarnings("serial")
	private static class LRUMap<K, V> extends LinkedHashMap<K, V> {
		private int maxCapacity;

		public LRUMap(int maxCapacity) {
			super(maxCapacity, .75f, true);
			this.maxCapacity = maxCapacity;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > this.maxCapacity;
		}
		
		public void setMaxCapacity(int maxCapacity) {
			this.maxCapacity = maxCapacity;
		}
		
		public int getMaxCapacity() {
			return this.maxCapacity;
		}
	}
}
