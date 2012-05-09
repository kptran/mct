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
/**
 * ServiceLocator.java December 2009
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.services.serviceLocator;

/**
 * Implement the NRS lookup algorithm: If both host and port are set, then skip the NRS lookup
 * and return the host and port.  Else do NRS lookup and return result.
 */
public interface ServiceLocator {
	
    /**
     * Service locator results.
     */
	public class ServiceLocatorResult {
		
	    /** hostname. */
	    protected String host;
		
	    /** port number. */
	    protected int port;

		/**
		 * Get the host result.
		 * @return host - hostname.
		 */
		public String getHost() {
			return host;
		}

		/**
		 * Get the port result.
		 * @return port - number.
		 */
		public int getPort() {
			return port;
		}
	}
	
	/**
	 * Search for a service based on properties of service locator.
	 * @return true if search succeeds in setting both host and port.
	 */
	ServiceLocatorResult lookupHostAndPort();
	
}
