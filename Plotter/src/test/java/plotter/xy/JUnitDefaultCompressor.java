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

import junit.framework.TestCase;
import plotter.DoubleData;
import plotter.xy.Compressor.StreamingCompressor;

public class JUnitDefaultCompressor extends TestCase {
	private PointData indata;

	private PointData expected;

	private DefaultCompressor compressor;

	private PointData outdata;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		indata = new PointData();
		expected = new PointData();
		compressor = new DefaultCompressor();
		outdata = new PointData();
	}


	public void testCompress0() {
		check();
	}


	public void testCompress1() {
		in(.5, 1);
		out(0, 1);
		check();
	}


	public void testCompress1_NaN() {
		in(.5, Double.NaN);
		out(0, Double.NaN);
		check();
	}


	public void testCompress2() {
		in(.5, 1);
		in(.6, 2);
		out(0, 1);
		out(0, 2);
		check();
	}


	public void testCompress2_2() {
		in(.5, 2);
		in(.6, 1);
		out(0, 2);
		out(0, 1);
		check();
	}


	public void testCompress2_NaN() {
		in(.5, 1);
		in(.6, Double.NaN);
		out(0, 1);
		out(0, Double.NaN);
		check();
	}


	public void testCompress2_NaN2() {
		in(.5, Double.NaN);
		in(.6, 1);
		out(0, Double.NaN);
		out(0, 1);
		check();
	}


	public void testCompress3() {
		in(.5, 1);
		in(.6, 2);
		in(.7, 3);
		out(0, 1);
		out(0, 3);
		check();
	}


	public void testCompress3_2() {
		in(.5, 1);
		in(.6, 3);
		in(.7, 2);
		out(0, 1);
		out(0, 3);
		out(0, 2);
		check();
	}


	public void testCompress3_3() {
		in(.5, 2);
		in(.6, 1);
		in(.7, 3);
		out(0, 2);
		out(0, 1);
		out(0, 3);
		check();
	}


	public void testCompress3_4() {
		in(.5, 2);
		in(.6, 3);
		in(.7, 1);
		out(0, 2);
		out(0, 3);
		out(0, 1);
		check();
	}


	public void testCompress3_5() {
		in(.5, 3);
		in(.6, 1);
		in(.7, 2);
		out(0, 3);
		out(0, 1);
		out(0, 2);
		check();
	}


	public void testCompress3_6() {
		in(.5, 3);
		in(.6, 2);
		in(.7, 1);
		out(0, 3);
		out(0, 1);
		check();
	}


	public void testCompress3_NaN() {
		in(.5, Double.NaN);
		in(.6, 2);
		in(.7, 3);
		out(0, Double.NaN);
		out(0, 2);
		out(0, 3);
		check();
	}


	public void testCompress3_NaN2() {
		in(.5, 1);
		in(.6, Double.NaN);
		in(.7, 3);
		out(0, 1);
		out(0, Double.NaN);
		out(0, 3);
		check();
	}


	public void testCompress3_NaN3() {
		in(.5, 1);
		in(.6, 2);
		in(.7, Double.NaN);
		out(0, 1);
		out(0, 2);
		out(0, Double.NaN);
		check();
	}


	public void testCompress3_NaN4() {
		in(.5, Double.NaN);
		in(.6, 2);
		in(.7, Double.NaN);
		out(0, Double.NaN);
		out(0, 2);
		out(0, Double.NaN);
		check();
	}


	public void testCompress() {
		in(.51, 3);
		in(.52, 4);
		in(.53, 5);
		in(.54, 6);
		in(.55, 7);
		in(.56, 1);
		in(.57, 2);
		out(0, 3);
		out(0, 1);
		out(0, 7);
		out(0, 2);
		check();
	}


	public void testCompress_2() {
		in(.51, 1);
		in(.52, 2);
		in(.53, Double.NaN);
		in(.54, 3);
		in(.55, 4);
		out(0, 1);
		out(0, 2);
		out(0, Double.NaN);
		out(0, 3);
		out(0, 4);
		check();
	}


	public void testCompress_3() {
		in(.51, 2);
		in(.52, 1);
		in(.53, Double.NaN);
		in(.54, 4);
		in(.55, 3);
		out(0, 2);
		out(0, 1);
		out(0, Double.NaN);
		out(0, 4);
		out(0, 3);
		check();
	}


	public void testCompress_4() {
		in(.51, 4);
		in(.52, 3);
		in(.53, Double.NaN);
		in(.54, 2);
		in(.55, 1);
		out(0, 4);
		out(0, 3);
		out(0, Double.NaN);
		out(0, 2);
		out(0, 1);
		check();
	}


	public void testCompress_5() {
		in(.51, 3);
		in(.52, 4);
		in(.53, Double.NaN);
		in(.54, 1);
		in(.55, 2);
		out(0, 3);
		out(0, 4);
		out(0, Double.NaN);
		out(0, 1);
		out(0, 2);
		check();
	}


	public void testCompress_6() {
		in(1.2, -10);
		in(1.4, -5);
		in(1.6, Double.NaN);
		in(1.8, 5);
		out(1, -10);
		out(1, -5);
		out(1, Double.NaN);
		out(1, 5);
		check();
	}


	public void testCompress_7() {
		in(1.2, -10);
		in(1.4, Double.NaN);
		in(1.6, -5);
		in(1.8, 5);
		out(1, -10);
		out(1, Double.NaN);
		out(1, -5);
		out(1, 5);
		check();
	}


	public void testCompress_8() {
		in(1.2, -10);
		in(1.25, -9);
		in(1.3, Double.NaN);
		in(1.35, -7);
		in(1.4, -5);
		in(1.6, Double.NaN);
		in(1.8, 5);
		out(1, -10);
		out(1, -9);
		out(1, Double.NaN);
		out(1, -7);
		out(1, -5);
		out(1, Double.NaN);
		out(1, 5);
		check();
	}


	public void testCompress_9() {
		in(1.1, -10);
		in(1.2, Double.NaN);
		in(1.3, -7);
		in(1.4, -5);
		in(1.5, Double.NaN);
		in(1.6, -2);
		in(1.7, 5);
		out(1, -10);
		out(1, Double.NaN);
		out(1, -7);
		out(1, -5);
		out(1, Double.NaN);
		out(1, -2);
		out(1, 5);
		check();
	}


	public void testCompress10() {
		in(.3, Double.NaN);
		in(.4, 1);
		in(.5, Double.NaN);
		in(.6, 2);
		in(.7, Double.NaN);
		in(.8, 5);
		in(.9, Double.NaN);
		out(0, Double.NaN);
		out(0, 1);
		out(0, Double.NaN);
		out(0, 2);
		out(0, Double.NaN);
		out(0, 5);
		out(0, Double.NaN);
		check();
	}


	public void testCompress11() {
		in(0, 5);
		in(.2, Double.NaN);
		in(.3, -10);
		in(.4, -5);
		in(.5, Double.NaN);
		in(.7, 10);
		out(0, 5);
		out(0, Double.NaN);
		out(0, -10);
		out(0, -5);
		out(0, Double.NaN);
		out(0, 10);
		check();
	}


	public void testCompress12() {
		in(0.5, 5);
		in(1, 5);
		in(1.1, Double.NaN);
		in(1.2, -5);
		out(0, 5);
		out(1, 5);
		out(1, Double.NaN);
		out(1, -5);
		check();
	}


	public void testMultiBucket() {
		in(.5, 1);
		in(1.5, 2);
		out(0, 1);
		out(1, 2);
		check();
	}


	public void testMultiBucket2() {
		in(.5, 1);
		in(2.5, 2);
		in(4.5, 3);
		out(0, 1);
		out(2, 2);
		out(4, 3);
		check();
	}


	public void testMultiBucket3() {
		in(.5, 1);
		in(2.5, 2);
		in(2.6, 2.3);
		in(2.7, 2.5);
		in(4.5, 3);
		out(0, 1);
		out(2, 2);
		out(2, 2.5);
		out(4, 3);
		check();
	}


	public void testMultiBucket4() {
		in(.5, 1);
		in(1.4, Double.NaN);
		in(1.5, 2);
		out(0, 1);
		out(1, Double.NaN);
		out(1, 2);
		check();
	}


	public void testStreaming() {
		StreamingCompressor s = compressor.createStreamingCompressor(outdata, 0, 1);
		assertEquals(1, s.add(0, 1));
		expected.add(0, 1);
		checkNoCompress();

		assertEquals(2, s.add(.1, 2));
		expected.add(0, 2);
		checkNoCompress();

		assertEquals(2, s.add(.2, 3));
		expected.removeLast(2);
		expected.add(0, 1);
		expected.add(0, 3);
		checkNoCompress();

		assertEquals(3, s.add(.3, 2));
		expected.add(0, 2);
		checkNoCompress();

		assertEquals(3, s.add(.4, 2.5));
		expected.removeLast(1);
		expected.add(0, 2.5);
		checkNoCompress();

		assertEquals(4, s.add(1, 5));
		expected.add(1, 5);
		checkNoCompress();

		assertEquals(2, s.add(1, 6));
		expected.add(1, 6);
		checkNoCompress();
	}


	public void testFlatLine() {
		in(.51, 3);
		in(.52, 3);
		in(.53, 3);
		in(.54, 3);
		in(.55, 3);
		in(.56, 3);
		in(.57, 3);
		out(0, 3);
		check();
	}


	public void testFlatLineNaN() {
		in(.51, Double.NaN);
		in(.52, Double.NaN);
		in(.53, Double.NaN);
		in(.54, Double.NaN);
		in(.55, Double.NaN);
		in(.56, Double.NaN);
		in(.57, Double.NaN);
		out(0, Double.NaN);
		check();
	}


	private void in(double x, double y) {
		indata.add(x, y);
	}


	private void out(double x, double y) {
		expected.add(x, y);
	}


	private void check() {
		compressor.compress(indata, outdata, 0, 1);
		checkNoCompress();
	}


	private void checkNoCompress() {
		StringBuffer b = new StringBuffer();
		b.append("input = ");
		appendPoints(b, indata);
		b.append(", expected = ");
		appendPoints(b, expected);
		b.append(", actual = ");
		appendPoints(b, outdata);
		String msg = b.toString();
		DoubleData outx = outdata.getX();
		DoubleData outy = outdata.getY();
		DoubleData expectedx = expected.getX();
		DoubleData expectedy = expected.getY();
		int n = expectedx.getLength();
		assertEquals(msg, n, outx.getLength());
		for(int i = 0; i < n; i++) {
			assertEquals(msg + ", i = " + i, expectedx.get(i), outx.get(i));
			assertEquals(msg + ", i = " + i, expectedy.get(i), outy.get(i));
		}
	}


	private void appendPoints(StringBuffer b, PointData data) {
		b.append("[");
		DoubleData x = data.getX();
		DoubleData y = data.getY();
		int n = x.getLength();
		for(int i = 0; i < n; i++) {
			b.append("(");
			b.append(x.get(i));
			b.append(",");
			b.append(y.get(i));
			b.append(")");
			if(i < n - 1) {
				b.append(",");
			}
		}
		b.append("]");
	}
}
