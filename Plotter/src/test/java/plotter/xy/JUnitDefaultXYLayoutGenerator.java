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

import java.awt.Dimension;

import junit.framework.TestCase;

public class JUnitDefaultXYLayoutGenerator extends TestCase {
	public void testBasic() {
		XYPlot plot = new XYPlot();
		XYPlotContents contents = new XYPlotContents();
		plot.add(contents);

		new DefaultXYLayoutGenerator().generateLayout(plot);
		plot.setSize(100, 100);
		plot.doLayout();

		assertEquals(0, contents.getX());
		assertEquals(0, contents.getY());
		assertEquals(100, contents.getWidth());
		assertEquals(100, contents.getHeight());
	}


	public void testNormal() {
		XYPlot plot = new XYPlot();
		XYPlotContents contents = new XYPlotContents();
		plot.add(contents);

		LinearXYAxis xAxis = new LinearXYAxis(XYDimension.X);
		plot.add(xAxis);
		xAxis.setPreferredSize(new Dimension(1, 10));

		LinearXYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		plot.add(yAxis);
		yAxis.setPreferredSize(new Dimension(10, 1));

		new DefaultXYLayoutGenerator().generateLayout(plot);
		plot.setSize(100, 100);
		plot.doLayout();

		assertEquals(10, contents.getX());
		assertEquals(0, contents.getY());
		assertEquals(90, contents.getWidth());
		assertEquals(90, contents.getHeight());

		assertEquals(0, xAxis.getX());
		assertEquals(90, xAxis.getY());
		assertEquals(100, xAxis.getWidth());
		assertEquals(10, xAxis.getHeight());

		assertEquals(0, yAxis.getX());
		assertEquals(0, yAxis.getY());
		assertEquals(10, yAxis.getWidth());
		assertEquals(100, yAxis.getHeight());
	}


	public void testFull() {
		XYPlot plot = new XYPlot();
		XYPlotContents contents = new XYPlotContents();
		plot.add(contents);

		LinearXYAxis xAxis = new LinearXYAxis(XYDimension.X);
		plot.add(xAxis);
		xAxis.setPreferredSize(new Dimension(1, 10));

		LinearXYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		plot.add(yAxis);
		yAxis.setPreferredSize(new Dimension(10, 1));

		SlopeLineDisplay slopeDisplay = new SlopeLineDisplay();
		plot.add(slopeDisplay);
		slopeDisplay.setPreferredSize(new Dimension(1, 10));

		XYLocationDisplay locationDisplay = new XYLocationDisplay();
		plot.add(locationDisplay);
		locationDisplay.setPreferredSize(new Dimension(1, 10));

		new DefaultXYLayoutGenerator().generateLayout(plot);
		plot.setSize(100, 100);
		plot.doLayout();

		assertEquals(10, contents.getX());
		assertEquals(10, contents.getY());
		assertEquals(90, contents.getWidth());
		assertEquals(80, contents.getHeight());

		assertEquals(0, xAxis.getX());
		assertEquals(90, xAxis.getY());
		assertEquals(100, xAxis.getWidth());
		assertEquals(10, xAxis.getHeight());

		assertEquals(0, yAxis.getX());
		assertEquals(0, yAxis.getY());
		assertEquals(10, yAxis.getWidth());
		assertEquals(100, yAxis.getHeight());

		assertEquals(10, locationDisplay.getX());
		assertEquals(0, locationDisplay.getY());
		assertEquals(45, locationDisplay.getWidth());
		assertEquals(10, locationDisplay.getHeight());

		assertEquals(55, slopeDisplay.getX());
		assertEquals(0, slopeDisplay.getY());
		assertEquals(45, slopeDisplay.getWidth());
		assertEquals(10, slopeDisplay.getHeight());
	}


	public void testSlopeDisplayNoLocation() {
		XYPlot plot = new XYPlot();
		XYPlotContents contents = new XYPlotContents();
		plot.add(contents);

		LinearXYAxis xAxis = new LinearXYAxis(XYDimension.X);
		plot.add(xAxis);
		xAxis.setPreferredSize(new Dimension(1, 10));

		LinearXYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		plot.add(yAxis);
		yAxis.setPreferredSize(new Dimension(10, 1));

		SlopeLineDisplay slopeDisplay = new SlopeLineDisplay();
		plot.add(slopeDisplay);
		slopeDisplay.setPreferredSize(new Dimension(1, 10));

		new DefaultXYLayoutGenerator().generateLayout(plot);
		plot.setSize(100, 100);
		plot.doLayout();

		assertEquals(10, contents.getX());
		assertEquals(10, contents.getY());
		assertEquals(90, contents.getWidth());
		assertEquals(80, contents.getHeight());

		assertEquals(0, xAxis.getX());
		assertEquals(90, xAxis.getY());
		assertEquals(100, xAxis.getWidth());
		assertEquals(10, xAxis.getHeight());

		assertEquals(0, yAxis.getX());
		assertEquals(0, yAxis.getY());
		assertEquals(10, yAxis.getWidth());
		assertEquals(100, yAxis.getHeight());

		assertEquals(55, slopeDisplay.getX());
		assertEquals(0, slopeDisplay.getY());
		assertEquals(45, slopeDisplay.getWidth());
		assertEquals(10, slopeDisplay.getHeight());
	}
}
