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

import gov.nasa.arc.mct.graphics.clip.AxisClip;
import gov.nasa.arc.mct.graphics.state.StateSensitive;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * A clipped fill is only drawn up to a certain point, given the appearance of 
 * extending and retracting as state changes.
 * 
 * @author vwoeltje
 *
 */
public class ClippedFill extends Fill implements StateSensitive {

	private AxisClip clip = new AxisClip(AxisClip.X_AXIS, AxisClip.INCREASING);
	
	private double currentState = 0.0;
	private double minimumState = 0.0;
	private double maximumState = 100.0;	
	
	/**
	 * Construct a new Fill which will appear to extend or retract based on 
	 * changes in state.
	 * @param c the color with which to fill the shape
	 */
	public ClippedFill(Color c) {
		super(c);
	}
	
	/**
	 * Construct a new Fill which will appear to extend or retract based on 
	 * changes in state.
	 * @param c the color with which to fill the shape 
	 * @param axis the axis to fill on, from AxisClip's constants
	 * @param direction the direction to fill, from AxisClip's constants
	 */
	public ClippedFill(Color c, int axis, int direction) {
		this(c);
		clip = new AxisClip(axis, direction);		
	}
	
	@Override
	public void draw(final Shape s, final Graphics g, final Rectangle bounds) {
		if (currentState < 0.0) return;
		if (currentState > 1.0) {
			superDraw(s, g, bounds);
			return;
		}
		
		clip.setClipToFit(bounds, currentState);
		
		clip.doClipped(new Runnable() {
			public void run() {
				ClippedFill.this.superDraw(s, g, bounds);
			}
		}, g);
		
	}
	
	/**
	 * Calls super.draw - exposes this method to the anonymous Runnable in draw
	 * @param s
	 * @param g
	 * @param bounds
	 */
	private void superDraw(Shape s, Graphics g, Rectangle bounds) {
		super.draw(s, g, bounds);
	}	
	

	@Override
	public void setState(Object state) {
		if (state instanceof String) {
			try {
				currentState = (Double.parseDouble((String) state) - minimumState) / 
							   (maximumState                       - minimumState);
				if (currentState < 0.0) currentState = 0.0;
				if (currentState > 1.0) currentState = 1.0;
			} catch (NumberFormatException nfe) { // Ignore non-numbers
			}
		}
		super.setState(state);
	}

	@Override
	public Object getState() {
		return Double.valueOf(currentState);
	}

	@Override
	public void setInterval(Object minimum, Object maximum) {		
		super.setInterval(minimum, maximum);
		if (minimum instanceof Number && maximum instanceof Number) {
			minimumState = ((Number) minimum).doubleValue();
			maximumState = ((Number) maximum).doubleValue();
		}		
	}
}
