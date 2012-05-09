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
import java.awt.Component;
import java.awt.Font;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.swing.JLabel;

import junit.framework.TestCase;
import plotter.DoubleDiffer;
import plotter.IntegerTickMarkCalculator;
import plotter.PropertyTester;


public class JUnitLinearXYAxis extends TestCase {
	public void testToLogicalX() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setSize(100, 10);
		axis.setStart(1);
		axis.setEnd(2);
		DoubleDiffer d = new DoubleDiffer(.0000001);
		d.assertClose(1, axis.toLogical(-1));
		d.assertClose(1.5, axis.toLogical(49));
		d.assertClose(2, axis.toLogical(99));

		axis.setSize(130, 10);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		d.assertClose(1, axis.toLogical(9));
		d.assertClose(1.5, axis.toLogical(59));
		d.assertClose(2, axis.toLogical(109));
	}


	public void testToLogicalXInverted() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setSize(100, 10);
		axis.setStart(2);
		axis.setEnd(1);
		DoubleDiffer d = new DoubleDiffer(.0000001);
		d.assertClose(2, axis.toLogical(-1));
		d.assertClose(1.5, axis.toLogical(49));
		d.assertClose(1, axis.toLogical(99));

		axis.setSize(130, 10);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		d.assertClose(2, axis.toLogical(9));
		d.assertClose(1.5, axis.toLogical(59));
		d.assertClose(1, axis.toLogical(109));
	}


	public void testToLogicalY() {
		DoubleDiffer d = new DoubleDiffer(.0000001);
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setSize(10, 100);
		axis.setStart(1);
		axis.setEnd(2);
		d.assertClose(2, axis.toLogical(0));
		d.assertClose(1.5, axis.toLogical(50));
		d.assertClose(1, axis.toLogical(100));

		axis.setSize(10, 130);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		d.assertClose(2, axis.toLogical(20));
		d.assertClose(1.5, axis.toLogical(70));
		d.assertClose(1, axis.toLogical(120));
	}


	public void testToLogicalYInverted() {
		DoubleDiffer d = new DoubleDiffer(.0000001);
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setSize(10, 100);
		axis.setStart(2);
		axis.setEnd(1);
		d.assertClose(1, axis.toLogical(0));
		d.assertClose(1.5, axis.toLogical(50));
		d.assertClose(2, axis.toLogical(100));

		axis.setSize(10, 130);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		d.assertClose(1, axis.toLogical(20));
		d.assertClose(1.5, axis.toLogical(70));
		d.assertClose(2, axis.toLogical(120));
	}


	public void testToPhysicalX() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setSize(100, 10);
		axis.setStart(1);
		axis.setEnd(2);
		assertEquals(-1, axis.toPhysical(1));
		assertEquals(49, axis.toPhysical(1.5));
		assertEquals(99, axis.toPhysical(2));

		axis.setSize(130, 10);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		assertEquals(9, axis.toPhysical(1));
		assertEquals(59, axis.toPhysical(1.5));
		assertEquals(109, axis.toPhysical(2));
	}


	public void testToPhysicalXInverted() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setSize(100, 10);
		axis.setStart(2);
		axis.setEnd(1);
		assertEquals(99, axis.toPhysical(1));
		assertEquals(49, axis.toPhysical(1.5));
		assertEquals(-1, axis.toPhysical(2));

		axis.setSize(130, 10);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		assertEquals(109, axis.toPhysical(1));
		assertEquals(59, axis.toPhysical(1.5));
		assertEquals(9, axis.toPhysical(2));
	}


	public void testToPhysicalY() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setSize(10, 100);
		axis.setStart(1);
		axis.setEnd(2);
		assertEquals(100, axis.toPhysical(1));
		assertEquals(50, axis.toPhysical(1.5));
		assertEquals(0, axis.toPhysical(2));

		axis.setSize(10, 130);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		assertEquals(120, axis.toPhysical(1));
		assertEquals(70, axis.toPhysical(1.5));
		assertEquals(20, axis.toPhysical(2));
	}


	public void testToPhysicalYInverted() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setSize(10, 100);
		axis.setStart(2);
		axis.setEnd(1);
		assertEquals(0, axis.toPhysical(1));
		assertEquals(50, axis.toPhysical(1.5));
		assertEquals(100, axis.toPhysical(2));

		axis.setSize(10, 130);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		assertEquals(20, axis.toPhysical(1));
		assertEquals(70, axis.toPhysical(1.5));
		assertEquals(120, axis.toPhysical(2));
	}


	public void testTickMarksX() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setSize(100, 10);
		axis.setStart(1);
		axis.setEnd(2);
		axis.doLayout();

		int[] majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(-1, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(99, majorTicks[2]);

		Component[] components = axis.getComponents();
		assertEquals(Arrays.toString(components), 3, components.length);
		assertEquals("1", ((JLabel) components[0]).getText());
		assertEquals("1.5", ((JLabel) components[1]).getText());
		assertEquals("2", ((JLabel) components[2]).getText());

		int[] minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(-1, minorTicks[0]);
		assertEquals(9, minorTicks[1]);
		assertEquals(19, minorTicks[2]);
		assertEquals(29, minorTicks[3]);
		assertEquals(39, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(59, minorTicks[6]);
		assertEquals(69, minorTicks[7]);
		assertEquals(79, minorTicks[8]);
		assertEquals(89, minorTicks[9]);
		assertEquals(99, minorTicks[10]);

		axis.setSize(130, 10);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		axis.doLayout();

		majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(-1, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(99, majorTicks[2]);

		minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(-1, minorTicks[0]);
		assertEquals(9, minorTicks[1]);
		assertEquals(19, minorTicks[2]);
		assertEquals(29, minorTicks[3]);
		assertEquals(39, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(59, minorTicks[6]);
		assertEquals(69, minorTicks[7]);
		assertEquals(79, minorTicks[8]);
		assertEquals(89, minorTicks[9]);
		assertEquals(99, minorTicks[10]);
	}


	public void testTickMarksXInverted() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setSize(100, 10);
		axis.setStart(2);
		axis.setEnd(1);
		axis.doLayout();

		int[] majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(99, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(-1, majorTicks[2]);

		Component[] components = axis.getComponents();
		assertEquals(Arrays.toString(components), 3, components.length);
		assertEquals("1", ((JLabel) components[0]).getText());
		assertEquals("1.5", ((JLabel) components[1]).getText());
		assertEquals("2", ((JLabel) components[2]).getText());

		int[] minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(99, minorTicks[0]);
		assertEquals(89, minorTicks[1]);
		assertEquals(79, minorTicks[2]);
		assertEquals(69, minorTicks[3]);
		assertEquals(59, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(39, minorTicks[6]);
		assertEquals(29, minorTicks[7]);
		assertEquals(19, minorTicks[8]);
		assertEquals(9, minorTicks[9]);
		assertEquals(-1, minorTicks[10]);

		axis.setSize(130, 10);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		axis.doLayout();

		majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(99, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(-1, majorTicks[2]);

		minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(99, minorTicks[0]);
		assertEquals(89, minorTicks[1]);
		assertEquals(79, minorTicks[2]);
		assertEquals(69, minorTicks[3]);
		assertEquals(59, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(39, minorTicks[6]);
		assertEquals(29, minorTicks[7]);
		assertEquals(19, minorTicks[8]);
		assertEquals(9, minorTicks[9]);
		assertEquals(-1, minorTicks[10]);
	}


	public void testTickMarksY() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setSize(10, 100);
		axis.setStart(1);
		axis.setEnd(2);
		axis.doLayout();

		int[] majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(-1, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(99, majorTicks[2]);

		Component[] components = axis.getComponents();
		assertEquals(Arrays.toString(components), 3, components.length);
		assertEquals("1", ((JLabel) components[0]).getText());
		assertEquals("1.5", ((JLabel) components[1]).getText());
		assertEquals("2", ((JLabel) components[2]).getText());

		int[] minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(-1, minorTicks[0]);
		assertEquals(9, minorTicks[1]);
		assertEquals(19, minorTicks[2]);
		assertEquals(29, minorTicks[3]);
		assertEquals(39, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(59, minorTicks[6]);
		assertEquals(69, minorTicks[7]);
		assertEquals(79, minorTicks[8]);
		assertEquals(89, minorTicks[9]);
		assertEquals(99, minorTicks[10]);

		axis.setSize(10, 130);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		axis.doLayout();

		majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(-1, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(99, majorTicks[2]);

		minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(-1, minorTicks[0]);
		assertEquals(9, minorTicks[1]);
		assertEquals(19, minorTicks[2]);
		assertEquals(29, minorTicks[3]);
		assertEquals(39, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(59, minorTicks[6]);
		assertEquals(69, minorTicks[7]);
		assertEquals(79, minorTicks[8]);
		assertEquals(89, minorTicks[9]);
		assertEquals(99, minorTicks[10]);

	}


	public void testTickMarksYInverted() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.Y);
		axis.setSize(10, 100);
		axis.setStart(2);
		axis.setEnd(1);
		axis.doLayout();

		int[] majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(99, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(-1, majorTicks[2]);

		Component[] components = axis.getComponents();
		assertEquals(Arrays.toString(components), 3, components.length);
		assertEquals("1", ((JLabel) components[0]).getText());
		assertEquals("1.5", ((JLabel) components[1]).getText());
		assertEquals("2", ((JLabel) components[2]).getText());

		int[] minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(99, minorTicks[0]);
		assertEquals(89, minorTicks[1]);
		assertEquals(79, minorTicks[2]);
		assertEquals(69, minorTicks[3]);
		assertEquals(59, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(39, minorTicks[6]);
		assertEquals(29, minorTicks[7]);
		assertEquals(19, minorTicks[8]);
		assertEquals(9, minorTicks[9]);
		assertEquals(-1, minorTicks[10]);

		axis.setSize(10, 130);
		axis.setStartMargin(10);
		axis.setEndMargin(20);
		axis.doLayout();

		majorTicks = axis.getMajorTicks();
		assertEquals(Arrays.toString(majorTicks), 3, majorTicks.length);
		assertEquals(99, majorTicks[0]);
		assertEquals(49, majorTicks[1]);
		assertEquals(-1, majorTicks[2]);

		minorTicks = axis.getMinorTicks();
		assertEquals(11, minorTicks.length);
		assertEquals(99, minorTicks[0]);
		assertEquals(89, minorTicks[1]);
		assertEquals(79, minorTicks[2]);
		assertEquals(69, minorTicks[3]);
		assertEquals(59, minorTicks[4]);
		assertEquals(49, minorTicks[5]);
		assertEquals(39, minorTicks[6]);
		assertEquals(29, minorTicks[7]);
		assertEquals(19, minorTicks[8]);
		assertEquals(9, minorTicks[9]);
		assertEquals(-1, minorTicks[10]);
	}


	public void testShift() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		axis.setStart(1);
		axis.setEnd(2);
		axis.shift(5);
		assertEquals(6.0, axis.getStart());
		assertEquals(7.0, axis.getEnd());
	}


	public void testSetFont() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		JLabel label = new JLabel();
		axis.add(label);
		Font font = new Font("Helvetica", Font.ITALIC, 72);
		axis.setFont(font);
		assertSame(font, axis.getFont());
		assertSame(font, label.getFont());
	}


	public void testSetForeground() {
		LinearXYAxis axis = new LinearXYAxis(XYDimension.X);
		JLabel label = new JLabel();
		axis.add(label);
		Color foreground = Color.cyan;
		axis.setForeground(foreground);
		assertSame(foreground, axis.getForeground());
		assertSame(foreground, label.getForeground());
	}


	public void testProperties() throws InvocationTargetException, IllegalAccessException, IntrospectionException {
		PropertyTester p = new PropertyTester(new LinearXYAxis(XYDimension.X));
		p.test("textMargin", 0, 1);
		p.test("showLabels", true, false);
		p.test("minorTickLength", 0, 1);
		p.test("majorTickLength", 0, 1);
		p.test("tickMarkCalculator", new IntegerTickMarkCalculator());
		p.test("format", new DecimalFormat("0.00"));
	}
}
