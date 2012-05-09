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

import javax.swing.JFrame;

import plotter.xy.LinearXYPlotLine;
import plotter.xy.MissingPointMode;
import plotter.xy.SimpleXYDataset;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;

public class MissingPointModes {
	public static void main(String[] args) {
		XYPlotFrame frame = new XYPlotFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setup();

		XYAxis xAxis = frame.getXAxis();
		XYAxis yAxis = frame.getYAxis();
		LinearXYPlotLine[] lines = new LinearXYPlotLine[8];
		SimpleXYDataset[] datasets = new SimpleXYDataset[lines.length];
		for(int i = 0; i < lines.length; i++) {
			LinearXYPlotLine line = new LinearXYPlotLine(xAxis, yAxis, XYDimension.X);
			SimpleXYDataset d = new SimpleXYDataset(line);
			d.setMaxCapacity(1000);
			d.setXData(line.getXData());
			d.setYData(line.getYData());
			frame.addPlotLine(line);
			lines[i] = line;
			datasets[i] = d;
		}

		yAxis.setStart(0);
		yAxis.setEnd(11);
		xAxis.setStart(0);
		xAxis.setEnd(2 * Math.PI);

		lines[0].setMissingPointMode(MissingPointMode.NONE);
		lines[1].setMissingPointMode(MissingPointMode.LEFT);
		lines[2].setMissingPointMode(MissingPointMode.RIGHT);
		lines[3].setMissingPointMode(MissingPointMode.BOTH);
		lines[4].setMissingPointMode(MissingPointMode.NONE);
		lines[5].setMissingPointMode(MissingPointMode.LEFT);
		lines[6].setMissingPointMode(MissingPointMode.RIGHT);
		lines[7].setMissingPointMode(MissingPointMode.BOTH);

		lines[0].setForeground(Color.white);
		lines[1].setForeground(Color.blue);
		lines[2].setForeground(Color.cyan);
		lines[3].setForeground(Color.gray);
		lines[4].setForeground(Color.green);
		lines[5].setForeground(Color.magenta);
		lines[6].setForeground(Color.orange);
		lines[7].setForeground(Color.red);

		// Note that line[0] is at the top
		for(int i = 0; i < datasets.length; i++) {
			SimpleXYDataset d = datasets[i];
			double offset = datasets.length - 1 - i;
			if(i >= datasets.length / 2) {
				d.add(0, Double.NaN);
			}
			d.add(1, 1.5 + offset);
			d.add(2, 1 + offset);
			d.add(3, Double.NaN);
			d.add(4, 1.25 + offset);
			d.add(5, 1.75 + offset);
			if(i >= datasets.length / 2) {
				d.add(6, Double.NaN);
			}
		}

		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
