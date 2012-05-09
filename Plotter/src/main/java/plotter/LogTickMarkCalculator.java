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

import java.util.ArrayList;
import java.util.List;



/**
 * Implementation of {@link TickMarkCalculator} for a logarithmic axis.
 * @author Adam Crume
 */
public class LogTickMarkCalculator implements TickMarkCalculator {
	// TODO: Make the ticks work at different scales
	@Override
	public double[][] calculateTickMarks(Axis axis) {
		double start = axis.getStart();
		double end = axis.getEnd();
		double min = start;
		double max = end;
		if(min > max) {
			double tmp = max;
			max = min;
			min = tmp;
		}
		double diff = max - min;

		// d should be diff scaled to between 1 and 10
		double d = diff / Math.pow(10, Math.floor(Math.log10(diff)));
		double spacing = diff / d;

		double[] minorVals;
		double[] majorVals;

		double startCount = Math.ceil(min / spacing);
		double endCount = Math.floor(max / spacing);
		majorVals = new double[(int)(endCount - startCount) + 1];
		double value = Math.ceil(min / spacing) * spacing;
		for(int i = 0; i < majorVals.length; i++) {
			majorVals[i] = value;
			value += spacing;
		}

		// TODO: Make the calculation for minor ticks efficient
		double base = Math.floor(min / spacing) * spacing;
		List<Double> mins = new ArrayList<Double>();
		while(base <= max) {
			for(int i = 1; i < 10; i++) {
				value = base + Math.log10(i);
				if(value >= min && value <= max) {
					mins.add(value);
				}
			}
			base++;
		}

		minorVals = new double[mins.size()];
		for(int i = 0; i < minorVals.length; i++) {
			minorVals[i] = mins.get(i);
		}

		return new double[][] {majorVals, minorVals};
	}
}
