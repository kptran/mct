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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * A ScalingFill shrinks or expands the shape it draws, depending on the received 
 * state. (As state approaches the minimum, the size of the drawn shape approaches 
 * zero; as state approaches the maximum, the size of the drawn shape approaches 
 * the size of its bounds.)
 * 
 * @author vwoeltje
 *
 */
public class ScalingFill extends Fill implements StateSensitive {

	private double currentState = 0.0;
	private double minimumState = 0.0;
	private double maximumState = 100.0;

	/**
	 * Construct a brush which will fill a shape with a specified color, 
	 * but will shrink or expand the fill dependent upon state
	 * @param c the color with which to fill the shape
	 */
	public ScalingFill(Color c) {
		super(c);
	}

	@Override
	protected void drawTransformed(Shape s, Graphics2D g) {
		
		Rectangle b = s.getBounds();
		AffineTransform transform = new AffineTransform();
		
		int xmid = b.x + b.width / 2;
		int ymid = b.y + b.height / 2;
		
		transform.translate(xmid, ymid);
		transform.scale(currentState, currentState);
		transform.translate(-xmid, -ymid);	
				
		super.drawTransformed(transform.createTransformedShape(s), g);
	}	
	
	
	@Override
	public void setState(Object state) {
		if (state instanceof String) {
			try {
				currentState = (Double.parseDouble((String) state) - minimumState) / 
							   (maximumState                       - minimumState);
				if (currentState < 0.0) currentState = 0.0;
				if (currentState > 1.0) currentState = 1.0;
			} catch (NumberFormatException nfe) {
				
			}
		}
		super.setState(state);
	}

	@Override
	public Object getState() {
		return new Double(currentState);
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
