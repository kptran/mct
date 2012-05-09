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
import java.awt.Dimension;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import plotter.DateNumberFormat;
import plotter.TimeTickMarkCalculator;
import plotter.xy.LinearXYAxis;
import plotter.xy.LinearXYPlotLine;
import plotter.xy.SimpleXYDataset;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;
import plotter.xy.XYMarkerLine;
import plotter.xy.XYPlotContents;

public class ScrollingTimeOnY {
	public static void main(String[] args) {
		XYPlotFrame frame = new XYPlotFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setup();
		Timer timer = new Timer();

		final LinearXYAxis yAxis = (LinearXYAxis) frame.getYAxis();
		final XYAxis xAxis = frame.getXAxis();
		yAxis.setTickMarkCalculator(new TimeTickMarkCalculator());
		yAxis.setFormat(new DateNumberFormat(new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss.SSS")));
		yAxis.setPreferredSize(new Dimension(100, 50));
		xAxis.setStartMargin(100);

		final LinearXYPlotLine line = new LinearXYPlotLine(xAxis, yAxis, XYDimension.Y);
		line.setForeground(Color.white);
		final SimpleXYDataset d = new SimpleXYDataset(line);
		d.setMaxCapacity(1000);
		d.setXData(line.getXData());
		d.setYData(line.getYData());
		final XYMarkerLine marker = new XYMarkerLine(xAxis, 60);
		marker.setForeground(Color.yellow);
		XYPlotContents contents = frame.getContents();
		contents.add(marker);
		final XYMarkerLine marker2 = new XYMarkerLine(yAxis, .5);
		marker2.setForeground(Color.red);
		contents.add(marker2);
		frame.addPlotLine(line);

		yAxis.setStart(0);
		yAxis.setEnd(10);
		xAxis.setStart(-1.2);
		xAxis.setEnd(1.2);

		frame.getLocationDisplay().setFormat(new MessageFormat("<html><b>X:</b> {0} &nbsp; <b>Y:</b> {1,date,HH:mm:ss}</html>"));
		frame.getSlopeLineDisplay().setFormat(new MessageFormat("<html><b>&Delta;x:</b> {0}  <b>&Delta;y:</b> {1,date,HH:mm:ss}</html>"));

		timer.schedule(new TimerTask() {
			int x = 0;


			@Override
			public void run() {
				x++;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						long now = System.currentTimeMillis();
						yAxis.setStart(now / 1000 * 1000 - 9000);
						yAxis.setEnd(now / 1000 * 1000 + 1000);
						double y2 = now;
						double x2 = Math.sin(y2 / 2000.0);
						d.add(x2, y2);
						marker.setValue(x2);
						marker2.setValue(y2);
					}
				});
			}
		}, 100, 100);
		
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
