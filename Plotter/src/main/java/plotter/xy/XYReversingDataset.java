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
package plotter.xy;

import plotter.DoubleData;

/**
 * Swaps the X and Y coordinates and forwards to another dataset.
 * @author Adam Crume
 */
public class XYReversingDataset implements XYDataset {
	private final XYDataset base;


	/**
	 * Creates the dataset.
	 * @param base dataset to forward data to
	 */
	public XYReversingDataset(XYDataset base) {
		this.base = base;
	}


	@Override
	public int getPointCount() {
		return base.getPointCount();
	}


	@Override
	public void add(double x, double y) {
		base.add(y, x);
	}


	@Override
	public void removeAllPoints() {
		base.removeAllPoints();
	}


	@Override
	public void prepend(double[] x, int xoff, double[] y, int yoff, int len) {
		base.prepend(y, yoff, x, xoff, len);
	}


	@Override
	public void prepend(DoubleData x, DoubleData y) {
		base.prepend(y, x);
	}


	@Override
	public void removeLast(int count) {
		base.removeLast(count);
	}
}
