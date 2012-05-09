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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import junit.framework.TestCase;
import plotter.CountingGraphics;
import plotter.LineChecker;

public class JUnitXYAxis extends TestCase {
	private static class TestAxis extends XYAxis {
		private static final long serialVersionUID = 1L;

		private TestAxis(XYDimension d) {
			super(d);
		}


		@Override
		public double toLogical(int n) {
			return n;
		}


		@Override
		public int toPhysical(double d) {
			return (int) d;
		}
	}


	public void testPaintX() throws InterruptedException, InvocationTargetException {
		XYAxis axis = createAxis(XYDimension.X);
		axis.setStartMargin(10);
		CountingGraphics g = paint(axis);
		assertEquals(12, g.getPointCount());

		LineChecker c = new LineChecker();

		// main line
		c.require(10, 0, 199, 0);

		// major tick marks
		c.require(10, 0, 10, 5);
		c.require(110, 0, 110, 5);
		c.require(210, 0, 210, 5);

		// minor tick marks
		c.require(20, 0, 20, 3);
		c.require(30, 0, 30, 3);

		c.check(g.getLines());
	}


	public void testPaintXNoStartMargin() throws InterruptedException, InvocationTargetException {
		XYAxis axis = createAxis(XYDimension.X);
		axis.setStartMargin(0);
		CountingGraphics g = paint(axis);
		assertEquals(12, g.getPointCount());

		LineChecker c = new LineChecker();

		// main line
		c.require(0, 0, 199, 0);

		// major tick marks
		c.require(0, 0, 0, 5);
		c.require(100, 0, 100, 5);
		c.require(200, 0, 200, 5);

		// minor tick marks
		c.require(10, 0, 10, 3);
		c.require(20, 0, 20, 3);

		c.check(g.getLines());
	}


	public void testPaintY() throws InterruptedException, InvocationTargetException {
		XYAxis axis = createAxis(XYDimension.Y);
		axis.setStartMargin(10);
		CountingGraphics g = paint(axis);
		assertEquals(12, g.getPointCount());

		LineChecker c = new LineChecker();

		// main line
		c.require(199, 189, 199, 0);

		// major tick marks
		c.require(194, 189, 199, 189);
		c.require(194, 89, 199, 89);
		c.require(194, -11, 199, -11);

		// minor tick marks
		c.require(196, 179, 199, 179);
		c.require(196, 169, 199, 169);

		c.check(g.getLines());
	}


	public void testPaintYNoStartMargin() throws InterruptedException, InvocationTargetException {
		XYAxis axis = createAxis(XYDimension.Y);
		axis.setStartMargin(0);
		CountingGraphics g = paint(axis);
		assertEquals(12, g.getPointCount());

		LineChecker c = new LineChecker();

		// main line
		c.require(199, 199, 199, 0);

		// major tick marks
		c.require(194, 199, 199, 199);
		c.require(194, 99, 199, 99);
		c.require(194, -1, 199, -1);

		// minor tick marks
		c.require(196, 189, 199, 189);
		c.require(196, 179, 199, 179);

		c.check(g.getLines());
	}


	private XYAxis createAxis(XYDimension dimension) {
		XYAxis axis = new TestAxis(dimension);
		axis.setStart(0);
		axis.setEnd(200);
		axis.setMajorTickLength(5);
		axis.setMinorTickLength(3);
		axis.setMajorTicks(new int[] {0, 100, 200});
		axis.setMinorTicks(new int[] {10, 20});
		return axis;
	}


	private CountingGraphics paint(XYAxis axis) throws InterruptedException, InvocationTargetException {
		return paint(axis, null);
	}


	// Ensures that the line is 200x200, paints it, and returns the stats.
	// We can't use 100x100 because on Mac OSX, the minimum frame width is 128.
	private CountingGraphics paint(final XYAxis axis, Shape clip) throws InterruptedException, InvocationTargetException {
		final JFrame frame = new JFrame();
		frame.getContentPane().add(axis);
		BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_3BYTE_BGR);
		final Graphics2D imageG = image.createGraphics();
		final CountingGraphics g = new CountingGraphics(imageG);
		try {
			if(clip != null) {
				g.setClip(clip);
			}
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						frame.pack();
						int xo = 200 - axis.getWidth();
						int yo = 200 - axis.getHeight();
						frame.setSize(frame.getWidth() + xo, frame.getHeight() + yo);
						frame.validate();

						// These are sanity checks to make sure our setup is correct, not actual functionality tests.
						assertEquals(200, axis.getWidth());
						assertEquals(200, axis.getHeight());

						axis.paint(g);
					} finally {
						frame.dispose();
					}
				}
			});
		} finally {
			imageG.dispose();
		}
		return g;
	}
}
