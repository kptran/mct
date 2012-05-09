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
package plotter;

/**
 * Specifies how something is rotated.
 * @author Adam Crume
 */
public enum Rotation {
	/** No rotation. */
	NONE(false),

	/** Clockwise 90 degrees. */
	CW(true),

	/** Counter clockwise 90 degrees. */
	CCW(true),

	/** 180 degrees, or a half rotation. */
	HALF(false);

	/** True if X and Y are switched. */
	private final boolean xySwitched;


	private Rotation(boolean xySwitched) {
		this.xySwitched = xySwitched;
	}


	/**
	 * Returns true if X and Y are switched.
	 * @return true if X and Y are switched
	 */
	public boolean isXYSwitched() {
		return xySwitched;
	}
}
