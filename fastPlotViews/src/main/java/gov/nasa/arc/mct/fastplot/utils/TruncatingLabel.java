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
package gov.nasa.arc.mct.fastplot.utils;

import gov.nasa.arc.mct.fastplot.bridge.PlotConstants;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;

import javax.swing.JLabel;

public class TruncatingLabel extends JLabel {

	private static final long serialVersionUID = 2217561217187839569L;

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (getParent().getWidth() < getWidth()) {
			int ellipseWidth;
			if (g instanceof Graphics2D) {
				TextLayout tl = new TextLayout(PlotConstants.LEGEND_ELIPSES, 
						g.getFont(), ((Graphics2D) g).getFontRenderContext());
				ellipseWidth = tl.getPixelBounds(null, 0, 0).width + 1;
			} else {
				ellipseWidth = 10;
			}
			g.setColor(getBackground());
			g.fillRect(getParent().getWidth()-ellipseWidth, 0, ellipseWidth, getHeight());
			g.setColor(getForeground());			
			g.drawString(PlotConstants.LEGEND_ELIPSES, getParent().getWidth()-ellipseWidth, 
					getHeight() - g.getFontMetrics().getDescent() - 1);
		}
	}

}
