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
package gov.nasa.arc.mct.fastplot.view;

/**
 * Controls a plot axis.
 * @author acrume
 */
public class Axis {
	private PinSupport pinSupport = new PinSupport() {
		protected void informPinned(boolean pinned) {
			Axis.this.informPinned(pinned);
		}
	};
	private boolean zoomed;


	/**
	 * Returns true if the axis is pinned.
	 * @return true if the axis is pinned
	 */
	public boolean isPinned() {
		return pinSupport.isPinned();
	}


	/**
	 * Called when the axis is pinned or unpinned.
	 * Do not call directly; this gets called from {@link Pin#informPinned(boolean)}.
	 * @param pinned true if it is pinned
	 */
	protected void informPinned(boolean pinned) {
	}


	/**
	 * Returns true if the axis is zoomed.  (True means that the zoom level is non-default.)
	 * @return true if the axis is zoomed
	 */
	public boolean isZoomed() {
		return zoomed;
	}


	/**
	 * Sets whether or not the axis is zoomed.  (True means that the zoom level is non-default.)
	 * @param zoomed true if the axis is zoomed
	 */
	public void setZoomed(boolean zoomed) {
		this.zoomed = zoomed;
	}


	/**
	 * Returns true if the axis is in a default state, i.e. not pinned and not zoomed.
	 * @return true if the axis is in a default state
	 */
	public boolean isInDefaultState() {
		return !pinSupport.isPinned() && !zoomed;
	}


	/**
	 * Creates a new pin for this axis.
	 * The axis is pinned if any of its pins are pinned.
	 * @return new pin
	 */
	public Pinnable createPin() {
		return pinSupport.createPin();
	}
}
