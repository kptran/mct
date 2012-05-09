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

/**
 * Compresses data for display on a plot.
 * The idea is to create a smaller dataset which, when plotted, looks the same as the original.
 * Ideally, they would be identical, but small discrepancies (on the order of a few pixels) will often be allowed.
 * @author Adam Crume
 */
public interface Compressor {
	/**
	 * Compresses a set of data.
	 * Note that in the input and output, X is assumed to be the independent dimension.
	 * @param input data to compress
	 * @param output receives the compressed data
	 * @param offset logical coordinate of the first pixel
	 * @param scale width of a pixel, in logical units
	 */
	public void compress(PointData input, PointData output, double offset, double scale);


	/**
	 * Creates a streaming compressor.
	 * Note that in the output, X is assumed to be the independent dimension.
	 * @param output receives the compressed data
	 * @param offset logical coordinate of the first pixel
	 * @param scale width of a pixel, in logical units
	 * @return new streaming compressor
	 */
	public StreamingCompressor createStreamingCompressor(CompressionOutput output, double offset, double scale);


	/**
	 * Compresses streaming data.
	 * A varying number of points at the end of the output is considered work in progress.
	 * These points may be removed or modified by the {@link #add(double, double)} method, not just added.
	 * Modifying these points directly (or changing the size of the output) may result in undefined behavior.
	 */
	public interface StreamingCompressor {
		/**
		 * Adds a data point to the end.
		 * Returns the number of points at the end of the output that were added or modified.
		 * The size of the work in progress area is less than or equal to this number.
		 * Modifying the work in progress area directly (or changing the size of the output) may result in undefined behavior.
		 * @param independentValue coordinate of the point along the independent dimension
		 * @param dependentValue coordinate of the point along the dependent dimension
		 * @return number of points at the end of the output that were modified
		 */
		public int add(double independentValue, double dependentValue);
	}
}
