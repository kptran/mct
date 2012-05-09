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
package gov.nasa.arc.mct.graphics.brush;

import gov.nasa.arc.mct.graphics.state.StateSensitive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * A Fill draws its shape as a solid color.
 * 
 * @author vwoeltje
 *
 */
public class Fill extends Brush implements StateSensitive {

	private Color color;
	
	/**
	 * Constructs a brush which fills a shape with a specified color.
	 * @param c the color with which to fill
	 */
	public Fill(Color c) {
		color = c;
	}
	

	@Override
	protected void drawTransformed(Shape s, Graphics2D g) {
		Color oldColor = g.getColor();
		g.setColor(color);
		g.fill(s);
		g.setColor(oldColor);		
	}


	/**
	 * Gets the color associated with this fill
	 * @return the color that this brush uses to fill its shape
	 */
	public Color getColor() {
		return color;
	}


	/**
	 * Set the color with which this brush fills its shape
	 * @param color the color with which to fill
	 */
	public void setColor(Color color) {
		this.color = color;
	}


	@Override
	public void setState(Object state) {
		if (color instanceof StateSensitive) {
			((StateSensitive) color).setState(state);
		}
		//TODO: this might be a bad way to delegate this. Can the manifestation inform the color directly?
	}


	@Override
	public Object getState() {
		if (color instanceof StateSensitive) {
			return ((StateSensitive) color).getState();
		}
		return null;
	}
	
	@Override
	public void setInterval(Object minimum, Object maximum) {
		if (color instanceof StateSensitive) {
			((StateSensitive) color).setInterval(minimum, maximum);
		}
	}

}
