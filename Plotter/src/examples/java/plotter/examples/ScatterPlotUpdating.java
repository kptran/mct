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
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import plotter.DoubleData;
import plotter.xy.ScatterXYPlotLine;
import plotter.xy.SimpleXYDataset;
import plotter.xy.XYAxis;

public class ScatterPlotUpdating {
	public static void main(String[] args) {
		XYPlotFrame frame = new XYPlotFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setup();

		XYAxis xAxis = frame.getXAxis();
		XYAxis yAxis = frame.getYAxis();
		final long[] paintTime=new long[1];
		final long[] paints=new long[1];
		final ScatterXYPlotLine line = new ScatterXYPlotLine(xAxis, yAxis) {
			int skip;
			@Override
			protected void paintComponent(Graphics g) {
				long start = System.nanoTime();
				super.paintComponent(g);
				if(skip++>1000) {
				paintTime[0] += System.nanoTime() - start;
				paints[0]++;
				}
			}
		};
		line.setForeground(Color.white);
		final SimpleXYDataset d = new SimpleXYDataset(line);
		d.setMaxCapacity(1000);
		d.setXData(line.getXData());
		d.setYData(line.getYData());
		frame.addPlotLine(line);

		yAxis.setStart(-1.2);
		yAxis.setEnd(1.2);
		xAxis.setStart(-1.2);
		xAxis.setEnd(1.2);

		final int n = 2000;
		for(int x = 0; x <= n; x++) {
			double theta = x / (double) n * 2 * Math.PI;
			double x2 = Math.cos(theta) + .2 * Math.cos(theta * 20);
			double y2 = Math.sin(theta) + .2 * Math.sin(theta * 20);
			d.add(x2, y2);
		}

		frame.setSize(400, 300);
		frame.setVisible(true);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			int x = 2000;


			@Override
			public void run() {
				x++;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						double theta = x / (double) n * 2 * Math.PI;
						double x2 = Math.cos(theta) + .2 * Math.cos(theta * 20);
						double y2 = Math.sin(theta) + .2 * Math.sin(theta * 20);
						d.add(x2, y2);
						if(x % 10 == 0 && paints[0] > 0) {
							System.out.println("Average paint time: " + paintTime[0] / paints[0]);
						}
					}
				});
			}
		}, 100, 100);
	}
}
