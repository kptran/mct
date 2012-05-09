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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import junit.framework.TestCase;

public class JUnitXYPlot extends TestCase {
	public void testToPhysical() {
		XYPlot plot = new XYPlot();
		LinearXYAxis xAxis = new LinearXYAxis(XYDimension.X);
		LinearXYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		plot.setXAxis(xAxis);
		plot.setYAxis(yAxis);
		xAxis.setStart(1);
		xAxis.setEnd(2);
		yAxis.setStart(2);
		yAxis.setEnd(3);
		xAxis.setSize(200, 1);
		yAxis.setSize(1, 200);
		Point2D src = new Point2D.Double(1.1, 2.2);
		plot.toPhysical(src, src);
		assertEquals(new Point2D.Double(19, 160), src);
	}


	public void testPaintComponent() {
		XYPlot plot = new XYPlot();
		plot.setBackground(new Color(1, 2, 3));
		plot.setSize(100, 100);
		BufferedImage image = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = image.getGraphics();
		plot.paintComponent(g);
		g.dispose();

		WritableRaster raster = image.getRaster();
		int[] data = new int[3];
		for(int x = 0; x < 100; x++) {
			for(int y = 0; y < 100; y++) {
				raster.getPixel(x, y, data);
				assertEquals(1, data[0]);
				assertEquals(2, data[1]);
				assertEquals(3, data[2]);
			}
		}
	}
}
