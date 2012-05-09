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
package plotter.examples;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import plotter.ExpFormat;
import plotter.LogTickMarkCalculator;
import plotter.xy.LinearXYAxis;
import plotter.xy.LinearXYPlotLine;
import plotter.xy.SimpleXYDataset;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;

/**
 * Demonstrates plotting with a logarithmic Y axis.
 * Note that this approach involves manually applying the logarithm function to data points before adding them to the dataset.
 * This also requires adjusting the format for the axis to use a {@link ExpFormat} so it will display the original values instead of their logarithms.
 * The rationale for all this is to reduce the number of times the logarithm of the data points must be calculated.
 * @author Adam Crume
 */
public class LogPlot {
	public static void main(String[] args) {
		XYPlotFrame frame = new XYPlotFrame() {
			private static final long serialVersionUID = 1L;

			@Override
			protected XYAxis createYAxis() {
				LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
				axis.setTickMarkCalculator(new LogTickMarkCalculator());
				axis.setFormat(new ExpFormat(new DecimalFormat("#.#")));
				return axis;
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setup();

		XYAxis xAxis = frame.getXAxis();
		XYAxis yAxis = frame.getYAxis();
		final LinearXYPlotLine line = new LinearXYPlotLine(xAxis, yAxis, XYDimension.X);
		line.setForeground(Color.white);
		final SimpleXYDataset d = new SimpleXYDataset(line);
		d.setMaxCapacity(1000);
		d.setXData(line.getXData());
		d.setYData(line.getYData());
		frame.addPlotLine(line);

		yAxis.setStart(-1.2);
		yAxis.setEnd(3.2);
		xAxis.setStart(0);
		xAxis.setEnd(2 * Math.PI);

		for(int x = 0; x <= 100; x++) {
			double x2 = x / 100.0 * 2 * Math.PI;
			double y2 = 100 * (Math.sin(x2) + 1) + 1;
			d.add(x2, Math.log10(y2));
		}

		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
