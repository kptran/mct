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
package gov.nasa.arc.mct.graphics.clip;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * A Clip is responsible for setting and restoring a user clip in a graphics context, 
 * before and after executing certain drawing commands.
 * @author vwoeltje
 *
 */
public abstract class Clip {
	private Shape clip;		
	
	/**
	 * Set the clip shape for this clip.
	 * @param s the shape to clip drawing actions with
	 */
	protected void setClip(Shape s) {
		clip = s;
	}
	
	/**
	 * Retrieve the clipping shape imposed by this object. This is independent 
	 * of any clips set within the graphics context.
	 * @return the shape of the clip used
	 */
	public    Shape getClip() {
		return clip;
	}
	
	/**
	 * Execute specific behavior in a graphics context, while enforcing this clip.
	 * @param r the behavior to execute (presumably drawing actions)
	 * @param g the graphics context
	 */
	public void doClipped(Runnable r, Graphics g) {
		Shape oldClip = g.getClip();
		Rectangle clipRect = g.getClipBounds();
		g.setClip(clip);
		if (clipRect != null)
			g.clipRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		
		r.run();
		
		g.setClip(oldClip);
	}
	

}
