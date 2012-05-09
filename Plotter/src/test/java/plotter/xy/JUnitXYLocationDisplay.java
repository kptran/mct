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

import java.awt.event.MouseEvent;
import java.text.MessageFormat;

import junit.framework.TestCase;

public class JUnitXYLocationDisplay extends TestCase {
	public void testFailIfNoContents() {
		XYLocationDisplay display = new XYLocationDisplay();
		XYPlot plot = new XYPlot();
		try {
			display.attach(plot);
			fail("Should throw an exception");
		} catch(IllegalArgumentException e) {
			// should happen
		}
	}


	public void testGeneral() {
		XYLocationDisplay display = new XYLocationDisplay();
		MessageFormat format = new MessageFormat("{0} {1}");
		display.setFormat(format);
		assertSame(format, display.getFormat());

		XYPlot plot = new XYPlot();
		XYPlotContents contents = new XYPlotContents();
		plot.add(contents);
		LinearXYAxis xAxis = new LinearXYAxis(XYDimension.X);
		LinearXYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		plot.add(xAxis);
		plot.add(yAxis);
		plot.setXAxis(xAxis);
		plot.setYAxis(yAxis);
		contents.setSize(100, 100);
		xAxis.setSize(100, 1);
		yAxis.setSize(1, 100);
		xAxis.setStartMargin(0);
		yAxis.setStartMargin(0);
		xAxis.setStart(0);
		xAxis.setEnd(100);
		yAxis.setStart(0);
		yAxis.setEnd(100);
		display.attach(plot);

		// The mouse should not affect the display until it enters
		display.mouseMoved(new MouseEvent(contents, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
		assertEquals("", display.getText());

		display.mouseEntered(new MouseEvent(contents, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, 0, 0, 0, false));

		display.mouseMoved(new MouseEvent(contents, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 25, 25, 0, false));
		assertEquals("26 75", display.getText());

		display.mouseDragged(new MouseEvent(contents, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 75, 75, 0, false));
		assertEquals("76 25", display.getText());

		// These aren't used, but make sure they don't affect anything or throw exceptions
		display.mousePressed(new MouseEvent(contents, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 0, 0, 0, false));
		display.mouseReleased(new MouseEvent(contents, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 0, false));
		display.mouseClicked(new MouseEvent(contents, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 0, false));

		// Exiting should always clear the display
		display.mouseExited(new MouseEvent(contents, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, false));
		assertEquals("", display.getText());
	}
}
