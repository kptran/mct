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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * A ConditionalBrush serves as a wrapper around an existing brush. It only draws 
 * when it receives a specific state.
 * 
 * @author vwoeltje
 *
 */
public class ConditionalBrush extends Brush implements StateSensitive {

	private Brush   delegate;
	private String  activationState = "";
	private boolean active          = false;
	
	/**
	 * Construct a brush which will draw another brush, but only when a  
	 * specified state is received.
	 * @param subFill the underlying brush to draw or hide
	 * @param state the state which will activate the drawing
	 */
	public ConditionalBrush(Brush subFill, String state) {
		activationState = state;
		delegate = subFill;
	}

	@Override
	public void setState(Object state) {
		if (state instanceof String && this.activationState.equals(state)) {
			active = true;
		} else {
			active = false;
		}
		if (delegate instanceof StateSensitive) {
			((StateSensitive)delegate).setState(state);
		}
	}

	@Override
	public Object getState() {
		return activationState;
	}
	
	@Override
	public void draw(Shape s, Graphics g, Rectangle bounds) {
		if (active) delegate.draw(s, g, bounds);
	}

	@Override
	public void setInterval(Object minimum, Object maximum) {
		/* Not relevant to a conditional brush */		
	}

	@Override
	protected void drawTransformed(Shape s, Graphics2D g) {
		/* Not used - deferred to delegate at draw */		
	}
}
