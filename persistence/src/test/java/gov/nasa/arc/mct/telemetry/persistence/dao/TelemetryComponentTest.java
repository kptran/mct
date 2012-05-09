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
 * TelemetryComponentTest.java Sep 28, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.telemetry.persistence.dao;

import gov.nasa.arc.mct.persistence.strategy.AbstractDaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;

import java.io.Serializable;
import java.util.Set;

public class TelemetryComponentTest extends AbstractDaoObject {
	private String componentId;
	private String name;
	private String componentType;
	private TelemetryComponentTest parentComponent;
	private Set<TelemetryComponentTest> childComponents;
	
	@Override
	public Serializable getId() {
	    return componentId;
	}
	
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String id) {
		this.componentId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TelemetryComponentTest getParentComponent() {
		return parentComponent;
	}
	public void setParentComponent(TelemetryComponentTest parentComponent) {
		this.parentComponent = parentComponent;
	}
	public Set<TelemetryComponentTest> getChildComponents() {
		return childComponents;
	}
	public void setChildComponents(Set<TelemetryComponentTest> childComponents) {
		this.childComponents = childComponents;
	}
	public void addChildComponent(TelemetryComponentTest childComponent) {
		this.childComponents.add(childComponent);
		childComponent.setParentComponent(this);
	}
    public String getComponentType() {
        return componentType;
    }
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof TelemetryComponentTest)) { return false; }
        if (this == obj) { return true; }
        TelemetryComponentTest compObj = (TelemetryComponentTest)obj;
        return componentId.equals(compObj.componentId);
    }

    
    @Override
    public int hashCode() {
        return componentId.hashCode();
    }
    
    @Override
    public void lockDaoObject() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void unlockDaoObject() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void merge(DaoObject toObject) {
        //
    }

    @Override
    public void addTag(String tagId, String tagProperty) {
        throw new UnsupportedOperationException();
    }
}
