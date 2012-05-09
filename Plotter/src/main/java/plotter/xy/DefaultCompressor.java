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

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Map.Entry;

import plotter.DoubleData;
import plotter.internal.RangeSet;

/**
 * Default implementation of {@link Compressor}.
 * Optimized for mostly continuous data.
 * Works best when, on the small scale, the data is either non-increasing or non-decreasing.
 * @author Adam Crume
 */
public class DefaultCompressor implements Compressor {
	@Override
	public void compress(PointData input, PointData output, double offset, double scale) {
		assert !Double.isNaN(offset);
		assert !Double.isInfinite(offset);
		assert !Double.isNaN(scale);
		assert !Double.isInfinite(scale);
		assert scale != 0;
		DoubleData inx = input.getX();
		DoubleData iny = input.getY();
		int size = inx.getLength();
		if(size == 0) {
			return;
		}
		long bucket = (long) ((inx.get(0) - offset) / scale);
		double nextx = (bucket + 1) * scale + offset;
		int i = 0;
		double oldy = Double.NaN;
		RangeSet r = new RangeSet();
		long bucketSize = 0;
		double firsty = Double.NaN;
		while(true) {
			double x = inx.get(i);
			double y = iny.get(i);
			if(x >= nextx) {
				double bucketx = bucket * scale + offset;
				flushBucket(output, bucketx, firsty, r, oldy, bucketSize);
				r.clear();
				bucket = (int) ((x - offset) / scale);
				nextx = (bucket + 1) * scale + offset;
				bucketSize = 0;
				firsty = y;
				if(!Double.isNaN(y)) {
					r.add(y, y);
				}
			} else {
				if(bucketSize == 0) {
					firsty = y;
				}
				if(!Double.isNaN(y)) {
					if(Double.isNaN(oldy)) {
						r.add(y, y);
					} else {
						double min, max;
						if(oldy > y) {
							max = oldy;
							min = y;
						} else {
							max = y;
							min = oldy;
						}
						r.add(min, max);
					}
				}
			}
			oldy = y;
			i++;
			bucketSize++;
			if(i >= size) {
				double bucketx = bucket * scale + offset;
				flushBucket(output, bucketx, firsty, r, oldy, bucketSize);
				return;
			}
		}
	}


	private void flushBucket(CompressionOutput out, double bucketx, double firsty, RangeSet r, double lasty,
			long bucketSize) {
		out.add(bucketx, firsty);
		double prevy = firsty;
		NavigableMap<Double, Double> data2 = r.getData();
		if(data2 == null) {
			double min = r.getMin();
			double max = r.getMax();
			if(min != Double.POSITIVE_INFINITY) {
				boolean wroteMin = false;
				// Since we always draw the first and last, it would be redundant to draw the min or max if it is equal to either.
				if(!(min == firsty || min == lasty)) {
					out.add(bucketx, min);
					prevy = min;
					wroteMin = true;
				}
				if(!(max == firsty || max == lasty || (wroteMin && max == min))) {
					out.add(bucketx, max);
					prevy = max;
				}
			} else if(bucketSize > 1 && !Double.isNaN(lasty) && !Double.isNaN(firsty)) {
				out.add(bucketx, Double.NaN);
				prevy = Double.NaN;
			}
		} else {
			boolean first = true;
			NavigableMap<Double, Double> m = data2;
			if(firsty > lasty) {
				// If the line is descending (based on the first and last Y coordinates),
				// draw the segments in decreasing order.  This improves the odds that
				// we will be able to merge the first and last points into line segments.
				m = data2.descendingMap();
			}
			for(Iterator<Entry<Double, Double>> itr = m.entrySet().iterator(); itr.hasNext();) {
				Entry<Double, Double> e = itr.next();
				double min = e.getKey();
				double max = e.getValue();
				boolean last = !itr.hasNext();
				boolean wroteSomething = true;
				// Since RangeSet will not return a map if there is only one segment, the current segment cannot be first and last.
				if(first) {
					if(firsty > max || firsty < min) {
						out.add(bucketx, Double.NaN);
						prevy = Double.NaN;
					}
					boolean wroteMin = false;
					if(!(min == firsty)) {
						out.add(bucketx, min);
						prevy = min;
						wroteMin = true;
					}
					if(!(max == firsty || (wroteMin && min == max))) {
						out.add(bucketx, max);
						prevy = max;
					}
				} else if(last) {
					boolean wroteMin = false;
					if(!(min == lasty)) {
						out.add(bucketx, min);
						prevy = min;
						wroteMin = true;
					}
					if(!(max == lasty || (wroteMin && min == max))) {
						out.add(bucketx, max);
						prevy = max;
					}
				} else {
					if(min == max) {
						if(!(min == firsty || min == lasty)) {
							out.add(bucketx, min);
							prevy = min;
						} else {
							wroteSomething = false;
						}
					} else {
						out.add(bucketx, min);
						out.add(bucketx, max);
						prevy = max;
					}
				}
				if(wroteSomething) {
					if(!last || lasty < min || lasty > max) {
						out.add(bucketx, Double.NaN);
						prevy = Double.NaN;
					}
				}
				first = false;
			}
		}
		if(!equal(lasty, prevy)) {
			out.add(bucketx, lasty);
		}
	}


	private static boolean equal(double a, double b) {
		return a == b || (Double.isNaN(a) && Double.isNaN(b));
	}


	@Override
	public StreamingCompressor createStreamingCompressor(CompressionOutput output, double offset, double scale) {
		assert !Double.isNaN(offset) : "Offset cannot be NaN";
		assert !Double.isInfinite(offset) : "Offset cannot be infinite";
		assert !Double.isNaN(scale) : "Scale cannot be NaN";
		assert !Double.isInfinite(scale) : "Scale cannot be infinite";
		assert scale != 0 : "Scale cannot be zero";
		return new DefaultStreamingCompressor(output, offset, scale);
	}


	/**
	 * Default implementation of {@link Compressor.StreamingCompressor}.
	 * @author Adam Crume
	 */
	public class DefaultStreamingCompressor implements StreamingCompressor {
		private CompressionOutput out;

		private double oldy = Double.NaN;

		private RangeSet r = new RangeSet();

		private long bucketSize;

		private double nextx;

		private double firsty;

		private long bucket;

		private boolean first = true;

		private double offset;

		private double scale;

		private int tmpPoints;


		/**
		 * Creates a streaming compressor.
		 * @param out receives the compressed data
		 * @param offset compression offset
		 * @param scale compression scale
		 */
		public DefaultStreamingCompressor(CompressionOutput out, double offset, double scale) {
			this.out = out;
			this.offset = offset;
			this.scale = scale;
		}


		@Override
		public int add(double x, double y) {
			if(first) {
				bucket = (long) ((x - offset) / scale);
				nextx = (bucket + 1) * scale + offset;
				first = false;
			}
			out.removeLast(tmpPoints);
			int sizeBeforeModification = out.getPointCount();
			if(x >= nextx) {
				double bucketx = bucket * scale + offset;
				flushBucket(out, bucketx, firsty, r, oldy, bucketSize);
				r.clear();
				bucket = (int) ((x - offset) / scale);
				nextx = (bucket + 1) * scale + offset;
				bucketSize = 0;
				firsty = y;
				if(!Double.isNaN(y)) {
					r.add(y, y);
				}
			} else {
				if(bucketSize == 0) {
					firsty = y;
				}
				if(!Double.isNaN(y)) {
					if(Double.isNaN(oldy)) {
						r.add(y, y);
					} else {
						double min, max;
						if(oldy > y) {
							max = oldy;
							min = y;
						} else {
							max = y;
							min = oldy;
						}
						r.add(min, max);
					}
				}
			}
			oldy = y;
			bucketSize++;
			int sizeWithoutTempArea = out.getPointCount();
			double bucketx = bucket * scale + offset;
			flushBucket(out, bucketx, firsty, r, y, bucketSize);
			tmpPoints = out.getPointCount() - sizeWithoutTempArea;
			return out.getPointCount() - sizeBeforeModification;
		}
	}
}
