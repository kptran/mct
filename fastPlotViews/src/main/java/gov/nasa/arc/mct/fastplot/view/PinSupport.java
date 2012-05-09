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
 * Helper code for objects that support multiple pins.
 * When any of the pins are pinned, the whole object is pinned.
 * <br><br>
 * Sample usage:
 * <pre>
 * class MyThing {
 *   private PinSupport pinSupport = new PinSupport() {
 *     protected void informPinned(boolean pinned) {
 *       System.out.println("pinned = " + pinned);
 *     }
 *   }
 *   
 *   public Pinnable createPin() {
 *     return pinSupport.createPin();
 *   }
 *   
 *   public boolean isPinned() {
 *     return pinSupport.isPinned();
 *   }
 * }
 * </pre>
 * @author Adam Crume
 */
public class PinSupport {
	private int pinCount;


	/**
	 * Called whenever the overall pin state changes.
	 * The default implementation does nothing. 
	 * @param pinned true if the object is pinned
	 */
	protected void informPinned(boolean pinned) {
	}


	/**
	 * Returns true if any of the pins are pinned.
	 * @return true if the object is pinned
	 */
	public boolean isPinned() {
		return pinCount > 0;
	}


	/**
	 * Creates a new pin.
	 * @return new pin
	 */
	public Pinnable createPin() {
		return new Pin();
	}

	private class Pin implements Pinnable {
		private boolean pinned;


		public void setPinned(boolean pinned) {
			if(pinned && !this.pinned) {
				pinCount++;
				if(pinCount == 1) {
					informPinned(true);
				}
			} else if(!pinned && this.pinned) {
				pinCount--;
				if(pinCount == 0) {
					informPinned(false);
				}
			}
			this.pinned = pinned;
			assert pinCount >= 0;
		}


		public boolean isPinned() {
			return pinned;
		}


		@Override
		protected void finalize() {
			setPinned(false);
		}
	}
}
