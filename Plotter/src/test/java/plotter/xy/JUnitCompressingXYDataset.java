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

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import plotter.DoubleData;
import plotter.PropertyTester;
import plotter.xy.CompressingXYDataset.MinMaxChangeListener;

public class JUnitCompressingXYDataset extends TestCase {
	public void testPrepend() {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		double[] x = new double[] { .1, .2, .3, 1.1, 1.2 };
		double[] y = new double[] { 2.1, 2.2, 2.3, 3.1, 3.2 };
		dataset.prepend(x, 0, y, 0, x.length);
		DoubleData xData = dataset.getXData();
		DoubleData yData = dataset.getYData();
		assertEquals(4, xData.getLength());
		assertEquals(4, yData.getLength());
		assertEquals(0.0, xData.get(0));
		assertEquals(2.1, yData.get(0));
		assertEquals(0.0, xData.get(1));
		assertEquals(2.3, yData.get(1));
		assertEquals(1.0, xData.get(2));
		assertEquals(3.1, yData.get(2));
		assertEquals(1.0, xData.get(3));
		assertEquals(3.2, yData.get(3));
		assertEquals(.1, dataset.getMinX());
		assertEquals(1.2, dataset.getMaxX());
		assertEquals(2.1, dataset.getMinY());
		assertEquals(3.2, dataset.getMaxY());
	}


	public void testPrependYIndependent() {
		CompressingXYDataset dataset = createDataset(XYDimension.Y);
		double[] y = new double[] { .1, .2, .3, 1.1, 1.2 };
		double[] x = new double[] { 2.1, 2.2, 2.3, 3.1, 3.2 };
		dataset.prepend(x, 0, y, 0, x.length);
		DoubleData xData = dataset.getXData();
		DoubleData yData = dataset.getYData();
		assertEquals(4, yData.getLength());
		assertEquals(4, xData.getLength());
		assertEquals(0.0, yData.get(0));
		assertEquals(2.1, xData.get(0));
		assertEquals(0.0, yData.get(1));
		assertEquals(2.3, xData.get(1));
		assertEquals(1.0, yData.get(2));
		assertEquals(3.1, xData.get(2));
		assertEquals(1.0, yData.get(3));
		assertEquals(3.2, xData.get(3));
		assertEquals(.1, dataset.getMinY());
		assertEquals(1.2, dataset.getMaxY());
		assertEquals(2.1, dataset.getMinX());
		assertEquals(3.2, dataset.getMaxX());
	}


	public void testAdd() {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		double[] x = new double[] { .1, .2, .3, 1.1, 1.2 };
		double[] y = new double[] { 2.1, 2.2, 2.3, 3.1, 3.2 };
		for(int i = 0; i < x.length; i++) {
			dataset.add(x[i], y[i]);
		}
		DoubleData xData = dataset.getXData();
		DoubleData yData = dataset.getYData();
		assertEquals(4, xData.getLength());
		assertEquals(4, yData.getLength());
		assertEquals(0.0, xData.get(0));
		assertEquals(2.1, yData.get(0));
		assertEquals(0.0, xData.get(1));
		assertEquals(2.3, yData.get(1));
		assertEquals(1.0, xData.get(2));
		assertEquals(3.1, yData.get(2));
		assertEquals(1.0, xData.get(3));
		assertEquals(3.2, yData.get(3));
		assertEquals(.1, dataset.getMinX());
		assertEquals(1.2, dataset.getMaxX());
		assertEquals(2.1, dataset.getMinY());
		assertEquals(3.2, dataset.getMaxY());
	}


	public void testAddYIndependent() {
		CompressingXYDataset dataset = createDataset(XYDimension.Y);
		double[] y = new double[] { .1, .2, .3, 1.1, 1.2 };
		double[] x = new double[] { 2.1, 2.2, 2.3, 3.1, 3.2 };
		for(int i = 0; i < x.length; i++) {
			dataset.add(x[i], y[i]);
		}
		DoubleData xData = dataset.getXData();
		DoubleData yData = dataset.getYData();
		assertEquals(4, yData.getLength());
		assertEquals(4, xData.getLength());
		assertEquals(0.0, yData.get(0));
		assertEquals(2.1, xData.get(0));
		assertEquals(0.0, yData.get(1));
		assertEquals(2.3, xData.get(1));
		assertEquals(1.0, yData.get(2));
		assertEquals(3.1, xData.get(2));
		assertEquals(1.0, yData.get(3));
		assertEquals(3.2, xData.get(3));
		assertEquals(.1, dataset.getMinY());
		assertEquals(1.2, dataset.getMaxY());
		assertEquals(2.1, dataset.getMinX());
		assertEquals(3.2, dataset.getMaxX());
	}


	public void testMinMaxListeners() {
		final CompressingXYDataset dataset = createDataset(XYDimension.X);
		class TestListener implements MinMaxChangeListener {
			private final XYDimension dimension;
			int count;


			TestListener(XYDimension dimension) {
				this.dimension = dimension;
			}


			@Override
			public void minMaxChanged(CompressingXYDataset dataset2, XYDimension dimension) {
				assertSame(dataset, dataset2);
				assertEquals(this.dimension, dimension);
				count++;
			}
		}
		TestListener x = new TestListener(XYDimension.X);
		dataset.addXMinMaxChangeListener(x);
		TestListener y = new TestListener(XYDimension.Y);
		dataset.addYMinMaxChangeListener(y);

		dataset.add(0, 0);
		assertEquals(1, x.count);
		assertEquals(1, y.count);

		dataset.add(1, 1);
		assertEquals(2, x.count);
		assertEquals(2, y.count);

		dataset.add(2, 0.5);
		assertEquals(3, x.count);
		assertEquals(2, y.count);

		dataset.prepend(new double[] { -3, -2, -1 }, 0, new double[] { -1, 4, 5 }, 0, 3);
		assertEquals(4, x.count);
		assertEquals(3, y.count);

		dataset.prepend(new double[] { -6, -5, -4 }, 0, new double[] { 0, 0, 0 }, 0, 3);
		assertEquals(5, x.count);
		assertEquals(3, y.count);

		dataset.removeAllPoints();
		assertEquals(6, x.count);
		assertEquals(4, y.count);
	}


	public void testTruncate() {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		dataset.add(0, 1);
		dataset.add(1, 2);
		dataset.add(2, 3);
		dataset.setTruncationPoint(1.5);
		dataset.setTruncationOffset(0);
		dataset.add(3, 4);
		dataset.add(4, 5);
		assertEquals(2.0, dataset.getMinX());
		assertEquals(4.0, dataset.getMaxX());
		assertEquals(3.0, dataset.getMinY());
		assertEquals(5.0, dataset.getMaxY());
	}


	public void testTruncateYIndependent() {
		CompressingXYDataset dataset = createDataset(XYDimension.Y);
		dataset.add(0, 1);
		dataset.add(1, 2);
		dataset.add(2, 3);
		dataset.setTruncationPoint(2.5);
		dataset.setTruncationOffset(0);
		dataset.add(3, 4);
		dataset.add(4, 5);
		assertEquals(2.0, dataset.getMinX());
		assertEquals(4.0, dataset.getMaxX());
		assertEquals(3.0, dataset.getMinY());
		assertEquals(5.0, dataset.getMaxY());
	}


	public void testTruncateWithOffset() {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		dataset.add(0, 1);
		dataset.add(1, 2);
		dataset.add(2, 3);
		dataset.setTruncationPoint(1.5);
		dataset.setTruncationOffset(1);
		dataset.add(3, 4);
		dataset.add(4, 5);
		assertEquals(1.0, dataset.getMinX());
		assertEquals(4.0, dataset.getMaxX());
		assertEquals(2.0, dataset.getMinY());
		assertEquals(5.0, dataset.getMaxY());
	}


	public void testTruncateWithOffsetYIndependent() {
		CompressingXYDataset dataset = createDataset(XYDimension.Y);
		dataset.add(0, 1);
		dataset.add(1, 2);
		dataset.add(2, 3);
		dataset.setTruncationPoint(2.5);
		dataset.setTruncationOffset(1);
		dataset.add(3, 4);
		dataset.add(4, 5);
		assertEquals(1.0, dataset.getMinX());
		assertEquals(4.0, dataset.getMaxX());
		assertEquals(2.0, dataset.getMinY());
		assertEquals(5.0, dataset.getMaxY());
	}


	public void testRemoveAll() {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		dataset.add(0, 0);
		dataset.add(1, 1);
		dataset.removeAllPoints();
		assertEquals(0, dataset.getPointCount());
		assertEquals(Double.POSITIVE_INFINITY, dataset.getMinX());
		assertEquals(Double.NEGATIVE_INFINITY, dataset.getMaxX());
		assertEquals(Double.POSITIVE_INFINITY, dataset.getMinY());
		assertEquals(Double.NEGATIVE_INFINITY, dataset.getMaxY());
	}


	public void testRecompress() {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		for(int i = 0; i < 10; i++) {
			dataset.add(i, i);
		}
		dataset.recompress();
		assertEquals(10, dataset.getPointCount());

		dataset.setCompressionScale(5);
		dataset.recompress();
		assertEquals(4, dataset.getPointCount());
	}


	public void testProperties() throws InvocationTargetException, IllegalAccessException, IntrospectionException {
		CompressingXYDataset dataset = createDataset(XYDimension.X);
		PropertyTester t = new PropertyTester(dataset);
		t.test("truncationPoint", -1.1, 0.0, 1.1);
		t.test("truncationOffset", 0, 1, 10);
		t.test("compressionScale", .1, 1.0, 10.0);
		t.test("compressionOffset", -10.0, 0.0, 10.0);
	}


	private CompressingXYDataset createDataset(XYDimension independentDimension) {
		XYAxis xAxis = new LinearXYAxis(XYDimension.X);
		XYAxis yAxis = new LinearXYAxis(XYDimension.Y);
		LinearXYPlotLine plotLine = new LinearXYPlotLine(xAxis, yAxis, independentDimension);
		XYPlotContents contents = new XYPlotContents();
		contents.add(plotLine);
		CompressingXYDataset dataset = new CompressingXYDataset(plotLine, new DefaultCompressor());
		dataset.setCompressionOffset(0);
		dataset.setCompressionScale(1);
		return dataset;
	}
}
