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
package gov.nasa.arc.mct.graphics.shape;

import java.awt.Polygon;

/**
 * A RegularPolygon has some number of sides, all of equal length.
 * Note that all vertices extend 1000 units away from the origin - this will 
 * probably need to be transformed if drawing in pixel space.
 * 
 * @author vwoeltje
 *
 */
public class RegularPolygon extends Polygon {
	private static final long serialVersionUID = -3310357736151267465L;

	/**
	 * Construct a regular polygon with the specified number of sides. The 
	 * first vertex is always located directly North.
	 * @param sides the number of sides 
	 */
	public RegularPolygon(int sides) {
		super();
		
		double delta = Math.PI * 2 / (double) sides;
		
		/* Note: Reduce upper bound by a half-step just to make sure we 
		 * don't get one extra point due to rounding. */
		for (double r = 0.0; r < Math.PI * 2 - (delta/2); r += delta) {
			this.addPoint((int) ( Math.sin(r) * 1000.0),
				          (int) (-Math.cos(r) * 1000.0));
		}
	}
}
