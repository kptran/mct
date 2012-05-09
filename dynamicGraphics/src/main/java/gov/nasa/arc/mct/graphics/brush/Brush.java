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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * A Brush is responsible for drawing shapes in some particular way. By drawing with 
 * different brushes in successive layers we arrive at a particular represenation of an object.
 * 
 * @author vwoeltje
 *
 */
public abstract class Brush {
	
	/**
	 * Draw a shape. By default, this transforms the shape to fit its bounds and then 
	 * invokes the brush's specific drawTransformed() behavior. This may be over-riden 
	 * by some brushes. 
	 * @param s the shape to draw
	 * @param g the graphics context
	 * @param bounds the bounds in which to draw the shape
	 */
	public void draw (Shape s, Graphics g, Rectangle bounds) {
		if (g instanceof Graphics2D) {			
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform transform = new AffineTransform();			
			
			Rectangle originalBounds = s.getBounds();

					transform.translate(bounds.x, bounds.y);
			transform.scale((double)bounds.width  / (double) originalBounds.width, 
					 (double)bounds.height / (double) originalBounds.height);
			transform.translate(-originalBounds.x, -originalBounds.y);
			
			drawTransformed(transform.createTransformedShape(s), g2);		
			
		}
	}
	
	/**
	 * Draw a shape within a graphics coordinate. We assume that when this is called 
	 * the shape's coordinates match with the desired drawing location in the graphics 
	 * context
	 * @param s the shape to draw
	 * @param g the graphics context
	 */
	protected abstract void drawTransformed (Shape s, Graphics2D g);
}
