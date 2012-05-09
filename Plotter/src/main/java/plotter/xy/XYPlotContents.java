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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Contains the grid and plot lines.
 * @author Adam Crume
 */
public class XYPlotContents extends JComponent {
	private static final long serialVersionUID = 1L;

	@Override
	public void doLayout() {
		int width = getWidth();
		int height = getHeight();
		for(Component c : getComponents()) {
			c.setBounds(0, 0, width, height);
		}
	}


	/**
	 * Converts from physical to logical coordinates.
	 * @param dest modified to contain the logical coordinates
	 * @param src contains the physical coordinates
	 */
	public void toLogical(Point2D dest, Point src) {
		XYPlot plot = (XYPlot) getParent();
		Point loc = SwingUtilities.convertPoint(this, src, plot);
		plot.toLogical(dest, loc);
	}


	@Override
	public boolean isValidateRoot() {
		return true;
	}


	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
