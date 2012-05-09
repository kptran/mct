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

import java.util.HashSet;
import java.util.Set;

import plotter.DoubleData;
import plotter.xy.Compressor.StreamingCompressor;

/**
 * Implementation of an XY dataset that supports truncation and compression.
 * Note that the behavior is undefined if you manipulate the DoubleData buffers directly.
 * Points in the dataset must be stored in increasing X value.
 * @author Adam Crume
 */
public class CompressingXYDataset implements XYDataset {
	/** Plot line that displays the data. */
	private LinearXYPlotLine line;

	/** Holds the X data. */
	private DoubleData xData;

	/** Holds the Y data. */
	private DoubleData yData;

	/**
	 * Points under this value will be truncated.
	 * @see #truncationOffset
	 */
	private double truncationPoint = Double.NEGATIVE_INFINITY;

	/** When truncating, this many points will be kept that are under {@link #truncationPoint}. */
	private int truncationOffset = 1;

	/** Cached minimum X value of all points in the dataset. */
	private double minX = Double.POSITIVE_INFINITY;

	/** Cached maximum X value of all points in the dataset. */
	private double maxX = Double.NEGATIVE_INFINITY;

	/** Cached minimum Y value of all points in the dataset. */
	private double minY = Double.POSITIVE_INFINITY;

	/** Cached maximum Y value of all points in the dataset. */
	private double maxY = Double.NEGATIVE_INFINITY;

	/** Listeners that get notified if the X min or max changes.  May be null. */
	private Set<MinMaxChangeListener> xMinMaxListeners;

	/** Listeners that get notified if the Y min or max chagnes.  May be null. */
	private Set<MinMaxChangeListener> yMinMaxListeners;

	/** Value of {@link #minX} before a modification. */
	private double oldMinX;

	/** Value of {@link #maxX} before a modification. */
	private double oldMaxX;

	/** Value of {@link #minY} before a modification. */
	private double oldMinY;

	/** Value of {@link #maxX} before a modification. */
	private double oldMaxY;

	/** Performs the compression. */
	private Compressor compressor;

	/** Performs the streaming compression. */
	private StreamingCompressor streamingCompressor;

	/** Offset to use for compression. */
	private double compressionOffset;

	/** Scale to use for compression. */
	private double compressionScale;


	/**
	 * Creates a dataset.
	 * @param line line to plot the data
	 * @param compressor performs the compression
	 */
	public CompressingXYDataset(LinearXYPlotLine line, Compressor compressor) {
		if(line.getIndependentDimension() == null) {
			throw new IllegalArgumentException("Can't use CompressingXYDataset with a scatter plot");
		}
		this.line = line;
		this.compressor = compressor;
		xData = line.getXData();
		yData = line.getYData();
	}


	/**
	 * Adds a point, truncating the buffer if necessary.
	 * For the independent dimension, the coordinate must be must be greater than or equal to all other values in the dataset for that dimension. 
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	@Override
	public void add(double x, double y) {
		preMod();

		truncate();
		if(streamingCompressor == null) {
			XYDataset out;
			if(line.getIndependentDimension() == XYDimension.X) {
				out = line;
			} else {
				out = new XYReversingDataset(line);
			}
			streamingCompressor = compressor.createStreamingCompressor(out, compressionOffset, compressionScale);
		}
		int updateSize;
		if(line.getIndependentDimension() == XYDimension.X) {
			updateSize = streamingCompressor.add(x, y);
		} else {
			updateSize = streamingCompressor.add(y, x);
		}
		updateMinMax(x, y);

		postMod();
	}


	/**
	 * Called after a modification.
	 * Notifies any relevant listeners of changes.
	 */
	protected void postMod() {
		if(xMinMaxListeners != null) {
			if(minX != oldMinX || maxX != oldMaxX) {
				for(MinMaxChangeListener listener : xMinMaxListeners) {
					listener.minMaxChanged(this, XYDimension.X);
				}
			}
		}
		if(yMinMaxListeners != null) {
			if(minY != oldMinY || maxY != oldMaxY) {
				for(MinMaxChangeListener listener : yMinMaxListeners) {
					listener.minMaxChanged(this, XYDimension.Y);
				}
			}
		}
	}


	/**
	 * Called before a modification.
	 * Stores any state needed for {@link #postMod()}.
	 */
	protected void preMod() {
		oldMinX = minX;
		oldMaxX = maxX;
		oldMinY = minY;
		oldMaxY = maxY;
	}


	private void truncate() {
		DoubleData data = line.getIndependentDimension() == XYDimension.Y ? yData : xData;
		int length = data.getLength();
		// We assume here that not many points will be truncated, so a simple linear search is best.
		// If a large number of points may be truncated, a binary search may be better.
		int i = 0;
		while(i < length && data.get(i) < truncationPoint) {
			i++;
		}
		i -= truncationOffset;
		if(i > 0) {
			_removeFirst(i);
		}
	}


	/**
	 * Removes the first <code>removeCount</code> points from the dataset.
	 * Does not call {@link #preMod()} or {@link #postMod()}.
	 * @param removeCount number of points to remove
	 */
	private void _removeFirst(int removeCount) {
		// TODO: Use a more efficient method than rescanning the entire arrays.  For example, cache min/max values for subranges.
		int length = xData.getLength();
		boolean rescanX = false;
		boolean rescanY = false;
		for(int i = 0; i < removeCount; i++) {
			double xd = xData.get(i);
			double yd = yData.get(i);
			if(xd == minX || xd == maxX) {
				rescanX = true;
			}
			if(yd == minY || yd == maxY) {
				rescanY = true;
			}
		}
		XYDimension independentDimension = line.getIndependentDimension();
		if(rescanX) {
			if(independentDimension == XYDimension.X) {
				if(removeCount < length) {
					minX = xData.get(removeCount);
					maxX = xData.get(length - 1);
				} else {
					minX = Double.POSITIVE_INFINITY;
					maxX = Double.NEGATIVE_INFINITY;
				}
			} else {
				minX = Double.POSITIVE_INFINITY;
				maxX = Double.NEGATIVE_INFINITY;
				for(int i = removeCount; i < length; i++) {
					double xd = xData.get(i);
					if(xd > maxX) {
						maxX = xd;
					}
					if(xd < minX) {
						minX = xd;
					}
				}
			}
		}
		if(rescanY) {
			if(independentDimension == XYDimension.Y) {
				if(removeCount < length) {
					minY = yData.get(removeCount);
					maxY = yData.get(length - 1);
				} else {
					minY = Double.POSITIVE_INFINITY;
					maxY = Double.NEGATIVE_INFINITY;
				}
			} else {
				minY = Double.POSITIVE_INFINITY;
				maxY = Double.NEGATIVE_INFINITY;
				for(int i = removeCount; i < length; i++) {
					double yd = yData.get(i);
					if(yd > maxY) {
						maxY = yd;
					}
					if(yd < minY) {
						minY = yd;
					}
				}
			}
		}
		line.removeFirst(removeCount);
	}


	@Override
	public void prepend(double[] x, int xoff, double[] y, int yoff, int len) {
		preMod();

		PointData input = new PointData();
		XYDimension independentDimension = line.getIndependentDimension();
		if(independentDimension == XYDimension.X) {
			input.getX().add(x, xoff, len);
			input.getY().add(y, yoff, len);
		} else {
			input.getY().add(x, xoff, len);
			input.getX().add(y, yoff, len);
		}
		PointData output = new PointData();
		compressor.compress(input, output, compressionOffset, compressionScale);

		DoubleData outx;
		DoubleData outy;
		if(independentDimension == XYDimension.X) {
			outx = output.getX();
			outy = output.getY();
		} else {
			outy = output.getX();
			outx = output.getY();
		}
		for(int i = 0; i < len; i++) {
			updateMinMax(x[xoff + i], y[yoff + i]);
		}
		// TODO: Only add data that wouldn't be truncated
		line.prepend(outx, outy);

		postMod();
	}


	@Override
	public void prepend(DoubleData x, DoubleData y) {
		preMod();

		PointData input = new PointData(x, y);
		PointData output = new PointData();
		compressor.compress(input, output, compressionOffset, compressionScale);

		DoubleData outx;
		DoubleData outy;
		XYDimension independentDimension = line.getIndependentDimension();
		if(independentDimension == XYDimension.X) {
			outx = output.getX();
			outy = output.getY();
		} else {
			outy = output.getX();
			outx = output.getY();
		}
		int length = outx.getLength();
		for(int i = 0; i < length; i++) {
			updateMinMax(outx.get(i), outy.get(i));
		}
		// TODO: Only add data that wouldn't be truncated
		line.prepend(outx, outy);

		postMod();
	}


	/**
	 * Updates the min/max cache to include this point.
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private void updateMinMax(double x, double y) {
		if(x > maxX) {
			maxX = x;
		}
		if(x < minX) {
			minX = x;
		}
		if(y > maxY) {
			maxY = y;
		}
		if(y < minY) {
			minY = y;
		}
	}


	@Override
	public void removeAllPoints() {
		preMod();
		line.removeAllPoints();
		minX = Double.POSITIVE_INFINITY;
		maxX = Double.NEGATIVE_INFINITY;
		minY = Double.POSITIVE_INFINITY;
		maxY = Double.NEGATIVE_INFINITY;
		postMod();
	}


	/**
	 * Recompresses the existing data.
	 * This is useful if the compression scale has increased since data was added.
	 * Data may lose fidelity if compressed multiple times.
	 */
	public void recompress() {
		DoubleData newx =xData.clone();
		DoubleData newy =yData.clone();
		line.removeAllPoints();
		prepend(newx, newy);
	}


	@Override
	public int getPointCount() {
		return xData.getLength();
	}


	/**
	 * Returns the X data.
	 * @return the X data
	 */
	public DoubleData getXData() {
		return xData;
	}


	/**
	 * Sets the X data.
	 * @param xData the X data
	 */
	public void setXData(DoubleData xData) {
		this.xData = xData;
	}


	/**
	 * Returns the Y data.
	 * @return the Y data
	 */
	public DoubleData getYData() {
		return yData;
	}


	/**
	 * Sets the Y data.
	 * @param yData the Y data
	 */
	public void setYData(DoubleData yData) {
		this.yData = yData;
	}


	/**
	 * Returns the truncation point.
	 * @return the truncation point
	 */
	public double getTruncationPoint() {
		return truncationPoint;
	}


	/**
	 * Sets the truncation point.
	 * @param truncationPoint the truncation point
	 */
	public void setTruncationPoint(double truncationPoint) {
		this.truncationPoint = truncationPoint;
	}


	/**
	 * Returns the truncation offset.
	 * @return the truncation offset
	 */
	public int getTruncationOffset() {
		return truncationOffset;
	}


	/**
	 * Sets the truncation offset.
	 * @param truncationOffset the truncation offset
	 */
	public void setTruncationOffset(int truncationOffset) {
		this.truncationOffset = truncationOffset;
	}


	/**
	 * Returns the compression offset.
	 * @return the compression offset
	 */
	public double getCompressionOffset() {
		return compressionOffset;
	}


	/**
	 * Sets the compression offset.
	 * @param compressionOffset the compression offset
	 */
	public void setCompressionOffset(double compressionOffset) {
		assert !Double.isNaN(compressionOffset);
		assert !Double.isInfinite(compressionOffset);
		if(this.compressionOffset != compressionOffset) {
			this.compressionOffset = compressionOffset;
			streamingCompressor = null;
		}
	}


	/**
	 * Returns the compression scale.
	 * @return the compression scale
	 */
	public double getCompressionScale() {
		return compressionScale;
	}


	/**
	 * Sets the compression scale.
	 * @param compressionScale the compression scale
	 */
	public void setCompressionScale(double compressionScale) {
		assert !Double.isNaN(compressionScale) : "Scale cannot be NaN";
		assert !Double.isInfinite(compressionScale) : "Scale cannot be infinite";
		if(this.compressionScale != compressionScale) {
			this.compressionScale = compressionScale;
			streamingCompressor = null;
		}
	}


	/**
	 * Returns the minimum X value.
	 * @return the minimum X value
	 */
	public double getMinX() {
		return minX;
	}


	/**
	 * Returns the maximum X value.
	 * @return the maximum X value
	 */
	public double getMaxX() {
		return maxX;
	}


	/**
	 * Returns the minimum Y value.
	 * @return the minimum Y value
	 */
	public double getMinY() {
		return minY;
	}


	/**
	 * Returns the maximum Y value.
	 * @return the maximum Y value
	 */
	public double getMaxY() {
		return maxY;
	}


	/**
	 * Adds a listener for min/max changes to the X data.
	 * @param listener listener to add
	 */
	public void addXMinMaxChangeListener(MinMaxChangeListener listener) {
		if(xMinMaxListeners == null) {
			xMinMaxListeners = new HashSet<MinMaxChangeListener>();
		}
		xMinMaxListeners.add(listener);
	}


	/**
	 * Adds a listener for min/max changes to the Y data.
	 * @param listener listener to add
	 */
	public void addYMinMaxChangeListener(MinMaxChangeListener listener) {
		if(yMinMaxListeners == null) {
			yMinMaxListeners = new HashSet<MinMaxChangeListener>();
		}
		yMinMaxListeners.add(listener);
	}


	/**
	 * Removes all {@link MinMaxChangeListener}s for the Y data.
	 */
	public void removeAllYMinMaxChangeListeners() {
		yMinMaxListeners = null;
	}


	/**
	 * Removes all {@link MinMaxChangeListener}s for the X data.
	 */
	public void removeAllXMinMaxChangeListeners() {
		xMinMaxListeners = null;
	}


	/**
	 * Note that this may not behave as expected.
	 * In particular, removing as many points as you added will not work,
	 * because the added points may have been compressed into a smaller number.
	 */
	@Override
	public void removeLast(int count) {
		line.removeLast(count);
	}


	/**
	 * Listens to min/max changes.
	 * @author Adam Crume
	 */
	public interface MinMaxChangeListener {
		/**
		 * Notifies the listener that the min or max changed.
		 * @param dataset dataset containing the data
		 * @param dimension specifies whether this notification is for the X or Y data
		 */
		public void minMaxChanged(CompressingXYDataset dataset, XYDimension dimension);
	}
}
