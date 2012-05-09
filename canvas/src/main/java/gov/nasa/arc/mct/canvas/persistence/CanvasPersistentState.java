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
package gov.nasa.arc.mct.canvas.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="extendedInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class CanvasPersistentState {
	private ViewRoleProperties viewRoleProperties = new ViewRoleProperties();
	
	public ViewRoleProperties getViewRoleProperties() {
		return viewRoleProperties;
	}
	
	public List<MCTViewManifestationInfo> getInfos() {
        return getViewRoleProperties().getEntries().getInfos();
    }
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ViewRoleProperties {
		private Entries entries = new Entries();
		
		public Entries getEntries() {
			return entries;
		}
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Entries {
		@XmlElement(name="ManifestInfo", type=MCTViewManifestationInfo.class)
		private List<MCTViewManifestationInfo> infos = new ArrayList<MCTViewManifestationInfo>();
		
		public List<MCTViewManifestationInfo> getInfos() {
			return infos;
		}
	}
}

