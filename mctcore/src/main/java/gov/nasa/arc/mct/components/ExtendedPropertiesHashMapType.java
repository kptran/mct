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
package gov.nasa.arc.mct.components;

import gov.nasa.arc.mct.gui.MCTViewManifestationInfoImpl;
import gov.nasa.arc.mct.util.LinkedHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
final class ExtendedPropertiesHashMapType {
    ViewRolePropertiesEntryType[] entries;
    
    public ExtendedPropertiesHashMapType() {
        super();
    }
    
    ExtendedPropertiesHashMapType(Map<String, Set<Object>> data) {
        this.entries = new ViewRolePropertiesEntryType[data.size()];
        int i=0;
        
        for (Map.Entry<String, Set<Object>> entry: data.entrySet()) {
            this.entries[i] = new ViewRolePropertiesEntryType();
            this.entries[i].key = entry.getKey();
            Set<Object> values = entry.getValue();
            this.entries[i].value = new Object[values.size()];
            int j=0;
            for (Object val: values) {
                this.entries[i].value[j++] = val;
            }
            i++;
        }
    }
    
    HashMap<String, Set<Object>> unMarshal() {
        HashMap<String, Set<Object>> returnValue = new HashMap<String, Set<Object>>();
        for (ViewRolePropertiesEntryType entry: entries) {
            LinkedHashSet<Object> valueList = new LinkedHashSet<Object>();
            for (Object obj: entry.value) {
                valueList.offerLast(obj);
            }
            returnValue.put(entry.key, valueList);
        }
        return returnValue;
    }

    private static final class ViewRolePropertiesEntryType {
        @XmlAttribute
        public String key;

        @XmlElements({
            @XmlElement(type=String.class),
            @XmlElement(name="ManifestInfo", type=MCTViewManifestationInfoImpl.class)
        })
        public Object[] value;
    }
}
