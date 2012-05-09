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
import java.text.MessageFormat;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import plotter.xy.DefaultXYLayoutGenerator;
import plotter.xy.LinearXYAxis;
import plotter.xy.LinearXYPlotLine;
import plotter.xy.SimpleXYDataset;
import plotter.xy.SlopeLine;
import plotter.xy.SlopeLineDisplay;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;
import plotter.xy.XYGrid;
import plotter.xy.XYLocationDisplay;
import plotter.xy.XYPlot;
import plotter.xy.XYPlotContents;

public class InvertedAxes {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new GridLayout(2, 2));
		JPanel[] panels = new JPanel[4];
		SimpleXYDataset[] datasets = new SimpleXYDataset[panels.length];
		for(int i = 0; i < panels.length; i++) {
			panels[i] = new JPanel();
			contentPane.add(panels[i]);
			datasets[i] = createPlot(panels[i], i % 2 == 1, i > 1);
			for(int j = 0; j < 100; j++) {
				datasets[i].add(j/20.0, Math.exp(j/20.0));
			}
		}

		frame.setSize(400, 300);
		frame.setVisible(true);
	}


	private static SimpleXYDataset createPlot(Container contentPane, boolean invertX, boolean invertY) {
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

		new DefaultXYLayoutGenerator().generateLayout(plot);

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		contentPane.add(plot);

		final LinearXYPlotLine line = new LinearXYPlotLine(xAxis, yAxis, XYDimension.X);
		line.setForeground(Color.white);
		final SimpleXYDataset d = new SimpleXYDataset(line);
		d.setMaxCapacity(10000);
		d.setXData(line.getXData());
		d.setYData(line.getYData());
		contents.add(line);
		contents.setComponentZOrder(grid, contents.getComponentCount() - 1);

		if(invertY) {
			yAxis.setStart(200);
			yAxis.setEnd(0);
		} else {
			yAxis.setStart(0);
			yAxis.setEnd(200);
		}
		if(invertX) {
			xAxis.setStart(5);
			xAxis.setEnd(0);
		} else {
			xAxis.setStart(0);
			xAxis.setEnd(5);
		}
		return d;
	}
}
