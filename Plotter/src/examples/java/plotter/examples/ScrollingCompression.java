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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import plotter.DateNumberFormat;
import plotter.TimeTickMarkCalculator;
import plotter.xy.CompressingXYDataset;
import plotter.xy.DefaultCompressor;
import plotter.xy.LinearXYAxis;
import plotter.xy.LinearXYPlotLine;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;
import plotter.xy.XYMarkerLine;
import plotter.xy.XYPlotContents;

public class ScrollingCompression {
	public static void main(String[] args) {
		XYPlotFrame frame = new XYPlotFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setup();
		Timer timer = new Timer();

		final LinearXYAxis xAxis = (LinearXYAxis) frame.getXAxis();
		final XYAxis yAxis = frame.getYAxis();
		xAxis.setTickMarkCalculator(new TimeTickMarkCalculator());
		xAxis.setFormat(new DateNumberFormat(new SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss.SSS")));

		final LinearXYPlotLine line = new LinearXYPlotLine(xAxis, yAxis, XYDimension.X);
		line.setForeground(Color.white);
		final CompressingXYDataset d = new CompressingXYDataset(line, new DefaultCompressor());
		d.setTruncationPoint(0);
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

		yAxis.setStart(-1.2);
		yAxis.setEnd(1.2);
		xAxis.setStart(0);
		xAxis.setEnd(10);

		frame.getLocationDisplay().setFormat(new MessageFormat("<html><b>X:</b> {0,date,HH:mm:ss} &nbsp; <b>Y:</b> {1}</html>"));
		frame.getSlopeLineDisplay().setFormat(new MessageFormat("<html><b>&Delta;x:</b> {0,date,HH:mm:ss}  <b>&Delta;y:</b> {1}</html>"));

		for(int x = 0; x < 900; x++) {
			double x2 = x / 10.0;
			double y2 = Math.sin(x2 / 10.0);
			d.add(x2, y2);
		}
		timer.schedule(new TimerTask() {
			int x = 0;
			long startTime = System.currentTimeMillis();


			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						long now = System.currentTimeMillis();
						xAxis.setStart(now / 1000 * 1000 - 9000);
						xAxis.setEnd(now / 1000 * 1000 + 1000);
						d.setTruncationPoint(xAxis.getStart());
						for(int i = 0; i < 10; i++) {
							double x2 = now + i;
							double y2 = Math.sin(x2 / 2000.0);
							d.add(x2, y2);
							marker.setValue(x2);
							marker2.setValue(y2);
							x++;
							if(x % 100 == 0) {
								System.out.println("Dataset size: " + d.getPointCount());
								System.out.println("Points per pixel: " + d.getPointCount() / (double) xAxis.getWidth());
								System.out.println("Points per second: " + 1000 * x / (double) (now - startTime));
							}
						}
					}
				});
			}
		}, 10, 10);

		line.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				d.setCompressionOffset(xAxis.getStart());
				d.setCompressionScale((xAxis.getEnd() - xAxis.getStart()) / xAxis.getWidth());
			}
		});

		frame.setSize(400, 300);
		frame.setVisible(true);
	}
}
