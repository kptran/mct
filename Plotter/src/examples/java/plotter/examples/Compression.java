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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.MessageFormat;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import plotter.DoubleData;
import plotter.xy.DefaultCompressor;
import plotter.xy.DefaultXYLayoutGenerator;
import plotter.xy.LinearXYAxis;
import plotter.xy.LinearXYPlotLine;
import plotter.xy.PointData;
import plotter.xy.SimpleXYDataset;
import plotter.xy.SlopeLine;
import plotter.xy.SlopeLineDisplay;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;
import plotter.xy.XYGrid;
import plotter.xy.XYLocationDisplay;
import plotter.xy.XYPlot;
import plotter.xy.XYPlotContents;

public class Compression {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		JPanel uncompressedContents = new JPanel();
		contentPane.add(uncompressedContents);
		final JPanel compressedContents = new JPanel();
		contentPane.add(compressedContents);
		contentPane.setLayout(new GridLayout(2, 1));
		final SimpleXYDataset d = createPlot(uncompressedContents);
		final SimpleXYDataset d2 = createPlot(compressedContents);

		double value = 0;
		double signal = 0;
		Random random = new Random();
		for(int i = 0; i < 5000; i++) {
			value = value * .99 + random.nextGaussian();
			signal = signal * .99 + random.nextGaussian();
			double y = value;
			if(signal < -1) {
				y = Double.NaN;
			}
			d.add(i, y);
		}
		for(int i = 0; i < 1000; i++) {
			value = (i / 100) % 2 - .5;
			d.add(i + 5000, value * 30);
		}
		for(int i = 0; i < 1000; i++) {
			value = i % 2 - .5;
			d.add(i + 6000, value * 30);
		}
		for(int i = 0; i < 1000; i++) {
			int j = i % 6;
			if(j == 0) {
				value = -.5;
			} else if(j == 1) {
				value = -.25;
			} else if(j == 2) {
				value = Double.NaN;
			} else if(j == 3) {
				value = .25;
			} else if(j == 4) {
				value = .5;
			} else if(j == 5) {
				value = Double.NaN;
			}
			d.add(i + 7000, value * 30);
		}
		for(int i = 0; i < 1000; i++) {
			int j = i % 100;
			if(j == 0) {
				value = Double.NaN;
			} else if(j < 50) {
				value = -.5;
			} else if(j == 50) {
				value = Double.NaN;
			} else {
				value = .5;
			}
			d.add(i + 8000, value * 30);
		}
		for(int i = 0; i < 1000; i++) {
			double x = (1000 - i) / 10000.0;
			value = Math.sin(x + 1 / x);
			d.add(i + 9000, value * 15);
		}

		compressedContents.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				compress(compressedContents, d, d2);
			}
		});

		compress(compressedContents, d, d2);

		frame.setSize(400, 300);
		frame.setVisible(true);
	}


	private static void compress(JPanel compressedContents, final SimpleXYDataset d, final SimpleXYDataset d2) {
		double scale = 10000.0 / compressedContents.getWidth();
		DefaultCompressor compressor = new DefaultCompressor();
		PointData input = new PointData(d.getXData(), d.getYData());
		PointData output = new PointData();
		compressor.compress(input, output, 0, scale);
		int size = d.getPointCount();
		int size2 = output.getX().getLength();
		DoubleData x = output.getX();
		DoubleData y = output.getY();
		d2.removeAllPoints();
		for(int i = 0; i < size2; i++) {
			d2.add(x.get(i), y.get(i));
		}
		System.out.println("Input size: " + size);
		System.out.println("Output size: " + size2);
		System.out.println("Points per pixel: " + size2 / (double) compressedContents.getWidth());
		System.out.println("Compression ratio: " + 100.0 * (size - size2) / (double) size);
	}


	private static SimpleXYDataset createPlot(Container contentPane) {
		final XYPlot plot = new XYPlot();
		XYAxis xAxis = new LinearXYAxis(XYDimension.X);
		XYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		xAxis.setPreferredSize(new Dimension(1, 40));
		yAxis.setPreferredSize(new Dimension(40, 1));
		xAxis.setForeground(Color.white);
		yAxis.setForeground(Color.white);
		xAxis.setTextMargin(10);
		yAxis.setTextMargin(10);
		plot.add(xAxis);
		plot.add(yAxis);
		plot.setXAxis(xAxis);
		plot.setYAxis(yAxis);
		plot.setBackground(Color.darkGray);
		XYPlotContents contents = new XYPlotContents();
		contents.setBackground(Color.black);
		plot.setBackground(Color.darkGray);
		XYGrid grid = new XYGrid(xAxis, yAxis);
		grid.setForeground(Color.lightGray);
		contents.add(grid);
		plot.add(contents);
		plot.setPreferredSize(new Dimension(150, 100));
		
		contentPane.setBackground(Color.darkGray);
		
		XYLocationDisplay locationDisplay = new XYLocationDisplay();
		// This is a hack to set the preferred height to the normal height so the component doesn't collapse to height 0 when the text is empty.
		// Note that mimimumSize does not work for some reason.
		locationDisplay.setText("Ag");
		Dimension size = locationDisplay.getPreferredSize();
		size.width = 100;
		locationDisplay.setText("");
		locationDisplay.setPreferredSize(size);
		// End hack
		locationDisplay.setForeground(Color.white);
		locationDisplay.setFont(new Font("Arial", 0, 12));
		locationDisplay.setFormat(new MessageFormat("<html><b>X:</b> {0} &nbsp; <b>Y:</b> {1}</html>"));
		locationDisplay.attach(plot);
		plot.add(locationDisplay);

		SlopeLine slopeLine = new SlopeLine();
		slopeLine.setForeground(Color.white);
		slopeLine.attach(plot);

		SlopeLineDisplay slopeLineDisplay = new SlopeLineDisplay();
		slopeLine.addListenerForPlot(plot, slopeLineDisplay);
		slopeLineDisplay.setFont(new Font("Arial", 0, 12));
		slopeLineDisplay.setForeground(Color.white);
		slopeLineDisplay.setFormat(new MessageFormat("<html><b>&Delta;x:</b> {0}  <b>&Delta;y:</b> {1}</html>"));
		plot.add(slopeLineDisplay);

		contentPane.add(plot);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		new DefaultXYLayoutGenerator().generateLayout(plot);

		final LinearXYPlotLine line = new LinearXYPlotLine(xAxis, yAxis, XYDimension.X);
		line.setForeground(Color.white);
		final SimpleXYDataset d = new SimpleXYDataset(line);
		d.setMaxCapacity(10000);
		d.setXData(line.getXData());
		d.setYData(line.getYData());
		contents.add(line);
		contents.setComponentZOrder(grid, contents.getComponentCount() - 1);

		yAxis.setStart(-20);
		yAxis.setEnd(20);
		xAxis.setStart(0);
		xAxis.setEnd(10000);
		return d;
	}
}
