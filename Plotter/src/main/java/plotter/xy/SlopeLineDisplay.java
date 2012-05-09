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

import java.text.MessageFormat;

import javax.swing.JLabel;

/**
 * Displays the slope data from a {@link SlopeLine}.
 * @author Adam Crume
 */
public class SlopeLineDisplay extends JLabel implements SlopeLine.Listener {
	private static final long serialVersionUID = 1L;

	/** Formats the slope information.  The format is given three arguments: the X difference, the Y difference, and the slope. */
	private MessageFormat format = new MessageFormat("dx: {0}  dy: {1}");


	@Override
	public void slopeLineAdded(SlopeLine slopeLine, XYPlot plot, double startX, double startY) {
		// ignore
	}


	@Override
	public void slopeLineRemoved(SlopeLine line, XYPlot plot) {
		setText("");
	}


	@Override
	public void slopeLineUpdated(SlopeLine line, XYPlot plot, double startX, double startY, double endX, double endY) {
		if(startX == endX && startY == endY) {
			setText("");
		} else {
			double dx = endX - startX;
			double dy = endY - startY;
			setText(format.format(new Object[] { dx, dy, dy / dx }));
		}
	}


	/**
	 * Returns the format used to display the data.
	 * @return the format used to display the data
	 */
	public MessageFormat getFormat() {
		return format;
	}


	/**
	 * Sets the format used to display the data.
	 * The format is given three arguments: the X difference, the Y difference, and the slope.
	 * @param format the format used to display the data
	 */
	public void setFormat(MessageFormat format) {
		this.format = format;
	}
}
