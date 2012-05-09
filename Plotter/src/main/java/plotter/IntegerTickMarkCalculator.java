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


/**
 * Creates tick marks on integer values.
 * @author Adam Crume
 */
public class IntegerTickMarkCalculator implements TickMarkCalculator {
	private static final double[] EMPTY = new double[0];

	@Override
	public double[][] calculateTickMarks(Axis axis) {
		double axisStart = axis.getStart();
		double axisEnd = axis.getEnd();
		double min;
		double max;
		if(axisStart < axisEnd) {
			min = axisStart;
			max = axisEnd;
		} else {
			min = axisEnd;
			max = axisStart;
		}
		int start = (int) Math.ceil(min);
		int end = (int) Math.floor(max);
		int count = end - start + 1;
		double[] majorVals;
		if(count > 0) {
			majorVals = new double[count];
			for(int i = 0; i < count; i++) {
				majorVals[i] = start + i;
			}
		} else {
			majorVals = EMPTY;
		}

		return new double[][] { majorVals, EMPTY };
	}
}
