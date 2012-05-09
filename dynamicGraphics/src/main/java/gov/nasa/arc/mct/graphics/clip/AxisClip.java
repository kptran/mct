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

import java.awt.Rectangle;

/**
 * An AxisClip 
 * 
 * @author vwoeltje
 *
 */
public class AxisClip extends Clip {
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int INCREASING = 0;
	public static final int DECREASING = 1;
	
	private Rectangle   clip = new Rectangle();
	
	private int         direction;
	private int         dimension;
	
	/**
	 * Construct a new axis clip along specified axes, with specified direction.
	 * @param dimension X_AXIS or Y_AXIS
	 * @param direction INCREASING (up, right) or DECREASING (down, left)
	 */
	public AxisClip (int dimension, int direction) {
		this.dimension = dimension % 2;
		this.direction = direction % 2;
		//TODO: Throw error on invalid dimension or direction?		
	}
	
	/**
	 * 
	 * @param bounds the area to fill (when value = 1.0)
	 * @param value  the fraction of the area which should be filled
	 */
	public void setClipToFit(Rectangle bounds, double value) {
		clip.setBounds(bounds);
		
		/* 
		 * Number the axes to treat all four directions of clip 
		 * consistently. Avoids nested if blocks
		 */		
		int loc[]       = new int[2];
		int dim[]       = new int[2];
		
		loc[X_AXIS]     = clip.x;
		dim[X_AXIS]     = clip.width;
		loc[Y_AXIS]     = clip.y;		    
		dim[Y_AXIS]     = clip.height;
		
		int newDim      = (int) (dim[dimension] * value);
		int decrease    = dim[dimension] - newDim;
		
		loc[dimension] += direction * decrease;
		dim[dimension]  =  newDim;
		
		clip.x          = loc[X_AXIS];
		clip.width      = dim[X_AXIS];
		clip.y          = loc[Y_AXIS];
		clip.height     = dim[Y_AXIS];	
		
		setClip(clip);
	}
}
