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

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class RegularPolygonTest {

	@Test
	public void testRegularPolygon() {
		for (int sides = 3; sides < 12; sides++) {
			Polygon p = new RegularPolygon(sides);
			AssertJUnit.assertTrue(p.npoints == sides);
			
			// Find origin (should be average of points for regular poly)
			int ox = 0;
			int oy = 0;
			for (int pt = 0; pt < sides; pt++) {
				ox += p.xpoints[pt];
				oy += p.ypoints[pt];
			}
			ox /= sides;
			oy /= sides;
			
			// Check distances
			double dist[] = new double[sides];     // Distance from origin, squared 
			double length[]   = new double[sides]; // Length of edge, squared
			int dx, dy;
			for (int pt = 0; pt < sides; pt++) {
				dx = p.xpoints[pt] - ox;
				dy = p.ypoints[pt] - oy;
				dist[pt] = Math.sqrt((double) (dx*dx + dy*dy));
				dx = p.xpoints[(pt+1)%sides] - p.xpoints[pt];
				dy = p.ypoints[(pt+1)%sides] - p.ypoints[pt];
				length[pt] = Math.sqrt((double) (dx*dx + dy*dy));
				/* Assert that all edges are approximately equal, 
				 * and all distances from origin are approximately equal */
				AssertJUnit.assertTrue(Math.abs(dist[pt] - dist[0]) / dist[0] < 0.01  );
				AssertJUnit.assertTrue(Math.abs(length[pt] - length[0]) / length[0] < 0.01  );
			}
			
		}
	}

}
