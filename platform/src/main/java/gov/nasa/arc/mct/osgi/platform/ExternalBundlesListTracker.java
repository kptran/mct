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
package gov.nasa.arc.mct.osgi.platform;

import java.util.ArrayList;
import java.util.List;

class ExternalBundlesListTracker extends DirectoryBundlesListTracker {
	private static final String lastBundle = "mctLastBundleMarker";
	
	private String lastBundleLoc = null;
	private boolean lastBundleFound = false;
	
	@Override
	void addBundle(String bundleLoc) {
		if (bundleLoc.contains(lastBundle))	{
			lastBundleFound = true;
			lastBundleLoc = bundleLoc;
		} else {
			super.addBundle(bundleLoc);
		}
	}
	
	@Override
	List<String> getBundlesLocation() {
		if (lastBundleFound) {
			List<String> bundleLocations = new ArrayList<String>();
			if (super.getBundlesLocation() != null) {
				bundleLocations.addAll(super.getBundlesLocation());
			}
			bundleLocations.add(lastBundleLoc);
			return bundleLocations;
		}
		return null;
	}
}
